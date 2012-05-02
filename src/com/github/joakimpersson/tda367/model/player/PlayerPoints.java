package com.github.joakimpersson.tda367.model.player;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.github.joakimpersson.tda367.model.constants.PointGiver;

/**
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

	public PlayerPoints() {
		this.totalScore = 0;
		this.credits = 0;
		this.pointGivers = new EnumMap<PointGiver, Integer>(PointGiver.class);
		initPointGiverMaps();
	}

	private void initPointGiverMaps() {
		int startValue = 0;
		for (PointGiver pg : PointGiver.values()) {
			this.pointGivers.put(pg, startValue);
		}
	}

	public void update(List<PointGiver> list) {

		for (PointGiver p : list) {
			this.totalScore += p.getScore();
			this.credits += p.getScore();
			int prevValue = pointGivers.get(p);
			pointGivers.put(p, prevValue + 1);
		}
	}

	/**
	 * Reducing the players credits.
	 * 
	 * @param cost
	 *            The cost for the item that is bought.
	 */
	public void useCredits(int cost) {
		this.credits = this.credits - cost;
	}

	/**
	 * Returns the players current score.
	 * 
	 * @return Integer
	 */
	public int getScore() {
		return this.totalScore;
	}

	/**
	 * Returns the players credit amount.
	 * 
	 * @return Integer
	 */
	public int getCredits() {
		return this.credits;
	}

	/**
	 * Returns the amount of certain destroyed PointGiver type by this player.
	 * 
	 * @param type
	 *            type of PointGiver tile
	 * @return The number of destroyed tile type in PointGiver
	 */
	public int getDestroyedPointGiver(PointGiver type) {
		return this.pointGivers.get(type);
	}

	/**
	 * Resets the players score and credits to zero
	 */
	public void reset() {
		this.totalScore = 0;
		this.credits = 0;
		pointGivers.clear();
		initPointGiverMaps();
	}

	@Override
	public int compareTo(PlayerPoints pp) {
		int sum = this.totalScore - pp.getScore();

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.KillPlayer)
				- pp.pointGivers.get(PointGiver.KillPlayer);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.PlayerHit)
				- pp.pointGivers.get(PointGiver.PlayerHit);

		sum = this.pointGivers.get(PointGiver.Bomb)
				- pp.pointGivers.get(PointGiver.Bomb);

		if (sum != 0) {
			return sum;
		}

		sum = this.pointGivers.get(PointGiver.Box)
				- pp.pointGivers.get(PointGiver.Box);

		if (sum != 0) {
			return sum;
		}

		return this.pointGivers.get(PointGiver.Pillar)
				- pp.pointGivers.get(PointGiver.Pillar);

	}

	@Override
	public boolean equals(final Object obj) {
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
		for (PointGiver pg : pointGivers.keySet()) {
			int tmpSum = pointGivers.get(pg);
			sum += tmpSum * 13;
		}
		return sum;
	}
}
