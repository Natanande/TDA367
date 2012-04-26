package com.github.joakimpersson.tda367.model.tiles.nonwalkable;

import com.github.joakimpersson.tda367.model.tiles.Tile;

/**
 * 
 * @author joakimpersson
 * 
 */
public class Wall implements Tile {

	private String image;

	public Wall() {
		this.image = "wall";
	}

	@Override
	public boolean isWalkable() {
		return false;
	}

	@Override
	public String toString() {
		return "Wall";
	}

	@Override
	public String getTileType() {
		return this.image;
	}

}
