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


public class GameOverListener implements IEventListener
{
	private final int scoreLimit;
	private final GameServer gameServer;
	private final IMessage uiManager;
	private final ActionManager actionManager;
	private Player gameEndingPlayer;
	
	public GameOverListener(int scoreLimit, GameServer gameServer, ActionManager actionManager, IMessage uiManager) {
		this.scoreLimit = scoreLimit;
		this.gameServer = gameServer;
		this.uiManager = uiManager;
		this.actionManager = actionManager;
	}
	
	@Override
	public void handleEvent(Event event) throws IOException {
		if (event.getType() == GAME_OVER) {
			Player winner = (Player) event.getData(EventDataKey.WINNER);
			uiManager.announceWinner(winner, winner.score().getPermanentScore());
		} else if (event.getType() == SCORE_UPDATED) {
			Player player = (Player) event.getData(EventDataKey.PLAYER);
			if (player.score().getPermanentScore() >= scoreLimit) {
				System.out.println(player.name() + " has won the game!");
				actionManager.setGameEndingPlayer(player);
				if (gameEndingPlayer == null) {
					gameEndingPlayer = player;
					gameServer.handleLastTurns(); // replace false with isTest if available
				}
			}
		}
	}
}
