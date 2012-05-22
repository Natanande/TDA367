package com.github.joakimpersson.tda367.controller;

import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.joakimpersson.tda367.audio.AudioEventListener;
import com.github.joakimpersson.tda367.controller.input.DefaultKeyMappings;
import com.github.joakimpersson.tda367.controller.input.InputData;
import com.github.joakimpersson.tda367.controller.input.InputHandler;
import com.github.joakimpersson.tda367.controller.input.InputManager;
import com.github.joakimpersson.tda367.controller.input.KeyBoardInputHandler;
import com.github.joakimpersson.tda367.controller.input.X360InputHandler;
import com.github.joakimpersson.tda367.controller.utils.ControllerUtils;
import com.github.joakimpersson.tda367.gui.SetupGameView;
import com.github.joakimpersson.tda367.model.PyromaniacModel;
import com.github.joakimpersson.tda367.model.IPyromaniacModel;
import com.github.joakimpersson.tda367.model.constants.EventType;
import com.github.joakimpersson.tda367.model.constants.Parameters;
import com.github.joakimpersson.tda367.model.constants.PlayerAction;
import com.github.joakimpersson.tda367.model.player.Player;
import com.github.joakimpersson.tda367.model.positions.Position;

/**
 * Sets up the players
 * 
 * @author rekoil
 * @modified joakimpersson
 */
public class SetupGameState extends BasicGameState {

	private SetupGameView view = null;
	private IPyromaniacModel model = null;
	private int stateID = -1;
	private int selection = 0;
	private int stage = 0;
	private int controllerCount;
	private int possiblePlayers;
	private int players;
	private List<Player> playerList;
	private LinkedList<String> controllersBound;
	private InputManager inputManager = null;
	private PropertyChangeSupport pcs;

