package com.github.joakimpersson.tda367.core.bombs;

import java.util.List;
import java.util.Timer;

import com.github.joakimpersson.tda367.core.Player;
import com.github.joakimpersson.tda367.core.Position;
import com.github.joakimpersson.tda367.core.tiles.Tile;

public class MockBomb extends Bomb {

	public MockBomb(Player p, Timer t) {
		super(p, t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Position> explode(Tile[][] map) {
		// TODO Auto-generated method stub
		return null;
	}

}
