import static org.junit.Assert.*;

import org.junit.Test;

import processing.pacman.GameEngine.Ghost;
import processing.pacman.GameEngine.Pacman;

public class Test {
	GameEngine ge = new GameEngine(); 
	Ghost testGhost = ge.ghosts.get(0);
	Pacman pacman = ge.pacman;
	
	@Test
	public void testGameEngine() {
		assertEquals("default score", ge.getScore(), 470);
		ge.setScore(458); //decrement by one
		assert ge.getScore() != 461 : "decrement by one";
		assert ge.getScore() == 458 : "Value set";
		assertEquals("getFieldValue 0/0", ge.getFieldValue(0, 0), 0);
		assertEquals("default level", ge.getLevel(), 1);
		assertNotNull("pacman is null", ge.pacman);
		assertEquals("4 ghosts must be present", ge.ghosts.toArray().length,4);
		ge.ghosts.stream().forEach(ghost -> assertNotNull("ghosts are null", ghost));
		assertFalse("win is true" , ge.getWin());
		ge.setFieldValue(1, 4, 4);
		assert ge.getFieldValue(1, 4) == 4 : "get FieldValue is not 4";
	}
	
	@Test
	public void testGameEngineReset() {
		ge.reset(1); //1 = win 
		assertEquals("pacman row not reset", ge.pacman.getR(), 26);
		assertEquals("pacman column not reset", ge.pacman.getC(), 12);
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost row not reset", ghost.getR(), 15));
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost column not reset", ghost.getC(), 8));
		assert ge.getScore() == 461 : "score not reset";
		assertEquals("level not incremented", ge.getLevel(), 2);
		
		ge.reset(2); //2 = LoseLife
		assertEquals("pacman row not reset", ge.pacman.getR(), 26);
		assertEquals("pacman column not reset", ge.pacman.getC(), 12);
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost row not reset", ghost.getR(), 15));
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost column not reset", ghost.getC(), 8));
		assert ge.getLevel() == 2 : "level changes";
		assertTrue("score blanks", ge.getScore() == 461);
		
		ge.reset(3); //3 = LoseAll
		assertEquals("pacman row not reset", ge.pacman.getR(), 26);
		assertEquals("pacman column not reset", ge.pacman.getC(), 12);
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost row not reset", ghost.getR(), 15));
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost column not reset", ghost.getC(), 8));
		assert ge.getLevel() == 1 : "Level not reset";
		assertTrue("score reset" , ge.getScore() == 461);
		
		ge.reset(4); //4 = HardReset
		assertEquals("pacman row not reset", ge.pacman.getR(), 26);
		assertEquals("pacman column not reset", ge.pacman.getC(), 12);
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost row not reset", ghost.getR(), 15));
		ge.ghosts.stream().forEach(ghost -> assertEquals("ghost column not reset", ghost.getC(), 8));
		assert ge.getLevel() == 1 : "level not reset";
		assertTrue("score reset" , ge.getScore() == 461);
	}

	@Test
	public void testGhost() {
			testGhost.setR(1).setC(1);
			for(int i = 0; i < 20; i++) {
				try {
					testGhost.go(ge.pacman); //no ArrayOutOfBounds
				}catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Array out of Bounds");
				}
			}
				testGhost.setR(1).setC(1);
				assert testGhost.getR() == 1 : "from setRow";
				assert testGhost.getC() == 1 : "from setColumn";
				ge.pacman.setR(1).setC(1);
				testGhost.checkCollision(ge.pacman);
				assertTrue("pacman is not dead", ge.pacman.getDies());	
	}

	@Test
	public void testPacman() {
		pacman.setR(1).setC(1);
		pacman.go(Dir.LEFT);
		assertEquals("pacman can go in the wall", pacman.getC(), 1);
		assertEquals("pacman can go in the wall", pacman.getR(), 1);
		pacman.go(Dir.RIGHT);
		assertEquals("pacman can't go a good way", pacman.getC(), 2);
		assertEquals("pacman can't go a good way", pacman.getR(), 1);
		int score = ge.getScore();
		pacman.go(Dir.RIGHT);
		assertEquals("score was not incremented", ge.getScore() ,score-1);
		pacman.go(Dir.LEFT);
		assertEquals("score was incremented", ge.getScore() ,score-1);
		pacman.setDies(true); 
		assertTrue("pacman not dead", pacman.getDies());
		pacman.setDies(false);
		assertFalse("pacman dies", pacman.getDies());
	}
}
