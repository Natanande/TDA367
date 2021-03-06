package com.github.joakimpersson.tda367.model.player;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.github.joakimpersson.tda367.model.constants.PointGiver;

/**
 * A class handling the players score and credits.
 * 
 * @author Andreas Rolen
 * @modified joakimpersson
 * 
 */
public class PlayerPoints implements Serializable, Comparable<PlayerPoints> {

	/**
	 * Auto generated serial UID
	 */
	private static final long serialVersionUID = -4730508024940842687L;

	private int totalScore;
	private int credits;
	private Map<PointGiver, Integer> pointGivers;

	/**
	 * Create a new PlayerPoint object with all fields set to zero
	 */
	public PlayerPoints() {
		this.totalScore = 0;
		this.credits = 0;
		this.pointGivers = new EnumMap<PointGiver, Integer>(PointGiver.class);
		initPointGiverMaps();
	}

	/**
	 * Create a new PlayerPoints object as an copy of another PlayerPoint object
	 * 
	 * @param other
	 *            The PlayerPoint to be copied
	 */
	public PlayerPoints(PlayerPoints other) {
		this.totalScore = other.totalScore;
		this.credits = other.credits;
		this.pointGivers = new EnumMap<PointGiver, Integer>(other.pointGivers);
	}

	/**
	 * Create a new map of all the possible PointGivers and set their starting
	 * value to zero
	 */
	private void initPointGiverMaps() {
		int startValue = 0;
		for (PointGiver pg : PointGiver.values()) {
			this.pointGivers.put(pg, startValue);
		}
	}

	/**
	 * Update the players PlayerPoint score, credits and number of destroyed
	 * blocks
	 * 
	 * @param list
	 *            A list of PointGivers that the player has received
	 */
	public void update(List<PointGiver> list) {
		for (PointGiver pointGiver : list) {
			this.totalScore += pointGiver.getScore();
			this.credits += pointGiver.getScore();
			int prevValue = pointGivers.get(pointGiver);
			pointGivers.put(pointGiver, prevValue + 1);
		}
	}

	/**
	 * * Update the players PlayerPoint score, credits and number of destroyed
	 * blocks
	 * 
	 * @param pointGiver
	 *            The achievment the player performed as a PointGiver
	 */
	public void update(PointGiver pointGiver) {
		int score = pointGiver.getScore();
		this.totalScore += score;
		this.credits += score;
		int prevValue = this.pointGivers.get(pointGiver);
		this.pointGivers.put(pointGiver, prevValue + 1);
	}

	/**
	 * Reducing the players credits.
	 * 
	 * @param cost
	 *            The cost for the item that is bought.
	 */
	public void useCredits(int cost) {
		credits -= cost;
	}

	/**
	 * Returns the players current score.
	 * 
	 * @return Integer
	 */
	public int getScore() {
		return totalScore;
	}

	/**
	 * Returns the players credit amount.
	 * 
	 * @return Integer
	 */
	public int getCredits() {
		return credits;
	}

	/**
	 * Returns the amount of certain destroyed PointGiver type by this player.
	 * 
	 * @param type
	 *            type of PointGiver tile
	 * @return The number of destroyed tile type in PointGiver
	 */
	public int getEarnedPointGiver(PointGiver type) {
		return pointGivers.get(type);
	}

	/**
	 * Resets the players score and credits to zero
	 */
	public void reset() {
		totalScore = 0;
		credits = 0;
		pointGivers.clear();
		initPointGiverMaps();
	}

	@Override
	public int compareTo(PlayerPoints playerPoints) {
		int sum = this.totalScore - playerPoints.getScore();

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.MatchWon)
				- playerPoints.pointGivers.get(PointGiver.MatchWon);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.RoundWon)
				- playerPoints.pointGivers.get(PointGiver.RoundWon);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.KillPlayer)
				- playerPoints.pointGivers.get(PointGiver.KillPlayer);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.PlayerHit)
				- playerPoints.pointGivers.get(PointGiver.PlayerHit);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.Bomb)
				- playerPoints.pointGivers.get(PointGiver.Bomb);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.Box)
				- playerPoints.pointGivers.get(PointGiver.Box);

		if (sum != 0) {
			return sum;
		}

		return this.pointGivers.get(PointGiver.Pillar)
				- playerPoints.pointGivers.get(PointGiver.Pillar);

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		PlayerPoints other = (PlayerPoints) obj;

		return this.totalScore == other.totalScore
				&& this.credits == other.credits
				&& this.pointGivers.equals(other.pointGivers);
	}

	@Override
	public int hashCode() {
		int sum = 0;
		sum += totalScore * 5;
		sum += credits * 7;
		sum += pointGivers.hashCode() * 17;
		return sum;
	}
}
