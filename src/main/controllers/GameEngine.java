package controllers;

import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IUserInteraction;

import java.io.IOException;


public class GameEngine
{
	private final GameStateManager gameStateManager;
	private final IEventDispatcher eventDispatcher;
	private final GameOptionManager gameOptionManager;
	private final ActionManager actionManager;
	private final IUserInteraction userInteraction;
	
	public GameEngine(
			IEventDispatcher eventDispatcher, GameStateManager gameStateManager, ActionManager actionManager,
			GameOptionManager gameOptionManager, IUserInteraction userInteraction
	) {
		this.eventDispatcher = eventDispatcher;
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.gameStateManager = gameStateManager;
		this.userInteraction = userInteraction;
	}
	
	public void initializeRules() {
		gameStateManager.initializeRules();
	}
	
	public void processGameTurn() throws IOException {
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("No current player available.");
			return;
		}
		
		actionManager.rollDice();
		
		Integer value = userInteraction.getOptionValue(); // Implement this method in IUserInteraction
		gameOptionManager.evaluateGameOptions(currentPlayer.dice().diceSetMap(), value);
		
		if (gameOptionManager.isValid()) {
			gameOptionManager.applyGameOption(currentPlayer, null);
		} else {
			System.out.println("No options available, turn skipped.");
		}
		
		checkGameOver(currentPlayer);
	}
	
	public boolean isGameOver() {
		return actionManager.isGameOver();
	}
	
	private void checkGameOver(Player player) throws IOException {
		if (actionManager.canEndGame(player)) {
			System.out.println(player.name() + " has won the game!");
			Event event = new Event(GameEventType.GAME_OVER);
			event.setData(EventDataKey.WINNER, player);
			eventDispatcher.dispatchEvent(event);
		}
	}
}
