package controllers;


import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;


public class GameEngine
{
	private final GameStateManager gameStateManager;
	private final IEventDispatcher eventDispatcher;
	private final GameOptionManager gameOptionManager;
	private final ActionManager actionManager;
	
	public GameEngine(
			IEventDispatcher eventDispatcher, GameStateManager gameStateManager, ActionManager actionManager,
			GameOptionManager gameOptionManager
	) {
		this.eventDispatcher = eventDispatcher;
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.gameStateManager = gameStateManager;
	}
	
	public void initializeRules() {
		gameStateManager.initializeRules();
	}
	
	public void processGameTurn() {
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("No current player available.");
			return;
		}
		
		actionManager.rollDice();
		
		Integer value = null; // TODO: Get input from user here
		gameOptionManager.evaluateGameOptions(currentPlayer.dice().diceSetMap(), value);
		
		if (gameOptionManager.isValid()) {
			gameOptionManager.applyGameOption(currentPlayer);
		} else {
			System.out.println("No options available, turn skipped.");
		}
		
		checkGameOver(currentPlayer);
	}
	
	public boolean isGameOver() {
		return gameStateManager.isBust();
	}
	
	private void checkGameOver(Player player) {
		if (canTurnEnd(player)) { // gameStateManager.canTurnEnd(player) // TODO: Access from EndTurnRule
			// gameOver = true;
			System.out.println(player.name() + " has won the game!");
			// Assuming you want to notify that a player has won the game
			Event event = new Event(GameEventType.GAME_STATE_CHANGED);
			event.setData(EventDataKey.WINNER, player); // Assuming 'player' is the winner object or data
			
			eventDispatcher.dispatchEvent(event);
		}
	}
}
