package managers;


import modelManagers.PlayerManager;
import models.GameOption;
import models.Player;
import ruleManagers.RuleType;
import types.Multiples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;


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
		Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
		// Leverage RuleEvaluator to get the valid game options based on the current game state
		List<GameOption> validOptions = new RuleEvaluator(gameCoordinator).evaluateRules(currentPlayer);
		
		// Update the game options
		this.gameOptions.clear();
		this.gameOptions.addAll(validOptions);
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
		// TODO: Implement a getPlayerDice method that takes in the currentPlayer and returns their dice (or diceSetMap)
		final PlayerManager playerManager = gameCoordinator.getPlayerManager();
		final Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
		;
		
		if (gameCoordinator.getRuleManager().isRuleValid(RuleType.STRAIT, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.STRAIT, null));
		}
		if (gameCoordinator.getRuleManager().isRuleValid(RuleType.SET, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.SET, null));
		}
		
		for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++) {
			addMultipleAndSingleOptions(dieValue);
		}
	}
	
	// Adds multiple and single game options based on a given die value
	private void addMultipleAndSingleOptions(int dieValue) {
		PlayerManager playerManager = gameCoordinator.getPlayerManager();
		Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
		
		if (gameCoordinator.getRuleManager().isRuleValid(RuleType.MULTIPLE, diceSetMap, null) || gameCoordinator.getRuleManager().isRuleValid(RuleType.ADD_MULTIPLE, diceSetMap, dieValue)) {
			gameOptions.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
		}
		if (dieValue == 1 || dieValue == 5) {
			if (gameCoordinator.getRuleManager().isRuleValid(RuleType.SINGLE, diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(GameOption.Type.SINGLE, dieValue));
			}
		}
	}
	
	// Processes the selected game option based on its type
	private void processSelectedOption() {
		switch (selectedGameOption.type()) {
			case STRAIT -> handleStraits();
			case SET -> handleSets();
			case MULTIPLE -> handleMultiples();
			case SINGLE -> handleSingles();
			default -> gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
		}
		
		// Reset the selectedGameOption after processing
		setSelectedGameOption(null);
	}
	
	// Handles the scoring and dice removal for straits
	private void handleStraits() {
		gameCoordinator.getPlayerManager().scoreStraits();
		gameCoordinator.getPlayerManager().removeAllDice();
	}
	
	// Handles the scoring and dice removal for sets
	private void handleSets() {
		gameCoordinator.getPlayerManager().scoreSets();
		gameCoordinator.getPlayerManager().removeAllDice();
	}
	
	// Handles the scoring and dice removal for multiples
	private void handleMultiples() {
		Multiples multiples = new Multiples(selectedGameOption.value());
		PlayerManager playerManager = gameCoordinator.getPlayerManager();
		
		// Check if the selected multiple is valid
		if (multiples.isValidMultiple(playerManager.getDice(playerManager.getCurrentPlayer()))) {
			int mScore = multiples.getCount(playerManager.getDice(playerManager.getCurrentPlayer())) * multiples.getValue() * 100;
			
			if (playerManager.getScore().getScoreFromMultiples() == 0) {
				playerManager.getScore().increaseRoundScore(mScore);
			} else { // Increase the round score by the difference between the new multiple score and the previous multiple score
				playerManager.getScore().increaseRoundScore(mScore - playerManager.getScore().getScoreFromMultiples());
			}
			
			playerManager.getScore().setScoreFromMultiples(mScore);
		}
	}
	
	// Handles the scoring and dice removal for singles
	private void handleSingles() {
		gameCoordinator.getPlayerManager().scoreSingle(selectedGameOption.value());
		gameCoordinator.getPlayerManager().removeDice(selectedGameOption.value(), 1);
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
