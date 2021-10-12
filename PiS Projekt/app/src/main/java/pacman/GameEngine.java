package processing.pacman;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import processing.core.PGraphics;

enum PacmanMode {
BLUE, YELLOW;
	public static int[] getColor(PacmanMode pm) {
		return pm == PacmanMode.BLUE ? blue : yellow;
	}
	static int[] blue = {0,0,255};
	static int[] yellow = {255,255,0};	
}

enum Dir {
	RIGHT,LEFT, UP, DOWN;
	public static List<Integer> getLoc(Dir d) {		
		return Map.of(RIGHT, List.of(0,1), LEFT, List.of(0,-1), UP,List.of(-1,0), DOWN, List.of(1,0)).get(d);
	}		
}
enum GhostMode{
	INVISBLE, NORMAL;
}

public class GameEngine {
	private int score = 470, level = 1;
	final Pacman pacman; 	
	List<Ghost> ghosts = new ArrayList<>();
	private int[][] field =  {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,3,3,3,3,3,3,3,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,3,0,0,0,3,0,0,0,0,3,3,0,3,3,0,0,0,0,3,0,0,0,3,0},
			{0,3,0,0,0,3,0,0,0,0,3,3,0,3,3,0,0,0,0,3,0,0,0,3,0},
			{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,3,3,0,0,3,0,3,3,3,3,0,0,0,3,3,3,0,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,0,3,3,3,3,3,0,3,3,3,3,0,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,0,0,0,3,3,3,0,3,3,0,0,0,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,0,3,3,3,3,3,3,0},
			{0,0,0,3,3,3,0,3,3,3,3,3,3,3,3,3,3,0,3,3,3,3,0,0,0},
			{0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0},
			{6,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,6},
			{0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0},
			{0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0},
			{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,0,2,2,2,2,2,2,2,2,2,0,3,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0},
			{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,3,3,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,3,3,0},
			{0,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,3,3,3,0},
			{0,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,3,3,3,0},
			{0,3,3,3,3,0,3,3,0,3,3,3,3,3,3,3,3,3,3,3,0,3,3,3,0},
			{0,3,3,3,3,3,3,3,0,3,3,3,3,3,3,3,3,0,3,3,3,3,3,3,0},
			{0,3,3,3,3,0,0,0,0,3,3,0,3,3,3,3,3,0,0,0,0,0,0,3,0},
			{0,3,3,3,3,3,3,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,3,3,3,3,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,3,3,3,3,0},
			{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	};
	final int ROWS = field.length, COLUMNS = field[0].length;

	GameEngine() {
		pacman = new Pacman(); 
		ghosts = List.of(new Ghost(4, List.of(255,0,0)), new Ghost(-3,List.of(200,6,152)), new Ghost(-1,List.of(42,220,152)), new Ghost(3,List.of(204,216,10)));
		setBigFood();
	}
	public int getScore() {
		return score;
	}
	public int getFieldValue(int row, int column) {
		return field[row][column];
	}
	public boolean getWin() {
		return score == 0;
	}
	public int getLevel() {
		return level;
	}		
	public void setFieldValue(int row, int column, int val) {
		field[row][column] = val;
	}
	public void setScore(int score) {
		this.score = score;
	}
	private void setBigFood() {
		int bigPoints = 9-(level*3);
		int act = 0;
		while(act < bigPoints) {
			int row = new Random().nextInt(ROWS-1);
			int column = new Random().nextInt(COLUMNS-1);
			if(field[row = new Random().nextInt(ROWS-1)][column] == 3) {
				field[row][column] = 4;
				act++;
			}			
		}		
	}
	public void drawField(PGraphics g, int ra, int in) {
		for(int r = 0; r < ROWS; r++) 
			for(int c = 0; c < COLUMNS;c++)
				switch(field[r][c]) {
				case 0: 
					g.fill(0,0,255);
					g.rect(in+(c*ra), in+(r*ra), ra,ra);
					break;
				case 2: 
					g.fill(255,97,71);
					g.rect(in+(c*ra), in+(r*ra), ra,ra);
					break;
				case 3:
					g.fill(255,255,0);
					g.ellipse(in+(c*ra)+ra/2, in+(r*ra)+ra/2, ra/3,ra/3);
					break;
				case 4: 
					g.fill(255,255,0);
					g.ellipse(in+(c*ra)+ra/2, in+(r*ra)+ra/2, (float) (ra/1.5),(float) (ra/1.5));	
					break;
				}	
	}
	
