package com.github.joakimpersson.tda367.model.player;

import static com.github.joakimpersson.tda367.model.constants.Attribute.BombStack;
import static com.github.joakimpersson.tda367.model.constants.Attribute.Health;
import static com.github.joakimpersson.tda367.model.player.PlayerAttributes.UpgradeType.Match;
import static com.github.joakimpersson.tda367.model.player.PlayerAttributes.UpgradeType.Round;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.joakimpersson.tda367.model.constants.Attribute;
import com.github.joakimpersson.tda367.model.constants.Direction;
import com.github.joakimpersson.tda367.model.constants.Parameters;
import com.github.joakimpersson.tda367.model.constants.PointGiver;
import com.github.joakimpersson.tda367.model.player.PlayerAttributes.UpgradeType;
import com.github.joakimpersson.tda367.model.utils.FPosition;
import com.github.joakimpersson.tda367.model.utils.Position;

/**
 * This class defines a Player in the bomberman-like game.
 * 
 * @author Adrian Bjug�rd
 * @modified Viktor Anderling, Joakim Persson
 */
public class Player {
	/**
	 * TimerTask initiated getFireDuration()-milliseconds after player has been
	 * hit by fire.
	 */
	private class HitTask extends TimerTask {
		/**
		 * Initiated when player is hit.
		 */
		public HitTask() {
			health--;
			justHit = true;
		}

		@Override
		public void run() {
			justHit = false;
		}
	}

	private final String name;
	private final Position initialPosition;
	private Position tilePos;
	private FPosition gamePos;
	private PlayerAttributes attr;
	private PlayerPoints points;
	private int bombsPlaced, health;
	private boolean justHit;

	/**
	 * Creates a player with a pre-defined position and name.
	 * 
	 * @param name
	 *            The name of the player.
	 * @param pos
	 *            The starting position of a player.
	 */
	public Player(String name, Position pos) {
		this.name = name;
		this.initialPosition = pos;
		initPlayer();
	}

	/**
	 * Method for initialising a player.
	 */
	private void initPlayer() {
		this.attr = new PlayerAttributes();
		this.points = new PlayerPoints();
		roundReset();
	}

	/**
	 * Method used to reset a players state for a new round.
	 */
	public void roundReset() {
		this.justHit = false;
		this.attr.resetAttr(Round);
		this.health = getAttribute(Health);
		this.tilePos = initialPosition;
		this.gamePos = new FPosition(initialPosition.getX() + 0.5F, initialPosition.getY() + 0.5F);
		this.bombsPlaced = 0;
	}

	/**
	 * Method used to reset a players state for a new match (3 rounds).
	 */
	public void matchReset() {
		this.attr.resetAttr(Match);
		roundReset();
	}

	/**
	 * Moves a player in specified direction.
	 * 
	 * @param dir
	 *            The direction in which the player will move.
	 */
	public void move(Direction dir) {
		double stepSize = Parameters.INSTANCE.getPlayerStepSize();
		double newFX = gamePos.getX() + stepSize * dir.getX();
		double newFY = gamePos.getY() + stepSize * dir.getY();
		gamePos = new FPosition((float) newFX, (float) newFY);
		tilePos = new Position((int) newFX, (int) newFY);
	}

	/**
	 * @return If a player can place a bomb or not.
	 */
	public boolean canPlaceBomb() {
		if (getAttribute(BombStack) > this.bombsPlaced) {
			return true;
		}
		return false;
	}

	/**
	 * Increases the current number of bombs placed by the player.
	 */
	public void increaseBombsPlaced() {
		this.bombsPlaced++;
	}

	/**
	 * Decreases the current number of bombs placed by the player.
	 */
	public void decreaseBombsPlaced() {
		this.bombsPlaced--;
	}

	/**
	 * Upgrade either a round or match attribute with one level.
	 * 
	 * @param attr
	 *            The attribute to be upgraded
	 * @param type
	 *            The type of the upgrade
	 */
	public void upgradeAttr(Attribute attr, UpgradeType type) {
		this.attr.upgradeAttr(attr, type);
	}

	/**
	 * Method for getting a players attributes.
	 * 
	 * @return A players list of attributes.
	 */
	public List<Attribute> getPermanentAttributes() {
		return this.attr.getAttributes();
	}

	/**
	 * Method for getting the value of a specific requested attribute.
	 * 
	 * @param attr
	 *            The type of attribute requested.
	 * @return The value of the attribute requested.
	 */
	public int getAttribute(Attribute attr) {
		return this.attr.getAttrValue(attr);
	}

	/**
	 * Method called when player is hit by a bomb.
	 */
	public void playerHit() {
		if (this.justHit == false) {
			Timer justHitTimer = new Timer();
			justHitTimer.schedule(new HitTask(), Parameters.INSTANCE.getFireDuration());
		}
	}

	/**
	 * Check whether player is alive.
	 * 
	 * @return Player's vitals.
	 */
	public boolean isAlive() {
		return this.health > 0;
	}

	/**
	 * Method to get a players score.
	 * 
	 * @return The players score.
	 */
	public int getScore() {
		return points.getScore();
	}

	/**
	 * Method to get a players available credits.
	 * 
	 * @return The players available credits.
	 */
	public int getCredits() {
		return points.getCredits();
	}

	/**
	 * Method which uses a players credits.
	 * 
	 * @param cost
	 *            Amount of credits used.
	 */
	public void useCredits(int cost) {
		this.points.useCredits(cost);
	}

	/**
	 * This will update a players points with a list of PointGiver's.
	 * 
	 * @param pg
	 *            List containing PointGiver's.
	 */
	public void updatePlayerPoints(List<PointGiver> pg) {
		this.points.update(pg);
	}

	/**
	 * Get the players name.
	 * 
	 * @return Name of the player.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the players current health.
	 * 
	 * @return Current health of the player.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Get the current Position of the player in the tile grid.
	 * 
	 * @return Which position the player has in the tile grid.
	 */
	public Position getTilePosition() {
		return tilePos;
	}

	/**
	 * Get the current FPosition of the player in the game grid.
	 * 
	 * @return Where the player is on the game grid.
	 */
	public FPosition getGamePosition() {
		return gamePos;
	}

	@Override
	public String toString() {
		return "P[" + this.name + ", " + this.tilePos + ", " + this.health + " HP]";
	}

}
