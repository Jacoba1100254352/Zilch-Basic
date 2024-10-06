package controllers;


import config.Config;
import config.ReadOnlyConfig;
import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import eventHandling.listeners.GameOverListener;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import rules.managers.IRuleManager;
import ui.IMessage;

import java.io.IOException;


public class GameServer
{
	private final GameEngine gameEngine;
	private final IMessage uiManager;
	private final ActionManager actionManager;
	private final IEventDispatcher eventDispatcher;
	
	public GameServer(
			IEventDispatcher eventDispatcher, ActionManager actionManager, IRuleManager ruleManager, IMessage uiManager
	) throws IOException {
		this.uiManager = uiManager;
		this.actionManager = actionManager;
		this.eventDispatcher = eventDispatcher;
		int scoreLimit = ((ReadOnlyConfig) new Config("config.properties")).getScoreLimit();
		
		// Initialize the game engine
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager);
		this.gameEngine = new GameEngine(eventDispatcher, gameStateManager, actionManager, gameOptionManager);
		
		// Add a GameOverListener to the event dispatcher
		GameOverListener gameOverListener = new GameOverListener(scoreLimit, this, actionManager, uiManager);
		eventDispatcher.addListener(GameEventType.SCORE_UPDATED, gameOverListener);
		eventDispatcher.addListener(GameEventType.GAME_STATE_CHANGED, gameOverListener); // NOTE: Game Over
	}
	
	public void playGame() {
		playGame(false);
	}
	
	public void playGame(boolean isTest) {
		uiManager.displayWelcomeMessage();
		// TODO: Rule Initialization sequence here
		gameEngine.initializeRules();
		
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
