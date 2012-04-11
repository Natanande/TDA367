package com.github.joakimpersson.tda367.model.tiles.bombs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.joakimpersson.tda367.model.player.Player;
import com.github.joakimpersson.tda367.model.tiles.Tile;
import com.github.joakimpersson.tda367.model.tiles.nonwalkable.Box;
import com.github.joakimpersson.tda367.model.tiles.nonwalkable.Pillar;
import com.github.joakimpersson.tda367.model.tiles.nonwalkable.Wall;
import com.github.joakimpersson.tda367.model.tiles.walkable.Fire;
import com.github.joakimpersson.tda367.model.tiles.walkable.Floor;
import com.github.joakimpersson.tda367.model.utils.Position;

/**
 * 
 * @author joakimpersson
 * 
 */
public class NormalBombTest {

	private Timer timer;
	private Bomb bomb;
	private Player player;
	private static Tile[][] map = {
			{ new Wall(), new Wall(), new Wall(), new Wall(), new Wall() },
			{ new Wall(), new Floor(), new Box(), new Floor(), new Wall() },
			{ new Wall(), new Floor(), new Pillar(), new Floor(), new Wall() },
			{ new Wall(), new Box(), new Floor(), new Box(), new Wall() },
			{ new Wall(), new Floor(), new Box(), new Floor(), new Wall() },
			{ new Wall(), new Floor(), new Floor(), new Floor(), new Wall() },
			{ new Wall(), new Wall(), new Wall(), new Wall(), new Wall() } };

	@BeforeClass
	public static void setUpMap() {
		// TODO jocke perhaps the map should be initialized here
	}

	@Before
	public void setUp() throws Exception {
		Position pos = new Position(3, 2);
		timer = new Timer();
		player = new Player("Kalle", pos);
		bomb = new NormalBomb(player, timer);
	}

	@Test
	public void testExplode() {

		List<Position> expectedPositions = new ArrayList<Position>();
		expectedPositions.add(new Position(3, 3));
		expectedPositions.add(new Position(3, 1));
		expectedPositions.add(new Position(4, 2));
		expectedPositions.add(new Position(5, 2));
		expectedPositions.add(new Position(3, 2));
		List<Position> actualPositions = bomb.explode(map);

		// cannot use the lists equal method since it does not regard that the
		// two lists have the positions at different index
		assertEquals(expectedPositions.size(), actualPositions.size());

		for (Position pos : expectedPositions) {
			assertTrue(actualPositions.contains(pos));
		}

	}

	@After
	public void tearDown() throws Exception {
		timer = null;
		player = null;
		bomb = null;
	}

	@AfterClass
	public static void destroyMap() {
		map = null;
	}
}
