package processing.pacman;
import java.util.Map;
import processing.core.PApplet;

enum Mode{
	DARK, LIGHT;	
	public static int[] getBackground(Mode m) {
		return m == Mode.DARK ? black : white;
	}
	static int[] black = {255,255,255};
	static int[] white = {0,0,0};
}

public class Draw extends PApplet {
	private boolean choosePm = false, choosetheme = false, paus = false, countdown = false; 
	private int x = 900, y = 900, in = 80, r = 25, timer = 1, timer2 = 0, rectLength = 220, x1 = x/5, x2 = x/5*3, seconds = 0, secondsTimer = 0, minutes = 0;
	Mode m;
	Map<Integer, Dir> hm = Map.of(39, Dir.RIGHT, 37, Dir.LEFT, 38, Dir.UP, 40, Dir.DOWN);
	GameEngine ge = new GameEngine();

	public static void main(String[] args) {
		PApplet.runSketch(new String[]{""}, new Draw());
	}

	public void settings() {
		size(x,y);
		second();
	}
	
	public void setup() {
		noStroke();
		surface.setTitle("Pac-Man");
	}
	
	public void draw() {
		if(!choosePm || !choosetheme)
			chooseSettings();
		if(!ge.getWin() && choosePm && choosetheme && !paus && !ge.pacman.getDies()) {
			background(Mode.getBackground(m)[0], Mode.getBackground(m)[0], Mode.getBackground(m)[0]);
			ge.drawField(super.g, r,in);	
			ge.pacman.drawPacman(super.g, r, in);
			ge.ghosts.forEach(ghost -> ghost.drawGhost(super.g,r,in).checkCollision(ge.pacman).switcher());	
			if(!countdown)
				countdown();
			else {	 
			drawMouth();
			ghostsgo();
			drawtext();	
			drawClock();
			}
			ButtonSettings();
			ButtonReset();
		}
		if(ge.getWin())
			win();
		if(paus) 
			paus();	
		if(ge.pacman.getDies())
			pacmanDies();
	}
	
	private void countdown() {
		textSize(90);
		fill(m == Mode.DARK ? 0 : 255);
		if(timer2 < 100) 
			text("READY", in+220, in+350);	
		if(timer2 >= 100 && timer2 < 200) 
			text("GO", in+220, in+350);
		timer2++;
		if(timer2 > 200) {
			timer2 = 0;
			countdown = true;		
		}
	}
	
	private void pacmanDies() {
		if(timer < 200) {
			fill(ge.pacman.getPm() ==PacmanMode.YELLOW ? 255 : 0, ge.pacman.getPm() ==PacmanMode.YELLOW ? 255 : 0, ge.pacman.getPm() ==PacmanMode.YELLOW ? 0 : 255);
			ellipse(x/2,y/2,220,220);
			fill(255);
			ellipse(x/2, y/2+50, 50,40);
			fill(0);
			textSize(90);
			text("X", x/2-60, y/2-20);
			text("X", x/2+10, y/2-20);
			timer++;
		}
		if(timer == 200) {
			countdown = false;
			ge.pacman.setDies(false);
			timer = 0;		
		}	
	}
		
	private void drawMouth() {
		fill(Mode.getBackground(m)[0], Mode.getBackground(m)[0], Mode.getBackground(m)[0]);
			if(second() % 2 == 0)
				ge.pacman.drawMouth(super.g, r, in);
	}
	
	private void drawtext() {
		fill(255,0,0);
		textSize(40);
		text("level: " + ge.getLevel(),150,850);
		text("score: " + ge.getScore(), 350, 850);	
		text("lifes: ", 150, 890);	
		for(int i = 0; i < ge.pacman.getLifes(); i++) {
			fill(ge.pacman.getPm() == PacmanMode.YELLOW ? 255 : 0, ge.pacman.getPm() == PacmanMode.YELLOW ? 255 : 0, ge.pacman.getPm() == PacmanMode.YELLOW ? 0 : 255);
			text("X", 270+(i*40), 890);	
		}
	}
	
	private void drawClock() {
		fill(255,154,0);
		textSize(25);
		if(minutes < 10)
			text("0" + minutes, 760,850);
		else
			text(minutes, 760, 850);
		if(seconds < 10)
			text(":0" + seconds, 790,850);
		else 
			text(":" + seconds,790,850);
		if (secondsTimer % 60 == 0) 
			seconds = seconds + 1;	
		if (seconds ==59) {
			minutes = minutes + 1;
			seconds = 0;
		}
		secondsTimer = secondsTimer + 1;
	}
	
	private void ghostsgo() {
		if(millis()%10 == 0)
			ge.ghosts.forEach(ghost -> ghost.go(ge.pacman));
	}
	
