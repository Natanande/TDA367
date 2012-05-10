package com.github.joakimpersson.tda367.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.github.joakimpersson.tda367.model.constants.Attribute;
import com.github.joakimpersson.tda367.model.constants.Direction;
import com.github.joakimpersson.tda367.model.constants.EventType;
import com.github.joakimpersson.tda367.model.constants.Parameters;
import com.github.joakimpersson.tda367.model.constants.PlayerAction;
import com.github.joakimpersson.tda367.model.constants.PointGiver;
import com.github.joakimpersson.tda367.model.constants.ResetType;
import com.github.joakimpersson.tda367.model.map.GameMap;
import com.github.joakimpersson.tda367.model.map.IGameMap;
import com.github.joakimpersson.tda367.model.player.Player;
import com.github.joakimpersson.tda367.model.player.PlayerAttributes.UpgradeType;
import com.github.joakimpersson.tda367.model.tiles.Destroyable;
import com.github.joakimpersson.tda367.model.tiles.Tile;
import com.github.joakimpersson.tda367.model.tiles.WalkableTile;
import com.github.joakimpersson.tda367.model.tiles.bombs.AreaBomb;
import com.github.joakimpersson.tda367.model.tiles.bombs.Bomb;
import com.github.joakimpersson.tda367.model.tiles.bombs.NormalBomb;
import com.github.joakimpersson.tda367.model.tiles.walkable.Fire;
import com.github.joakimpersson.tda367.model.tiles.walkable.Floor;
import com.github.joakimpersson.tda367.model.utils.FPosition;
import com.github.joakimpersson.tda367.model.utils.Position;

/**
 * 
 * @author Viktor Anderling
 * @modified Joakim Persson, Adrian Bjug�rd
 * 
 */
public class BombermanModel implements IBombermanModel {

	private List<Player> players;
	private IGameMap map;
	private static BombermanModel instance = null;
	private LinkedList<Map<Position, Tile>> waitingFirePositions;
	private PropertyChangeSupport pcs;
	private Highscore highscore = null;
	private GameController gameController = null;

	private BombermanModel() {
		this.pcs = new PropertyChangeSupport(this);
		highscore = new Highscore();
	}

	/**
	 * @return The instance of this bombermanModel.
	 */
	public static BombermanModel getInstance() {
		if (instance == null) {
			instance = new BombermanModel();
		}
		return instance;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.pcs.addPropertyChangeListener(pcl);
	}

	@Override
	public void startGame(List<Player> players) {
		this.players = players;
		this.gameController = new GameController(players);
		this.map = new GameMap();
		this.waitingFirePositions = new LinkedList<Map<Position, Tile>>();
	}

	@Override
	public void upgradePlayer(Player player, Attribute attr) {
		if (player.getCredits() >= attr.getCost()) {
			player.upgradeAttr(attr, UpgradeType.Match);
			player.useCredits(attr.getCost());

			pcs.firePropertyChange("play", null, EventType.MENU_ACTION);
		} else {
			pcs.firePropertyChange("play", null, EventType.ERROR);
		}
	}

	/**
	 * Updates the model by either moving a player or making them place a bomb.
	 * 
	 * @param action
	 * 				The given action.
	 * @param player
	 * 				The player that will perform the action.
	 */
	@Override
	public void updateGame(Player player, PlayerAction action) {
		if (player.isAlive()) {
			if(action.equals(PlayerAction.ACTION)) {
				this.placeBomb(player);
			} else {
				for(Direction direction : action.getDirections()) {
					if (direction != Direction.NONE) {
						this.move(player, direction);
					}
				}
			}			
		}
	}

