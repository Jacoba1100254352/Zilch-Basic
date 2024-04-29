package eventHandling.listeners;


import controllers.GameServer;
import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import model.entities.Player;
import model.managers.ActionManager;
import ui.IGameplayUI;

import static eventHandling.events.GameEventType.GAME_OVER;
import static eventHandling.events.GameEventType.SCORE_UPDATED;


public class GameOverListener implements IEventListener
{
	private final int scoreLimit;
	private final GameServer gameServer;
	private final IGameplayUI uiManager;
	private final ActionManager actionManager;
	private Player gameEndingPlayer;
	
	public GameOverListener(int scoreLimit, GameServer gameServer, ActionManager actionManager, IGameplayUI uiManager) {
		this.scoreLimit = scoreLimit;
		this.gameServer = gameServer;
		this.uiManager = uiManager;
		this.actionManager = actionManager;
	}
	
	@Override
	public void handleEvent(Event event) {
		if (event.getType() == GAME_OVER) {
			Player winner = (Player) event.getData(GameEventType.valueOf("winner")); // FIXME: This will need to be fixed, maybe use an enum?
			uiManager.announceWinner(winner, winner.score().getPermanentScore());
		} else if (event.getType() == SCORE_UPDATED) {
			Player player = (Player) event.getData(GameEventType.valueOf("player")); // FIXME: Fix me too
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