	private void chooseSettings() {
		background(50,50,50); 		//Background
		textSize(40);				//Text
		fill(255,0,0);
		text("choose your theme", x/2-120, y/2+150);
		text("choose your player", x/2-120, 100);
		//rectangle Dark/Light
		fill(255);
		rect(x1,y-270,rectLength,rectLength);
		fill(0);
		rect(x2,y-270,rectLength,rectLength);	
		rect(x1,y/2-320,rectLength,rectLength);
		rect(x2,y/2-320,rectLength,rectLength);
		
		fill(0,!choosetheme && mouseX > x1 && mouseX < x1+rectLength && mouseY > y-270 && mouseY < y-270+rectLength ? 255 : 0 ,0);
		text("LIGHT", x1+30, y-150);
		fill(!choosetheme && mouseX > x2 && mouseX < x2+rectLength && mouseY > y-270 && mouseY < y-270+rectLength ? 0 : 255, 255, 	!choosetheme && mouseX > x2 && mouseX < x2+200 && mouseY > y-270 && mouseY < y-270+rectLength ? 0 : 255 );
		text("DARK", (x2)+30, y-150);	
		fill(255);
		if(choosePm) {
			fill(0,255,0);
			rect(ge.pacman.getPm() == PacmanMode.YELLOW ? x/ 5 : x2,y/2-320,rectLength,rectLength);
		} 
		
		//pacman yellow
		fill(255,255,0);
		ellipse(x1+110,y/2-300+90,180,180);
		//pacman blue
		fill(0,0,255);
		ellipse(x2+100,y/2-300+90,180,180);
		//eye yellow
		fill(255);
		ellipse(x2+50,y/2-300+10,40,40);
		ellipse(x2+80,y/2-300+10,40,40);
		//eye blue
		fill(0);
		ellipse(x2+50,y/2-300+10,10,10);
		ellipse(x2+80,y/2-300+10,10,10);
		ellipse(x1+90,y/2-300+40,20,20);
		//Mouth
		if(!choosePm) {
		fill(0);	
		triangle(x2+100,y/2-300+90,x2+190,y/2-300+180,x2+190,y/2-300);
		triangle(x1+100,y/2-300+90,x1+rectLength,y/2-300+180,x1+rectLength,y/2-300);
		}
		//Mouth if choosen
		if(ge.pacman.getPm() == PacmanMode.BLUE && choosePm) {
			fill(0,255,0);
			triangle(x2+100,y/2-300+90,x2+190,y/2-300+180,x2+190,y/2-300);
			fill(0);
			triangle(x1+100,y/2-300+90, x1+rectLength,y/2-300+180,x1+rectLength,y/2-300);		
		}
		if(ge.pacman.getPm() == PacmanMode.YELLOW && choosePm) {
			fill(0,255,0);
			triangle(x1+100,y/2-300+90, x1+rectLength,y/2-300+180,x1+rectLength,y/2-300);
			fill(0);
			triangle(x2+100,y/2-300+90,x2+190,y/2-300+180,x2+190,y/2-300);
		}
	}
	
	public void mouseClicked() {
		if(!choosePm && mouseX > x1 && mouseX < x1+rectLength && mouseY > y/2-320 && mouseY < y/2-320+rectLength) {
			ge.pacman.setPm(PacmanMode.YELLOW);
			choosePm = true;
		}
		if(!choosePm && mouseX > x2 && mouseX < x2+200 && mouseY > y/2-320 && mouseY < y/2-320+rectLength) {
			ge.pacman.setPm(PacmanMode.BLUE);
			choosePm = true;
		}
		if(!choosetheme && choosePm && mouseX > x1+110 && mouseX < x1+rectLength && mouseY > y-270 && mouseY < y-270+rectLength) {
			m = Mode.DARK;
			choosetheme = true;
		}
		if(!choosetheme && choosePm && mouseX > x2 && mouseX < x2+200 && mouseY > y-270 && mouseY < y-270+rectLength) {
			m = Mode.LIGHT;
			choosetheme = true;
		}
		if(choosePm && choosetheme && mouseX > x-150 && mouseX < x-30 && mouseY > 200 && mouseY < 280) {
			choosePm = false;
			choosetheme = false;
		}
		if(choosePm && choosetheme && mouseX > x-150 && mouseX < x-30 && mouseY > 100 && mouseY < 180) 
			ge.reset(4);	
		}
	
	public void keyPressed() {
		if(keyCode > 36 && keyCode < 41 && choosePm && choosetheme && !ge.getWin() && !paus && countdown) {
			ge.tunnel(ge.pacman, ge.pacman.getR(), ge.pacman.getC(),hm.get(keyCode));
			ge.pacman.go(hm.get(keyCode));
		}
		if(keyCode == 32 && choosePm && choosetheme && !ge.getWin() && countdown) 
			paus = !paus;
		if(keyCode == 10 && ge.getWin()) {
			countdown = false;
			ge.reset(1);
		}
	}
	
	private void paus() {
		textSize(40);
		fill(m == Mode.DARK ? 0 : 255);
		text("Press spacebar to continue", in+170, in+350);
	}
	
	public void win() {
		fill(0,255,20);
		rect(in,in,800,700);
		textSize(40);
		fill(255);
		text("YOU WIN!", in+200, in+280);
		text("PRESS ENTER TO CONTINUE", in+100, in+350);	
	}
	
	public void ButtonReset() {
		fill(m == Mode.DARK ? 0 : 255);
		rect(x-150, 100, 120, 80);
		textSize(20);
		fill(m == Mode.DARK ? 255 : 0);
		text("Reset", x-130, 140); 	
	}
	
	public void ButtonSettings() {
		fill(m == Mode.DARK ? 0 : 255);
		rect(x-150, 200, 120, 80);
		textSize(20);
		fill(m == Mode.DARK ? 255 : 0);
		text("Settings", x-130, 240); 	
	}	
}