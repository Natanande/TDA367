package com.github.joakimpersson.tda367.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.github.joakimpersson.tda367.gui.guiutils.GUIUtils;
import com.github.joakimpersson.tda367.model.BombermanModel;
import com.github.joakimpersson.tda367.model.IBombermanModel;
import com.github.joakimpersson.tda367.model.player.Player;

/**
 * 
 * @author joakimpersson
 * 
 */
public class UpgradePlayerView implements IUpgradePlayerView {

	private IBombermanModel model = null;
	private List<Player> players = null;
	private List<UpgradePlayerPanelView> playerViews = null;
	private static final int POS_Y = 50;
	private static final int POS_X = 50;

	/**
	 * Creats a new view that holds the info about the upgradeplayerview
	 * subpanels
	 */
	public UpgradePlayerView() {
		init();
	}

	/**
	 * Responsible for fetching instances ,info from the model and init fonts
	 * etc
	 */
	private void init() {
		model = BombermanModel.getInstance();
	}

	public void enter() {
		players = model.getPlayers();
		playerViews = new ArrayList<UpgradePlayerPanelView>();
		int xDelta = GUIUtils.getGameWidth() / players.size();
		int x = POS_X;

		for (Player p : players) {
			UpgradePlayerPanelView view = new UpgradePlayerPanelView(p, x,
					POS_Y);

			playerViews.add(view);
			x += xDelta;
		}

	}

	@Override
	public void render(GameContainer container, Graphics g,
			Map<Integer, Integer> playerAttrIndex) {

		for (UpgradePlayerPanelView view : playerViews) {
			view.render(container, g, playerAttrIndex);
		}
	}

}
