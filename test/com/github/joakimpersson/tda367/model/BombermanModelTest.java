package com.github.joakimpersson.tda367.model;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.joakimpersson.tda367.model.constants.Parameters;
import com.github.joakimpersson.tda367.model.player.Player;
import com.github.joakimpersson.tda367.model.tiles.Tile;
import com.github.joakimpersson.tda367.model.tiles.nonwalkable.*;
import com.github.joakimpersson.tda367.model.tiles.walkable.*;
import com.github.joakimpersson.tda367.model.utils.Position;

public class BombermanModelTest {

	private IBombermanModel model;

	@Before
	public void setUp() throws Exception {
		model = BombermanModel.getInstance();
		List<Player> players = new ArrayList<Player>();
		Dimension mapD = Parameters.INSTANCE.getMapSize();
		players.add(new Player(1,"testPlayer1", new Position(1, 1)));
		players.add(new Player(2,"testPlayer2", new Position(
				(int)(mapD.getWidth()) -2, (int)(mapD.getHeight()) -2)));
		model.startGame(players);
	}

	@Test
	public void testIsRoundOver() {
		
		boolean test1;
		boolean test2;
		boolean test3;
		
		test1 = model.isRoundOver();
		
		List<Player> playerList = model.getPlayers();
		for(Player p : playerList) {
			p.playerHit();
			p.removeImmortality();
			p.playerHit();
		}
		test2 = model.isRoundOver();
		
		for(int i = 1; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			p.removeImmortality();
			p.playerHit();
		}
		test3 = model.isRoundOver();

		assertTrue(!test1 && !test2 && test3);
	}
	
	@Test
	public void testMove() {
//		String s = "";
//		String t;
//		Tile[][] map = model.getMap();
//		for(int i = 0; i < map.length; i++) {
//			for(int j = 0; j < map[0].length; j++) {
//				if(map[i][j] instanceof Floor) {
//					t = "F ";
//				} else if(map[i][j] instanceof Pillar) {
//					t = "P ";
//				} else if(map[i][j] instanceof Box) {
//					t = "B ";
//				} else {
//					t = ". ";
//				}
//				s = s + t;
//			}
//			s = s + "\n";
//		}
//		System.out.println(s);
		fail("Not yet implemented");
	}
	
	@Test
	public void testStartGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpgradePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayers() {
		fail("Not yet implemented");
	}
	
	@After
	public void tearDown() throws Exception {
		List<Player> players = model.getPlayers();
		players.clear();
		model = null;
	}
}
