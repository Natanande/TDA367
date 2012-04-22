package com.github.joakimpersson.tda367.controller;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.joakimpersson.tda367.controller.input.InputData;
import com.github.joakimpersson.tda367.controller.input.InputManager;
import com.github.joakimpersson.tda367.controller.input.KeyBoardInputHandler;
import com.github.joakimpersson.tda367.gui.GameplayView;
import com.github.joakimpersson.tda367.model.BombermanModel;
import com.github.joakimpersson.tda367.model.IBombermanModel;
import com.github.joakimpersson.tda367.model.constants.PlayerAction;
import com.github.joakimpersson.tda367.model.constants.ResetType;
import com.github.joakimpersson.tda367.model.player.Player;

/**
 * 
 * @author joakimpersson
 * 
 */
public class GameplayState extends BasicGameState {

	private int stateID = -1;
	private IBombermanModel model = null;
	private GameplayView view = null;
	private STATE currentState = null;
	private InputManager inputManager = null;

	private List<Player> players = null;

	private enum STATE {
		GAME_RUNNING, ROUND_OVER, MATCH_OVER, GAME_OVER, NOT_STARTED;
	}

	public GameplayState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);

		currentState = STATE.GAME_RUNNING;

	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		model = BombermanModel.getInstance();
		view = new GameplayView();
		players = model.getPlayers();
		inputManager = InputManager.getInstance();
		// TODO should be init in setupgamestate
		int id = 0;
		for (Player p : players) {
			KeyBoardInputHandler inputHandler = new KeyBoardInputHandler(p, id);
			inputManager.addInputObject(inputHandler);
			id++;
		}

		currentState = STATE.NOT_STARTED;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (!currentState.equals(STATE.NOT_STARTED)) {
			view.render(container, g);

			if (currentState.equals(STATE.ROUND_OVER)) {
				// view.showRoundStats(container, g);
				System.out.println("press enter");
			}

		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO only during development
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}

		switch (currentState) {
		case GAME_RUNNING:

			// simple check to see whether the turn is over or not
			if (model.isRoundOver()) {
				currentState = STATE.ROUND_OVER;
			} else {
				gameRunning(container);
			}

			break;
		case MATCH_OVER:
			resetState();
			if (model.isGameOver()) {
				currentState = STATE.GAME_OVER;
			} else {
				game.enterState(BombermanGame.UPGRADE_PLAYER_STATE);
			}
			resetState();
			break;
		case ROUND_OVER:

			roundOver(container);

			break;
		default:
			break;
		}

	}

	private void roundOver(GameContainer container) {
		Input input = container.getInput();

		if (inputManager.pressedProcced(input)) {
			if (model.isMatchOver()) {
				currentState = STATE.MATCH_OVER;
			} else {
				resetState();
				// TODO jocke add an wainting state and restart
			}
		}

	}

	private void resetState() {

		switch (currentState) {
		case ROUND_OVER:
			model.reset(ResetType.Round);
			break;
		case MATCH_OVER:
			model.reset(ResetType.Match);
			break;
		default:
			break;
		}

	}

	private void gameRunning(GameContainer gc) {
		Input input = gc.getInput();

		// TODO jocke only used during development
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			gc.exit();
		}

		List<InputData> data = inputManager.getData(input);

		for (InputData d : data) {
			model.updateGame(d.getPlayer(), d.getAction());
		}

		// TODO adrian 360 controller input tests
		Player p1 = players.get(0);
		Player p2 = players.get(1);
		X360Input(input, p1, 0);
		X360Input(input, p2, 1);
	}

	private void X360Input(Input input, Player player, int controller) {
		if (input.isButtonPressed(11, controller)) {
			model.updateGame(player, PlayerAction.PlaceBomb);
		}

		if (input.isControllerUp(controller)) {
			model.updateGame(player, PlayerAction.MoveUp);
		} else if (input.isControllerDown(controller)) {
			model.updateGame(player, PlayerAction.MoveDown);
		} else if (input.isControllerLeft(controller)) {
			model.updateGame(player, PlayerAction.MoveLeft);
		} else if (input.isControllerRight(controller)) {
			model.updateGame(player, PlayerAction.MoveRight);
		}
	}

	@Override
	public int getID() {
		return stateID;
	}

}