	public void tunnel(Pacman pm, int row, int column, Dir dir) {
		if(field[row][column+1] == 6 && dir == Dir.RIGHT)
			pm.setC(0);
		if(field[row][column-1] == 6 && dir == Dir.LEFT)
			pm.setC(COLUMNS-1);
	}
	
	public void reset(int reason) {
		pacman.setR(26).setC(12);  
		ghosts.forEach(ghost -> {ghost.setR(15).setC(8);});	
		if(reason != 2) {
			for(int r = 0; r < ROWS; r++) 
				for(int c = 0; c < COLUMNS;c++) 
					if(field[r][c] == 4 || field[r][c] == 5)
						field[r][c] = 3;
			setBigFood();
			score = 470;	
		}
		level = reason == 1 ? level + 1 : level; 
		level = reason == 3 ? 1 : level;
	}	

	class Ghost {
		private List<Integer> rgb; 
		private int row = 15, column = 8, r, lastrow=15, lastcolumn=8,timer=1,frequency=0; 
		private GhostMode gm = GhostMode.NORMAL;
		
		public Ghost(int r, List<Integer> list) {
			rgb = list;
			this.r = r;
		}
		public int getR() {
			return row;
		}
		public int getC() {
			return column;
		}
		public Ghost setR(int r) {
			row = r;
			return this;
		}
		public Ghost setC(int c) {
			column = c;
			return this;
		}
		public Ghost drawGhost(PGraphics g, int ra, int in) {
			g.fill(gm == GhostMode.NORMAL ? rgb.get(0) : 70, gm == GhostMode.NORMAL ? rgb.get(1) : 70, gm == GhostMode.NORMAL ? rgb.get(2) : 70);
			g.ellipse(in+(column*ra)+ra/2, in+(row*ra)+ra/2, ra,ra);
			g.rect(in+(column*ra), in+(row*ra)+ra/2, ra, ra/2);
			g.fill(255);
			g.ellipse(in+(column*ra)+ra/4, in+(row*ra)+ra/3, ra/4,ra/4);
			g.ellipse(in+(column*ra)+ra/1.5f, in+(row*ra)+ra/3, ra/4,ra/4);
			g.fill(0);
			g.ellipse(in+(column*ra)+ra/4, in+(row*ra)+ra/3, ra/12,ra/12);
			g.ellipse(in+(column*ra)+ra/1.5f, in+(row*ra)+ra/3, ra/12,ra/12);
			return this;
		}
		
		public void go(Pacman pm) {
			if(new Random().nextBoolean())
				return;
			int[] last = {row,column};
			List<Dir> dirList = Arrays.asList(Dir.values());
			ArrayList<Integer> l = new ArrayList<>();
			while(l.size() < 4)
				l.add(gm != GhostMode.INVISBLE ? 99 : -99);
			Predicate<Integer> greaterthan = v -> (v > 1 && v != 6);
			BiPredicate<Integer, Integer> notTheLastMove = (a,b) -> (a != lastrow || b != lastcolumn); 
			for(int i = 0; i < 4; i++) 
				if(greaterthan.test(getFieldValue(row+Dir.getLoc(dirList.get(i)).get(0), column+Dir.getLoc(dirList.get(i)).get(1))) 
						&& notTheLastMove.test(row+Dir.getLoc(dirList.get(i)).get(0), column+Dir.getLoc(dirList.get(i)).get(1))) 
					l.set(i, Math.abs(row+Dir.getLoc(dirList.get(i)).get(0)-pm.row+r) + Math.abs(column+Dir.getLoc(dirList.get(i)).get(1)-pm.column+r));		
				
				switch(gm == GhostMode.NORMAL ? l.indexOf(Collections.min(l)) : l.indexOf(Collections.max(l))) {
				case 0: 
					column = column < COLUMNS-2 ? column + 1 : column;
					break;
				case 1: 
					column = column > 0 ? column - 1 : column;
					break;
				case 2: 
					row = row > 0 ? row - 1 : row;
					break;
				case 3:
					row = row < ROWS-2? row + 1 : row;
					break;
				}				
				lastrow = last[0];
				lastcolumn = last[1];			
		}
		