	/**
	 * This method moves the player in a double grid. If the tile at the chosen
	 * direction is walkable or the player will end up at least at the length of
	 * 0.2 from it, the player will move in that direction and action is taken
	 * if a new tile is entered. Else, nothing happens.
	 * 
	 * @param player
	 *            The player to move.
	 * @param direction
	 *            The direction for the player to move
	 */
	private void move(Player player, Direction direction) {
		Position prevPos = player.getTilePosition();
		Tile tileAtDirection = map.getTile(new Position(prevPos.getX()
				+ direction.getX(), prevPos.getY()
				+ direction.getY()));
		// The tile that the player may walk into.
		
		// TODO fix bug where player walks into blocks.
		if (tileAtDirection.isWalkable()) {
			player.move(direction);
		} else {
			FPosition decimalPos = player.getGamePosition();
			// Removes the integer part of the players position, leaving only
			// the decimal part.
			decimalPos = new FPosition(decimalPos.getX()
					- (int) decimalPos.getX(), decimalPos.getY()
					- (int) decimalPos.getY());

			double stepSize = Parameters.INSTANCE.getPlayerStepSize();
			double xStep = stepSize * direction.getX();
			double yStep = stepSize * direction.getY();
			// Adding the steps to the player's new position.
			decimalPos = new FPosition((float) (decimalPos.getX() + xStep),
					(float) (decimalPos.getY() + yStep));

			// Can't move closer than 0.2 to a non-walkable tile.
			// TODO move/refactor this parameter?
			float pD = 0.2F;
			if ((direction == Direction.NORTH && decimalPos.getY() >= pD)
					|| (direction == Direction.SOUTH && decimalPos.getY() <= 1 - pD)
					|| (direction == Direction.WEST && decimalPos.getX() >= pD)
					|| (direction == Direction.EAST && decimalPos.getX() <= 1 - pD)) {
				player.move(direction);
			}
		}

		Position newPos = player.getTilePosition();

		// Handles what happens if the player walks into a new tile.
		if (!prevPos.equals(newPos)) {
			WalkableTile enteredTile = (WalkableTile) map.getTile(newPos);
			map.setTile(enteredTile.playerEnter(player), newPos);
		}
	}

	/**
	 * This method tells a player to create a bomb, and starts a timer for that
	 * bomb.
	 * 
	 * @param player
	 *            The player that places the bomb.
	 */
	private void placeBomb(Player player) {
		if (player.canPlaceBomb()
				&& map.getTile(player.getTilePosition()) instanceof Floor) {
			Timer bombTimer = new Timer();
			Bomb bomb = createBomb(player, bombTimer);// player.createBomb(bombTimer);
			bombTimer.schedule(new BombTask(bomb),
					Parameters.INSTANCE.getBombDetonationTime());

			map.setTile(bomb, player.getTilePosition());

			pcs.firePropertyChange("play", null, EventType.BOMB_PLACED);
		}
	}

	// TODO refactor
	private Bomb createBomb(Player player, Timer bombTimer) {

		switch (player.getAttribute(Attribute.BombType)) {
		case 1:
			return new AreaBomb(player, bombTimer);
		default:
			return new NormalBomb(player, bombTimer);
		}
	}

	/**
	 * This method takes care of what happens to the the positions that the fire
	 * strikes, including giving points and place the fire.
	 * 
	 * @param positions
	 *            The list that contains the positions of where the fire is to
	 *            be placed.
	 * @param bombOwner
	 *            The player that placed the bomb.
	 */
	private void handleFire(Player bombOwner,
			Map<Position, Direction> directedFirePositions) {
		List<Position> list = new ArrayList<Position>(
				directedFirePositions.keySet());
		fireObjects(bombOwner, list);
		setFire(bombOwner, directedFirePositions);
	}

	/**
	 * This method is called internally by handle fire to distribute points for
	 * hitting players and items.
	 * 
	 * @param positions
	 *            The list that contains the positions of where the fire
	 *            strikes.
	 * @param bombOwner
	 *            The player that placed the bomb.
	 */
	private void fireObjects(Player bombOwner, List<Position> positions) {
		hitPlayers(bombOwner, positions);
		hitItems(bombOwner, positions);
	}

	private void hitPlayer(Player bombOwner, Player targetPlayer) {
		List<PointGiver> pg = new ArrayList<PointGiver>();
		if (!targetPlayer.isImmortal() && targetPlayer.isAlive()) {
			targetPlayer.playerHit();
			if (!bombOwner.equals(targetPlayer)) {
				pg.add(PointGiver.PlayerHit);
				if (!targetPlayer.isAlive()) {
					pg.add(PointGiver.KillPlayer);
				}
			}
		}
		bombOwner.updatePlayerPoints(pg);
	}

	private void hitPlayers(Player bombOwner, List<Position> positions) {

		for (Player p : players) {
			Position playerPos = p.getTilePosition();
			if (positions.contains(playerPos)) {
				hitPlayer(bombOwner, p);
			}
		}
	}

	private void hitItems(Player bombOwner, List<Position> positions) {
		List<PointGiver> pg = new ArrayList<PointGiver>();
		Tile tmpTile;

		for (Position pos : positions) {

			tmpTile = map.getTile(pos);
			if (tmpTile instanceof Destroyable) {
				Destroyable destroyableTile = (Destroyable) tmpTile;
				if (tmpTile instanceof Bomb) {
					if (!bombOwner.equals(((Bomb) tmpTile).getPlayer())) {
						// TODO jocke really bad code...
						destroyableTile = (Destroyable) tmpTile;
						pg.add(destroyableTile.getPointGiver());
					}
				} else {
					destroyableTile = (Destroyable) tmpTile;
					pg.add(destroyableTile.getPointGiver());
				}
			}
		}
		bombOwner.updatePlayerPoints(pg);
	}

