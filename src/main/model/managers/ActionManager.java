package model.managers;


import model.entities.Dice;
import model.entities.Player;
import rules.constantModels.IConstantRule;
import rules.managers.IRuleManager;
import rules.managers.RuleType;


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
		((IConstantRule) ruleManager.getRule(RuleType.END_TURN)).applyAction();
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
	private Dice getDice() {
		return playerManager.getCurrentPlayer().dice();
	}
	
	public void rollAgain() {
		((IConstantRule) ruleManager.getRule(RuleType.ROLL_AGAIN)).applyAction();
	}
	
	public void rollDice() {
		diceManager.rollDice(getDice());
	}
	
	public void replenishAllDice() {
		diceManager.replenishAllDice(getDice());
	}
	
	public void removeDice(int dieValue) {
		diceManager.removeDice(getDice(), dieValue);
	}
	
	public void removeDice(int dieValue, int numToRemove) {
		diceManager.removeDice(getDice(), dieValue, numToRemove);
	}
	
	public void removeAllDice() {
		diceManager.removeAllDice(getDice());
	}
}
