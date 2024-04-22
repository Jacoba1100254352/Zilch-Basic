package managers;


import events.Event;
import interfaces.IEventDispatcher;
import modelManagers.DiceManager;
import modelManagers.PlayerManager;
import modelManagers.ScoreManager;
import models.GameOption;
import models.Player;
import ruleManagers.RuleManager;

import java.util.List;


public class GameEngine
{
	private final PlayerManager playerManager;
	private final ScoreManager scoreManager;
	private final DiceManager diceManager;
	private final RuleManager ruleManager;
	private final IEventDispatcher eventDispatcher;
	private final RuleEvaluator ruleEvaluator;
	
	public GameEngine(IEventDispatcher eventDispatcher, RuleManager ruleManager) {
		this.eventDispatcher = eventDispatcher;
		this.ruleManager = ruleManager;
		
		this.playerManager = new PlayerManager();
		
		this.scoreManager = new ScoreManager(playerManager);
		this.diceManager = new DiceManager(playerManager);
		this.ruleEvaluator = new RuleEvaluator(this);
	}
	
	public void setupGame(/* Game setup parameters */) {
		// Setup game, including players, scores, etc.
	}
	
	public void playTurn() {
		Player currentPlayer = playerManager.getCurrentPlayer();
		
		// Roll the dice for the current player
		diceManager.rollDice(currentPlayer.dice());
		
		// Evaluate available game options based on the current dice roll
		List<GameOption> availableOptions = ruleEvaluator.evaluateRules(currentPlayer);
		
		// Logic to select a game option (could be based on user input or AI decision)
		GameOption selectedOption = selectGameOption(availableOptions);
		
		// Process the selected option if it's valid
		if (selectedOption != null && ruleManager.isOptionAvailable(selectedOption, currentPlayer)) {
			// Update score based on the selected option
			scoreManager.processOption(selectedOption, currentPlayer);
			
			// Dispatch an event for score update or any other game state changes
			dispatchGameEvents(currentPlayer);
		}
		
		// Additional logic to proceed to the next turn or handle other aspects of game flow
	}
	
	private GameOption selectGameOption(List<GameOption> availableOptions) {
		// Placeholder for selecting a game option (implementation depends on game UI or AI logic)
		return null; // Example return
	}
	
	private void dispatchGameEvents(Player currentPlayer) {
		// Example: Dispatching a score update event
		Event scoreUpdateEvent = new Event("scoreUpdated");
		scoreUpdateEvent.setData("player", currentPlayer);
		eventDispatcher.dispatchEvent("scoreUpdated", scoreUpdateEvent);
	}
}
