package modelManagers;


import abstracts.AbstractManager;
import interfaces.IDiceManager;
import interfaces.IPlayerManager;
import interfaces.IScoreManager;
import models.Player;


public class ActionManager extends AbstractManager
{
	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final IScoreManager scoreManager;
	
	public ActionManager(IPlayerManager playerManager, IDiceManager diceManager, IScoreManager scoreManager) {
		this.playerManager = playerManager;
		this.diceManager = diceManager;
		this.scoreManager = scoreManager;
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
	public void scoreStraits() {
		Player currentPlayer = getCurrentPlayer();
		scoreManager.scoreStraits(currentPlayer.score());
	}
	
	public void scoreSets() {
		Player currentPlayer = getCurrentPlayer();
		scoreManager.scoreSets(currentPlayer.score());
	}
	
	public void scoreSingle(int dieValue) {
		Player currentPlayer = getCurrentPlayer();
		scoreManager.scoreSingle(currentPlayer.score(), dieValue);
	}
	
	public void scoreMultiple(int dieValue) {
		Player currentPlayer = getCurrentPlayer();
		scoreManager.scoreMultiple(currentPlayer.score(), currentPlayer.dice().diceSetMap().get(dieValue), dieValue);
	}
	
	public int getScoreLimit() {
		return scoreManager.getScoreLimit();
	}
	
	@Override
	protected void initialize() {
		// Initialization logic for actions, if necessary
	}
}