	/**
	 * This method is called internally by handle fire to set fire at positions
	 * for a fixed time specifically.
	 * 
	 * @param positions
	 *            The list that contains the positions of where the fire is to
	 *            be placed.
	 */
	private void setFire(Player fireOwner, Map<Position, Direction> directedFire) {
		Map<Position, Tile> waitingTiles = new HashMap<Position, Tile>();

		Set<Map.Entry<Position, Direction>> entries = directedFire.entrySet();
		Iterator<Map.Entry<Position, Direction>> iter = entries.iterator();

		while (iter.hasNext()) {
			Map.Entry<Position, Direction> entry = iter.next();
			Position pos = entry.getKey();
			Direction direction = entry.getValue();

			Tile currentTile = map.getTile(pos);
			if (currentTile instanceof Destroyable) {

				Destroyable destroyableTile = (Destroyable) currentTile;
				Tile newTile = destroyableTile.onFire();
				waitingTiles.put(pos, newTile);
				map.setTile(new Fire(fireOwner, direction), pos);
			}
		}

		waitingFirePositions.addLast(waitingTiles);
		Timer timer = new Timer();
		int fireDuration = Parameters.INSTANCE.getFireDuration();
		timer.schedule(new FireTimerTask(), fireDuration);
	}

	/**
	 * This method is called when the fire is dying, witch causes it to be
	 * replaced by the appropriate new tile.
	 */
	private void removeFirstFire() {

		if (waitingFirePositions != null && waitingFirePositions.size() > 0) {
			Map<Position, Tile> fireReplacers = waitingFirePositions.get(0);

			Set<Map.Entry<Position, Tile>> entries = fireReplacers.entrySet();
			Iterator<Map.Entry<Position, Tile>> iter = entries.iterator();
			while (iter.hasNext()) {
				Map.Entry<Position, Tile> entry = iter.next();
				Position pos = entry.getKey();
				Tile tile = entry.getValue();
				map.setTile(tile, pos);
			}
			waitingFirePositions.removeFirst();
		}
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public Tile[][] getMap() {
		return map.getMap();
	}

	@Override
	public boolean isRoundOver() {
		return gameController.isRoundOver();
	}

	@Override
	public boolean isMatchOver() {
		return gameController.isMatchOver();
	}

	@Override
	public boolean isGameOver() {
		return gameController.isGameOver();
	}

	@Override
	public void reset(ResetType type) {

		switch (type) {
		case Match:
			matchReset();
			break;
		case Round:
			roundReset();
			break;
		default:
			// Should not happen and therefore we do nothing TODO perhaps
			break;
		}
		resetMap();
	}

	@Override
	public List<Score> getHighscoreList() {
		return highscore.getList();
	}

	@Override
	public void resetHighscoreMap() {
		highscore.reset();
	}

	@Override
	public void gameOver() {

		gameController.gameOver();

		// add the players to highscore list
		highscore.update(players);

		// destroy the current GameController Objet
		gameController = null;

		// destroy the list of current players
		players = null;

		// also destroy the current map
		map = null;
	}

	private void matchReset() {
		gameController.matchOver();
		resetPlayer(ResetType.Match);
	}

	private void roundReset() {
		gameController.roundOver();
		resetPlayer(ResetType.Round);
	}

	private void resetPlayer(ResetType type) {
		for (Player p : players) {
			p.reset(type);
		}
	}

	private void resetMap() {
		// In order to avoid that tiles are cleared on the new map
		waitingFirePositions.clear();
		map.reset();
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for(Player player : players) {
			strBuilder.append(player.toString());
			strBuilder.append("\n");
		}
		strBuilder.append(map.toString());
		return strBuilder.toString();
	}

	/**
	 * Timer-task that is used for scheduling what happens when the fire is
	 * removed.
	 */
	private class FireTimerTask extends TimerTask {
		@Override
		public void run() {
			removeFirstFire();
		}
	}

	/**
	 * Timer-task that is used for scheduling what happens when a bomb
	 * detonates.
	 */
	private class BombTask extends TimerTask {
		private Bomb bomb;

		public BombTask(Bomb b) {
			this.bomb = b;
		}

		@Override
		public void run() {
			handleFire(bomb.getPlayer(), bomb.explode(map.getMap()));

			pcs.firePropertyChange("play", null, EventType.BOMB_EXPLODED);
		}
	}

}