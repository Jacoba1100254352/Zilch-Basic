package controllers;


import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import eventHandling.listeners.GameOverListener;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import rules.managers.IRuleManager;
import ui.IGameplayUI;


public class GameServer
{
	private final GameEngine gameEngine;
	private final IGameplayUI uiManager;
	private final ActionManager actionManager;
	private final IEventDispatcher eventDispatcher;
	
	public GameServer(IEventDispatcher eventDispatcher, ActionManager actionManager, IRuleManager ruleManager, IGameplayUI uiManager) {
		this.uiManager = uiManager;
		this.actionManager = actionManager;
		this.eventDispatcher = eventDispatcher;
		
		// Initialize the game engine
		GameOptionManager gameOptionManager = new GameOptionManager(actionManager, ruleManager);
		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager);
		this.gameEngine = new GameEngine(eventDispatcher, gameStateManager, actionManager, gameOptionManager);
		
		// Add a GameOverListener to the event dispatcher
		GameOverListener gameOverListener = new GameOverListener(actionManager.getScoreLimit(), this, actionManager, uiManager);
		eventDispatcher.addListener(GameEventType.SCORE_UPDATED, gameOverListener);
		eventDispatcher.addListener(GameEventType.GAME_STATE_CHANGED, gameOverListener); // NOTE: Game Over
	}
	
	public void playGame() {
		uiManager.displayWelcomeMessage();
		playGame(false);
	}
	
	public void playGame(boolean isTest) {
		System.out.println("Starting the game...");
		while (!gameEngine.isGameOver()) {
			playTurn(actionManager.getCurrentPlayer());
			if (isTest) break; // For testing purposes to prevent infinite loops
		}
		concludeGame();
		System.out.println("Game has ended.");
	}
	
	private void playTurn(Player player) {
		uiManager.displayCurrentScore(player.name(), player.score().getRoundScore());
		gameEngine.processGameTurn();
		
		// Dispatch a scoreUpdated event instead of directly checking for game over
		Event scoreUpdatedEvent = new Event(GameEventType.SCORE_UPDATED);
		scoreUpdatedEvent.setData(EventDataKey.PLAYER, player);
		eventDispatcher.dispatchEvent(scoreUpdatedEvent);
		
		if (!gameEngine.isGameOver()) {
			actionManager.switchToNextPlayer();
		}
	}
	
	public void handleLastTurns() {
		Player initialPlayer = actionManager.getCurrentPlayer();
		do {
			playTurn(actionManager.getCurrentPlayer());
			actionManager.switchToNextPlayer();
		} while (actionManager.getCurrentPlayer() != initialPlayer);
	}
	
	private void concludeGame() {
		Event gameOverEvent = new Event(GameEventType.GAME_OVER);
		Player gameEndingPlayer = actionManager.getGameEndingPlayer();
		if (gameEndingPlayer != null) {
			gameOverEvent.setData(EventDataKey.WINNER, gameEndingPlayer);
		} else {
			Player highestScoringPlayer = actionManager.findHighestScoringPlayer();
			gameOverEvent.setData(EventDataKey.WINNER, highestScoringPlayer);
		}
		eventDispatcher.dispatchEvent(gameOverEvent);
	}
}