	/**
	 * Create a new slick BasicGameState controller for the SetupGameState
	 * 
	 * @param stateID
	 */
	public SetupGameState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);

		pcs.firePropertyChange("play", null, EventType.TITLE_SCREEN);
		playerList = new ArrayList<Player>();
		controllersBound = new LinkedList<String>();

		stage = 0;
		controllerCount = container.getInput().getControllerCount();
		possiblePlayers = 2 + controllerCount;

		if (possiblePlayers > 4) {
			possiblePlayers = 4;
		}

		selection = 2;
		view.setPossiblePlayers(possiblePlayers);
		view.enter();
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		ControllerUtils.clearInputQueue(container.getInput());

		this.pcs = new PropertyChangeSupport(this);
		this.pcs.addPropertyChangeListener(AudioEventListener.getInstance());

		model = PyromaniacModel.getInstance();
		inputManager = InputManager.getInstance();
		view = new SetupGameView(container);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		view.render(container, g, selection);
	}

	/**
	 * Gets actions from the default inputs, before any controllers are setup.
	 * 
	 * @param input
	 *            The input object
	 * @return A list of actions to perform
	 */
	private List<PlayerAction> defaultInput(Input input) {
		List<InputData> dataList = inputManager.getMenuInputData(input);
		List<PlayerAction> actions = new ArrayList<PlayerAction>();
		for (InputData data : dataList) {
			actions.add(data.getAction());
		}
		return actions;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input = container.getInput();
		List<PlayerAction> actions = defaultInput(input);

		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			game.enterState(PyromaniacsGame.MAIN_MENU_STATE);
		}

		for (PlayerAction action : actions) {
			switch (action) {
			case MOVE_NORTH:
				moveIndex(-1);
				if (stage != 1) {
					pcs.firePropertyChange("play", null,
							EventType.MENU_NAVIGATE);
				}
				break;
			case MOVE_SOUTH:
				moveIndex(1);
				if (stage != 1) {
					pcs.firePropertyChange("play", null,
							EventType.MENU_NAVIGATE);
				}
				break;
			}
		}
		if (stage < 2 && inputManager.pressedProceed(input)) {
			if (stage == 0) {
				players = selection;
				view.startPlayerCreation(players);
				stage++;
			} else if (stage == 1) {
				if (view.verifyNameValidity()) {
					createPlayer(view.getName(), view.getIndex());
					view.playerCreated();
					if (allPlayersCreated()) {
						view.assignControllers();
						stage++;
					}
					pcs.firePropertyChange("play", null, EventType.MENU_ACTION);
				} else {
					pcs.firePropertyChange("play", null, EventType.ERROR);
				}
			}
		} else if (stage == 2 && validControllerBindingProceed(input)) {
			assignPlayer(controllersBound.getLast(), view.getIndex());
			if (allPlayersAssigned()) {
				int newState = PyromaniacsGame.GAMEPLAY_STATE;
				ControllerUtils.changeState(game, newState);
			}
			view.incIndex();
		}
	}

	/**
	 * Method which determines if all players have been assigned to controllers.
	 * 
	 * @return Whether all players have been assigned
	 */
	private boolean allPlayersAssigned() {
		return inputManager.getAssignedControllers() == players;
	}

	/**
	 * Assigns a player to a controller determined by an input string
	 * 
	 * @param controllerUsed
	 *            Which controller will be assigned to the player
	 * @param playerIndex
	 *            Which player the controller will be assigned to
	 */
	private void assignPlayer(String controllerUsed, int playerIndex) {
		inputManager.addInputObject(controllerFactory(
				playerList.get(playerIndex - 1), controllersBound.getLast()));
	}

	/**
	 * Determines whether we can continue binding the chosen controller
	 * 
	 * @param input
	 *            The game input object
	 * @return Whether we are allowed to continue binding the controller
	 */
	private boolean validControllerBindingProceed(Input input) {
		if (input
				.isKeyDown(DefaultKeyMappings.getInstance().getActionButton(0))
				&& !controllersBound.contains("k0")) {
			controllersBound.add("k0");
			return true;
		} else if (input.isKeyDown(DefaultKeyMappings.getInstance()
				.getActionButton(1)) && !controllersBound.contains("k1")) {
			controllersBound.add("k1");
			return true;
		}
		for (int i = 0; i < controllerCount; i++) {
			if (input.isButtonPressed(X360InputHandler.PRIMARY_ACTION, i)
					&& !controllersBound.contains("x" + i)) {
				controllersBound.add("x" + i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a player
	 * 
	 * @param name
	 *            The name of the player
	 * @param playerIndex
	 *            The players index
	 */
	private void createPlayer(String name, int playerIndex) {
		Player player = new Player(playerIndex, name,
				getInitialPosition(playerIndex));
		playerList.add(player);
	}

	/**
	 * Get the initial position of the player to be created.
	 * 
	 * @param playerIndex
	 *            The index of the player
	 * @return The players initial position
	 */
	private Position getInitialPosition(int playerIndex) {
		Dimension mapDimension = Parameters.INSTANCE.getMapSize();

		int left = 1;
		int right = mapDimension.width - 2;
		int top = 1;
		int bottom = mapDimension.height - 2;

		switch (playerIndex) {
		default:
			return new Position(left, top);
		case 2:
			if (players < 3) {
				return new Position(right, bottom);
			} else {
				return new Position(right, top);
			}
		case 3:
			return new Position(left, bottom);
		case 4:
			return new Position(right, bottom);
		}
	}

	/**
	 * Creates a controller to be assigned to the player
	 * 
	 * @param player
	 *            Player to assign the controller to
	 * @param controllerUsed
	 *            Which type of controller (and index of it) was used
	 * @return an InputHandler
	 */
	private InputHandler controllerFactory(Player player, String controllerUsed) {
		char controllerType = controllerUsed.charAt(0);
		int controllerIndex = Integer.decode(Character.toString(controllerUsed
				.charAt(1)));
		if (controllerType == 'k') {
			return new KeyBoardInputHandler(player, controllerIndex);
		} else {
			return new X360InputHandler(player, controllerIndex);
		}
	}

	/**
	 * Checks if all players have been created
	 * 
	 * @return whether all players have been created
	 */
	private boolean allPlayersCreated() {
		return playerList.size() == players;
	}

	/**
	 * Moves the selector
	 * 
	 * @param delta
	 *            How the selector will be moved
	 */
	private void moveIndex(int delta) {
		int currentIndex = selection;
		int newIndex = (currentIndex + delta);

		if (newIndex < 2) {
			newIndex = 2;
		} else if (newIndex > possiblePlayers) {
			newIndex = possiblePlayers;
		}

		selection = newIndex;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		model.startGame(playerList);
	}

	@Override
	public int getID() {
		return stateID;
	}
}