		public void switcher() {
			if(gm == GhostMode.INVISBLE) {
				gm = timer % (frequency*500) == 0 ? GhostMode.NORMAL : GhostMode.INVISBLE;	
				timer++;
			}
		}
		
		public Ghost checkCollision(Pacman pm) {
			if(row == pm.row && column == pm.column && gm == GhostMode.NORMAL) 
				pm.die();
			return this;
		}		
	}
	
	class Pacman{
		private int lifes = 3, row = 26, column = 12;
		private PacmanMode pm;
		private boolean lookright = true, dies = false;
		
		public int getR() {
			return row;
		}
		public int getC() {
			return column;
		}
		public PacmanMode getPm() {
			return pm;
		}
		public int getLifes() {
			return lifes;
		}
		public boolean getDies() {
			return dies;
		} 
		public Pacman setR(int r) {
			row = r;
			return this;
		}
		public Pacman setC(int c) {
			column = c;
			return this;
		}
		public void setPm(PacmanMode pm) {
			this.pm = pm;
		}
		public void setDies(boolean dies) {
			this.dies = dies;
		}
		public Pacman drawPacman(PGraphics g, int ra, int in) { 
			g.fill(PacmanMode.getColor(pm)[0], PacmanMode.getColor(pm)[1], PacmanMode.getColor(pm)[2]);
			g.ellipse(in+(column*ra)+ra/2, in+(row*ra)+ra/2, ra,ra);
			if(pm == PacmanMode.YELLOW) {
				g.fill(0);
				g.ellipse(in+(column*ra)+ra/2, in+(row*ra)+ra/5, ra/5,ra/5);
			}
			if(pm == PacmanMode.BLUE) {
				g.fill(255);
				g.ellipse(in+(column*ra)+ra/2, in+(row*ra), ra/2.7f,ra/2.7f);
				g.fill(0);
				g.ellipse(in+(column*ra)+ra/2, in+(row*ra), ra/6.25f,ra/6.25f);
			}
			return this;
		}
		
		public Pacman drawMouth(PGraphics g, int ra, int in) {
			float right = lookright == true ? (ra/2)*1.1f : -ra/2*1.1f;
			g.triangle(in+(column*ra)+ra/2, in+(row*ra)+ra/2, in+(column*ra)+ra/2+right, in+(row*ra)+ra/2-ra/2, in+(column*ra)+ra/2+right, in+(row*ra)+ra/2+ra/2);			
			return this;
		}
		

		public void go(Dir dir) {
			lookright = dir == Dir.LEFT ? false : true;
			if(field[row+Dir.getLoc(dir).get(0)][column+Dir.getLoc(dir).get(1)] > 0 && field[row+Dir.getLoc(dir).get(0)][column+Dir.getLoc(dir).get(1)] != 2)  
				setR(row + Dir.getLoc(dir).get(0)).setC(column + Dir.getLoc(dir).get(1)).eat();	
		}
					
		private void eat() {
			if(field[row][column] == 3 || field[row][column] == 4) {
				setScore(getScore()-1);
				if (field[row][column] == 4) 
					ghosts.forEach(ghost -> {
						ghost.gm = GhostMode.INVISBLE;
						ghost.frequency = ghost.frequency+1;
					});				
				setFieldValue(row, column, 5);
			}
		}
		public void die() {
			dies = true;
			if(lifes > 0) {
				lifes = lifes-1;
				reset(2);
				return;
			}
			if(lifes == 0) 
				reset(3);	
		}		
	}
}