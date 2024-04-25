package modelManagers;


import abstracts.AbstractManager;
import events.Event;
import interfaces.IEventDispatcher;
import models.GameOption;
import models.Player;
import ruleManagers.RuleManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Manages the core game logic and interactions.
 */
public class GameEngine extends AbstractManager
{
	
	private final IEventDispatcher eventDispatcher;
	private final PlayerManager playerManager;
	private final ScoreManager scoreManager;
	private final DiceManager diceManager;
	private final RuleManager ruleManager;
	private boolean gameOver = false;
	private List<GameOption> gameOptions = new ArrayList<>();
	private GameOption selectedGameOption = null;
	private boolean optionSelectedForCurrentRoll = false;
	
	public GameEngine(
			IEventDispatcher eventDispatcher, PlayerManager playerManager,
			ScoreManager scoreManager, DiceManager diceManager, RuleManager ruleManager
	) {
		this.eventDispatcher = eventDispatcher;
		this.playerManager = playerManager;
		this.scoreManager = scoreManager;
		this.diceManager = diceManager;
		this.ruleManager = ruleManager;
	}
	
	/**
	 * Processes each turn of the game.
	 */
	public void processGameTurn() {
		Player currentPlayer = playerManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("No current player available.");
			return;
		}
		
		// Roll dice for the current player
		diceManager.rollDice(currentPlayer.dice());
		
		// Evaluate available game options based on the current dice state
		evaluateGameOptions(currentPlayer);
		
		// Handle game options if available
		if (!gameOptions.isEmpty() && selectedGameOption != null) {
			applyGameOption(selectedGameOption, currentPlayer);
		} else {
			System.out.println("No options available, turn skipped.");
		}
		
		// Check if game over condition is met
		checkGameOver(currentPlayer);
	}
	
	/**
	 * Evaluate game options available to the player.
	 */
	private void evaluateGameOptions(Player player) {
		gameOptions = ruleManager.evaluateRules(player);
	}
	
	/**
	 * Apply a game option selected during a player's turn.
	 *
	 * @param option The game option to apply.
	 * @param player The player to whom the option applies.
	 */
	private void applyGameOption(GameOption option, Player player) {
		System.out.println("Applying game option: " + option.type());
		switch (option.type()) {
			case STRAIT:
				scoreManager.scoreStraits(player.score());
				break;
			case SET:
				scoreManager.scoreSets(player.score());
				break;
			case MULTIPLE:
				scoreManager.scoreMultiple(player.score(), 3, option.value()); // Assuming 3 as a placeholder
				break;
			case SINGLE:
				scoreManager.scoreSingle(player.score(), option.value());
				break;
		}
		eventDispatcher.dispatchEvent("scoreUpdated", new Event("Score Updated", player.name() + " scored points!"));
	}
	
	/**
	 * Checks if the game should end based on the current state.
	 *
	 * @param player The player to check for end game conditions.
	 */
	private void checkGameOver(Player player) {
		if (scoreManager.isGameOverConditionMet(player.score())) {
			gameOver = true;
			System.out.println(player.name() + " has won the game!");
			eventDispatcher.dispatchEvent("gameStateChanged", new Event("Game Over", player.name() + " wins!"));
		}
	}
	
	/**
	 * Determine if the game is over.
	 *
	 * @return true if the game is over, false otherwise.
	 */
	public boolean isGameOver() {
		return gameOver;
	}
	
	// Additional methods for managing game options within the GameEngine
	
	public List<GameOption> getGameOptions() {
		return gameOptions;
	}
	
	public void setSelectedGameOption(GameOption gameOption) {
		this.selectedGameOption = gameOption;
	}
	
	public boolean isOptionSelectedForCurrentRoll() {
		return optionSelectedForCurrentRoll;
	}
	
	public void setOptionSelectedForCurrentRoll(boolean optionSelected) {
		this.optionSelectedForCurrentRoll = optionSelected;
	}
	
	/**
	 * Processes a selected game option, applying its effects to the game state.
	 *
	 * @param option The game option selected by the player.
	 */
	public void processGameOption(GameOption option) {
		Player currentPlayer = playerManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("Error: No current player found.");
			return;
		}
		
		switch (option.type()) {
			case STRAIT:
				scoreManager.scoreStraits(currentPlayer.score());
				playerManager.removeAllDice(currentPlayer);
				break;
			case SET:
				scoreManager.scoreSets(currentPlayer.score());
				playerManager.removeAllDice(currentPlayer);
				break;
			case MULTIPLE:
				scoreManager.scoreMultiple(currentPlayer.score(), option.value(), option.value()); // Example: value used as multiples count
				playerManager.removeDice(currentPlayer, option.value(), 3); // Assuming the count is 3 for simplicity
				break;
			case SINGLE:
				scoreManager.scoreSingle(currentPlayer.score(), option.value());
				playerManager.removeDice(currentPlayer, option.value(), 1);
				break;
		}
		
		// Dispatch an event that an option has been processed
		eventDispatcher.dispatchEvent("optionProcessed", new Event("Option Processed", "Processed " + option.type()));
		checkGameRules(currentPlayer);
	}
	
	/**
	 * Additional method to check for any rule updates or state changes after an option is processed.
	 *
	 * @param player The current player to check rules for.
	 */
	private void checkGameRules(Player player) {
		// Check if game over conditions are met
		if (player.score().getPermanentScore() >= gameEngine.getScoreLimit()) {
			setGameOver(true);
			System.out.println(player.getName() + " wins the game!");
		}
	}
	
	@Override
	protected void initialize() {
	
	}
}
