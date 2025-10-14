package managers;


import modelManagers.PlayerManager;
import models.GameOption;
import ruleManagers.RuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages game options available to players based on the current game state.
 */
public class GameOptionManager
{
	private final GameCoordinator gameCoordinator;
	private final List<GameOption> gameOptions;
	private GameOption selectedGameOption;
	private Integer previouslySelectedMultipleValue;
	private boolean optionSelectedForCurrentRoll;
	
	public GameOptionManager(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
		this.gameOptions = new ArrayList<>();
		this.selectedGameOption = null;
		this.previouslySelectedMultipleValue = null;
	}
	
	
	///   Main Functions   ///
	
	/**
	 * Evaluates and updates the game options available based on the current state of the game.
	 */
	public void evaluateGameOptions() {
		// Clear existing game options
		gameOptions.clear();
		
		// Add game options based on the current dice state and rules
                addGameOptionsBasedOnRules();
        }

	/**
	 * Checks if the selected game option is a valid move.
	 *
	 * @return true if the move is valid, false otherwise.
	 */
	public boolean isValidMove() {
		// Check if the selected option matches any of the available options
		return gameOptions.stream()
		                  .anyMatch(option -> option.type() == selectedGameOption.type() &&
				                  (option.value() == null || option.value().equals(selectedGameOption.value())));
	}
	
	/**
	 * Processes the selected game option.
	 */
	public void processMove() {
		// Check if an option has been selected
		if (selectedGameOption == null) {
			gameCoordinator.getGameplayUI().displayMessage("No option selected.");
			return;
		}
		
		// Process the move if it's valid
		if (isValidMove()) {
			processSelectedOption();
		} else {
			gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
		}
	}
	
	
	///   Helper Functions   ///
	
	// Adds game options based on the current rules and dice state
        private void addGameOptionsBasedOnRules() {
                final PlayerManager playerManager = gameCoordinator.getPlayerManager();
                final Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());

                RuleContext context = new RuleContext(gameCoordinator, diceSetMap);
                gameOptions.addAll(gameCoordinator.getRuleManager().evaluateOptions(context));
        }
	
	// Processes the selected game option based on its type
	private void processSelectedOption() {
                PlayerManager playerManager = gameCoordinator.getPlayerManager();
                Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
                RuleContext context = new RuleContext(gameCoordinator, diceSetMap);
                gameCoordinator.getRuleManager().applyOption(context, selectedGameOption);

                // Reset the selectedGameOption after processing
                setSelectedGameOption(null);
        }
	
	
	///   Getters and Setters   ///
	
	public List<GameOption> getGameOptions() {
		return gameOptions;
	}
	
	public boolean isOptionSelectedForCurrentRoll() {
		return optionSelectedForCurrentRoll;
	}
	
	public void setOptionSelectedForCurrentRoll(boolean optionSelectedForCurrentRoll) {
		this.optionSelectedForCurrentRoll = optionSelectedForCurrentRoll;
	}
	
	public void setSelectedGameOption(GameOption gameOption) {
		this.selectedGameOption = gameOption;
	}
	
	public Integer getPreviouslySelectedMultipleValue() {
		return previouslySelectedMultipleValue;
	}
	
	public void setPreviouslySelectedMultipleValue(Integer value) {
		this.previouslySelectedMultipleValue = value;
	}
}
