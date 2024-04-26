package managers;


import interfaces.IEventDispatcher;
import interfaces.IScoreManager;
import modelManagers.GameEngine;
import modelManagers.PlayerManager;
import modelManagers.ScoreManager;
import models.Player;
import ui.ConsoleGameplayUI;


public class GameServer
{
	
	private final GameEngine gameEngine;
	private final PlayerManager playerManager;
	private final ConsoleGameplayUI gameplayUI;
	private final IScoreManager scoreManager;
	
	public GameServer(IEventDispatcher eventDispatcher, PlayerManager playerManager, ConsoleGameplayUI gameplayUI, int scoreLimit) {
		this.playerManager = playerManager;
		this.gameplayUI = gameplayUI;
		this.scoreManager = new ScoreManager(scoreLimit);
		this.gameEngine = new GameEngine(eventDispatcher, playerManager, scoreManager); // Assuming GameEngine now also takes ScoreManager
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
		gameplayUI.displayCurrentScore();
		gameEngine.processGameTurn();
		
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
