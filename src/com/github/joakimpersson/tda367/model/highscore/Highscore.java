package com.github.joakimpersson.tda367.model.highscore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.joakimpersson.tda367.model.constants.Parameters;
import com.github.joakimpersson.tda367.model.player.Player;
import com.github.joakimpersson.tda367.model.utils.FileScanner;

/**
 * A class handling the applications highscore logic
 * 
 * @author joakimpersson
 * @modified adderollen
 */
public class Highscore {

	private final String FILE_NAME;
	private List<Score> playerList;
	private final int MAX_SIZE;

	/**
	 * Create a new HighScore object responsible for managing the games score
	 * objects
	 */
	public Highscore() {
		playerList = new ArrayList<Score>();
		FILE_NAME = "gen/highScore.data";
		MAX_SIZE = Parameters.INSTANCE.getHighscoreMaxSize();
		loadList();
	}

	/**
	 * Update the current HighScorelist
	 * 
	 * @param players
	 *            A list of the players that have completed a game
	 */
	public void update(List<Player> players) {
		for (Player player : players) {
			Score score = new Score(player.getName(), player.getPoints());
			playerList.add(score);
		}

		this.sortList();
		this.trimeHighScoreList();
		this.saveList();
	}

	/**
	 * Sorts the list of score objects in descending order
	 */
	private void sortList() {

		Collections.sort(playerList);
		// Reverse the order, since it sorts ascending
		Collections.reverse(playerList);

	}

	/**
	 * Responsible for score objects from the list if they don't qualify under
	 * the limit of Score objects
	 */
	private void trimeHighScoreList() {
		int index = playerList.size() - 1;
		while (playerList.size() > MAX_SIZE) {
			playerList.remove(index);
			index--;
		}

	}

	/**
	 * Save the highscorelist in a file.
	 */
	private void saveList() {
		FileScanner.writeOjbect(FILE_NAME, playerList);
	}

	/**
	 * Loads the Highscorelist file. If no file exist, it creates a
	 * Highscorelist object.
	 */
	@SuppressWarnings("unchecked")
	private void loadList() {
		Object object = FileScanner.readObject(FILE_NAME);
		if (object != null) {
			playerList = (List<Score>) object;
		}// otherwise it should overwrite itself
	}

	/**
	 * Get a copy the HighScore list
	 * 
	 * @return A copy of the list of score objects in descending order
	 */
	public List<Score> getList() {
		this.loadList();
		return new ArrayList<Score>(playerList);
	}

	/**
	 * Get the size of the HighScore list
	 * 
	 * @return The number of score objects in the list
	 */
	public int getSize() {
		return this.playerList.size();
	}

	/**
	 * Reset the HighScorelist and remove all previous records from the game
	 */
	public void reset() {
		playerList.clear();
		saveList();
	}
}
