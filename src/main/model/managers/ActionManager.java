package model.managers;

import model.entities.Player;
import rules.managers.IRuleManager;
import rules.managers.RuleType;
import rules.models.IRule;


public class ActionManager
{
	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final IRuleManager ruleManager;
	
	public ActionManager(IPlayerManager playerManager, IDiceManager diceManager, IRuleManager ruleManager) {
		this.playerManager = playerManager;
		this.diceManager = diceManager;
		this.ruleManager = ruleManager;
	}
	
	// Game-related actions
	public void endTurn() {
		applyRule(RuleType.END_TURN);
	}
	
	// Player-related actions
	public void switchToNextPlayer() {
		playerManager.switchToNextPlayer();
	}
	
	public Player getCurrentPlayer() {
		return playerManager.getCurrentPlayer();
	}
	
	public Player findHighestScoringPlayer() {
		return playerManager.findHighestScoringPlayer();
	}
	
	public Player getGameEndingPlayer() {
		return playerManager.getGameEndingPlayer();
	}
	
	public void setGameEndingPlayer(Player player) {
		playerManager.setGameEndingPlayer(player);
	}
	
	// Dice-related actions
	public void rollDice() {
		Player currentPlayer = getCurrentPlayer();
		diceManager.rollDice(currentPlayer.dice());
	}
	
	public void replenishAllDice() {
		Player currentPlayer = getCurrentPlayer();
		diceManager.replenishAllDice(currentPlayer.dice());
	}
	
	public void removeDice(int dieValue) {
		Player currentPlayer = getCurrentPlayer();
		diceManager.removeDice(currentPlayer.dice(), dieValue);
	}
	
	public void removeDice(int dieValue, int numToRemove) {
		Player currentPlayer = getCurrentPlayer();
		diceManager.removeDice(currentPlayer.dice(), dieValue, numToRemove);
	}
	
	public void removeAllDice() {
		Player currentPlayer = getCurrentPlayer();
		diceManager.removeAllDice(currentPlayer.dice());
	}
	
	// Score-related actions
	public void applyRule(RuleType ruleType) {
		Player currentPlayer = getCurrentPlayer();
		IRule rule = ruleManager.getRule(ruleType);
		if (rule != null) {
			rule.apply(this, currentPlayer);
		} else {
			System.err.println("Rule not found: " + ruleType);
		}
	}
}
