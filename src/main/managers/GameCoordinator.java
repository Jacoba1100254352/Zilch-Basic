package managers;


import interfaces.IEventDispatcher;
import modelManagers.GameEngine;
import modelManagers.PlayerManager;
import models.Player;
import ui.ConsoleGameplayUI;


/**
 * Unified manager for game setup, gameplay logic, and flow control.
 */
public class GameCoordinator
{
	
	private final GameEngine gameEngine;
	private final PlayerManager playerManager;
	private final ConsoleGameplayUI gameplayUI;
	private Player gameEndingPlayer;
	
	public GameCoordinator(IEventDispatcher eventDispatcher, PlayerManager playerManager, ConsoleGameplayUI gameplayUI) {
		this.gameEngine = new GameEngine(eventDispatcher, playerManager);
		this.playerManager = playerManager;
		this.gameplayUI = gameplayUI;
	}
	
	public void setupGame() {
		playerManager.initializePlayers();
		gameplayUI.displayWelcomeMessage();
		System.out.println("Game setup is complete.");
	}
	
	public void playGame(boolean isTest) {
		System.out.println("Starting the game...");
		while (!gameEngine.isGameOver()) {
			playTurn(playerManager.getCurrentPlayer(), isTest);
			if (isTest) break; // For testing purposes to prevent infinite loops
		}
		concludeGame();
		System.out.println("Game has ended.");
	}
	
	private void playTurn(Player player, boolean isTest) {
		gameplayUI.displayCurrentScore(player);
		gameEngine.processGameTurn(player);
		
		if (gameEngine.checkGameOver(player)) {
			if (gameEndingPlayer == null) {
				gameEndingPlayer = player;
				handleLastTurns(isTest);
			}
		}
		
		if (!gameEngine.isGameOver()) {
			playerManager.switchToNextPlayer();
		}
	}
	
	private void handleLastTurns(boolean isTest) {
		Player initialPlayer = playerManager.getCurrentPlayer();
		do {
			playTurn(playerManager.getCurrentPlayer(), isTest);
			playerManager.switchToNextPlayer();
		} while (playerManager.getCurrentPlayer() != initialPlayer);
	}
	
	private void concludeGame() {
		if (gameEndingPlayer != null) {
			gameplayUI.announceWinner(gameEndingPlayer, gameEndingPlayer.score().getPermanentScore());
		} else {
			Player highestScoringPlayer = playerManager.findHighestScoringPlayer();
			gameplayUI.announceWinner(highestScoringPlayer, highestScoringPlayer.score().getPermanentScore());
		}
	}
}
