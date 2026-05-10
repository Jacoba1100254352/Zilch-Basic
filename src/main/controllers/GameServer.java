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


/**
 * Top-level game coordinator. It owns the game loop, the event dispatcher,
 * and the final-round orchestration once a player crosses the score limit.
 */
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

		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		GameStateManager gameStateManager = new GameStateManager(
				gameOptionManager,
				uiManager,
				actionManager,
				userInteraction
		);
		this.gameEngine = new GameEngine(gameStateManager, actionManager);

		GameOverListener gameOverListener = new GameOverListener(scoreLimit, this, actionManager, uiManager);
		eventDispatcher.addListener(GameEventType.GAME_OVER, gameOverListener);
		eventDispatcher.addListener(GameEventType.SCORE_UPDATED, gameOverListener);
	}

	/**
	 * Runs the main game loop until the final-round flow marks the game as over.
	 */
	public void playGame() throws IOException {
		uiManager.displayWelcomeMessage();
		System.out.println("Starting game with ID: " + gameID);

		while (!gameEngine.isGameOver()) {
			playTurn(actionManager.getCurrentPlayer());
		}

		concludeGame();
		System.out.println("Game has ended.");
	}

	/**
	 * Plays a single turn, then broadcasts the resulting score update.
	 */
	private void playTurn(Player player) throws IOException {
		gameEngine.processGameTurn();

		Event scoreUpdatedEvent = new Event(GameEventType.SCORE_UPDATED);
		scoreUpdatedEvent.setData(EventDataKey.PLAYER, player);
		eventDispatcher.dispatchEvent(scoreUpdatedEvent);

		if (!gameEngine.isGameOver()) {
			actionManager.switchToNextPlayer();
		}
	}

	/**
	 * Grants one final turn to every player after the player who triggered the
	 * score limit, preserving turn order.
	 */
	public void handleLastTurns() throws IOException {
		Player gameEndingPlayer = actionManager.getCurrentPlayer();
		actionManager.switchToNextPlayer();

		while (actionManager.getCurrentPlayer() != gameEndingPlayer) {
			playTurn(actionManager.getCurrentPlayer());
			actionManager.switchToNextPlayer();
		}
	}

	/**
	 * Dispatches the final game-over event once the winner is known.
	 */
	private void concludeGame() throws IOException {
		Player winner = actionManager.findHighestScoringPlayer();
		Event gameOverEvent = new Event(GameEventType.GAME_OVER);
		gameOverEvent.setData(EventDataKey.WINNER, winner);
		eventDispatcher.dispatchEvent(gameOverEvent);
	}
}
