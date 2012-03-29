package com.github.joakimpersson.tda367.core.bombs;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.github.joakimpersson.tda367.core.Attribute.*;
import com.github.joakimpersson.tda367.core.Parameters;
import com.github.joakimpersson.tda367.core.Player;
import com.github.joakimpersson.tda367.core.Position;
import com.github.joakimpersson.tda367.core.tiles.Floor;
import com.github.joakimpersson.tda367.core.tiles.Tile;

public abstract class Bomb implements Tile {

	int toughness, range, power;
	Timer timer;
	Player player;
	Position pos;
	List<Position> fireList = new ArrayList<Position>();

	public Bomb(Player p, Timer t) {
		this.pos = p.getTilePosition();
		this.player = p;
		this.toughness = 1; // TODO adrian: kanske �ndras senare eller n�tt s�nt
		this.range = p.getAttribute(BombRange);
		this.power = p.getAttribute(BombPower);
		this.timer = t;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public int getToughness() {
		return toughness;
	}

	@Override
	public Tile onFire() {
		this.timer.cancel();
		this.player.decreaseBombsPlaced();
		return new Floor();
	}

	@Override
	public boolean isWalkable() {
		return false;
	}

	//
	// int tryBreak(Position pos, int power, Tile tile) {
	// if (pos.getX()<0 || pos.getY()<0) {
	// return -1;
	// }
	// int toughness = tile.getToughness();
	// if (power >= toughness)
	// return toughness;
	// else
	// return -1;
	// }
	
	boolean tryBreak(Tile tile, int power) {
		return power >= tile.getToughness();
	}

	boolean validPos(Position firePos) {
		Dimension d = Parameters.INSTANCE.getDimensions();
		return firePos.getX() >= 0 
				&& firePos.getX() <= d.width
				&& firePos.getY() >= 0
				&& firePos.getY() <= d.height;
				
	}
	
	public abstract List<Position> explode(Tile[][] map);
}
