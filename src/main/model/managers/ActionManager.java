package model.managers;


import config.Config;
import config.ReadOnlyConfig;
import model.entities.Dice;
import model.entities.Player;
import rules.constant.IConstantRule;
import rules.managers.IRuleManager;
import rules.managers.RuleType;

import java.io.IOException;


public class ActionManager
{
	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final IRuleManager ruleManager;
	private Player gameEndingPlayer;
	
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
		return gameEndingPlayer;
	}
	
	public void setGameEndingPlayer(Player player) {
		this.gameEndingPlayer = player;
	}
	
	public boolean canEndGame(Player player) throws IOException {
		return player.score().getPermanentScore() >= new Config("config.properties").getScoreLimit();
	}
	
	public boolean isGameOver() {
		return gameEndingPlayer != null;
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
