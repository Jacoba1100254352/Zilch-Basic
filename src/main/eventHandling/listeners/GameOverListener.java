package eventHandling.listeners;


import controllers.GameServer;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import model.entities.Player;
import model.managers.ActionManager;
import ui.IMessage;

import java.io.IOException;

import static eventHandling.events.GameEventType.GAME_OVER;
import static eventHandling.events.GameEventType.SCORE_UPDATED;


/**
 * Handles score-limit transitions into the final round and announces the
 * winner once the game-over event is dispatched.
 */
public class GameOverListener implements IEventListener
{
	private final int scoreLimit;
	private final GameServer gameServer;
	private final IMessage uiManager;
	private final ActionManager actionManager;
	private Player gameEndingPlayer;

	/**
	 * Creates the listener that watches for score updates and final game-over events.
	 */
	public GameOverListener(int scoreLimit, GameServer gameServer, ActionManager actionManager, IMessage uiManager) {
		this.scoreLimit = scoreLimit;
		this.gameServer = gameServer;
		this.uiManager = uiManager;
		this.actionManager = actionManager;
	}

	@Override
	/**
	 * Starts the final-round flow when a player reaches the score limit and
	 * prints the winner when the game concludes.
	 */
	public void handleEvent(Event event) throws IOException {
		if (event.getType() == GAME_OVER) {
			Player winner = (Player) event.getData(EventDataKey.WINNER);
			if (winner != null) {
				uiManager.announceWinner(winner, winner.score().getPermanentScore());
			}
			return;
		}

		if (event.getType() == SCORE_UPDATED) {
			Player player = (Player) event.getData(EventDataKey.PLAYER);
			if (player != null && player.score().getPermanentScore() >= scoreLimit && gameEndingPlayer == null) {
				gameEndingPlayer = player;
				actionManager.setGameEndingPlayer(player);
				uiManager.displayLastRoundMessage(player, () -> {
				});
				gameServer.handleLastTurns();
			}
		}
	}
}
