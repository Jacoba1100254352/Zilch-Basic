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
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;


public class GameServer
{
	private final GameEngine gameEngine;
	private final IMessage uiManager;
	private final ActionManager actionManager;
	private final IEventDispatcher eventDispatcher;
	private final String gameID;
	
	public GameServer(
			IEventDispatcher eventDispatcher,
			ActionManager actionManager,
			IRuleManager ruleManager,
			IMessage uiManager,
			int scoreLimit,
			IUserInteraction userInteraction,
			String gameID
	) {
		this.uiManager = uiManager;
		this.actionManager = actionManager;
		this.eventDispatcher = eventDispatcher;
		this.gameID = gameID;
		
		// Initialize the game engine
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager);
		this.gameEngine = new GameEngine(eventDispatcher, gameStateManager, actionManager, gameOptionManager, userInteraction);
		
		// Add a GameOverListener to the event dispatcher
		GameOverListener gameOverListener = new GameOverListener(scoreLimit, this, actionManager, uiManager);
		eventDispatcher.addListener(GameEventType.GAME_OVER, gameOverListener);
	}
	
	public void playGame() {
		uiManager.displayWelcomeMessage();
		System.out.println("Starting Game with ID: " + gameID);
		
		while (!gameEngine.isGameOver()) {
			try {
				playTurn(actionManager.getCurrentPlayer());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		concludeGame();
		System.out.println("Game has ended.");
	}
	
	private void playTurn(Player player) throws IOException {
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
	
	public void handleLastTurns() throws IOException {
		Player initialPlayer = actionManager.getCurrentPlayer();
		do {
			playTurn(actionManager.getCurrentPlayer());
			actionManager.switchToNextPlayer();
		} while (actionManager.getCurrentPlayer() != initialPlayer);
	}
	
	private void concludeGame() {
		Player winner = actionManager.getGameEndingPlayer();
		if (winner == null) {
			winner = actionManager.findHighestScoringPlayer();
		}
		Event gameOverEvent = new Event(GameEventType.GAME_OVER);
		gameOverEvent.setData(EventDataKey.WINNER, winner);
		eventDispatcher.dispatchEvent(gameOverEvent);
	}
}
