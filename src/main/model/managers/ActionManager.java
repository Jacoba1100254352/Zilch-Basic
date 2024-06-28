package model.managers;


import model.entities.Player;


public class ActionManager
{
	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final IScoreManager scoreManager;
	
	public ActionManager(IPlayerManager playerManager, IDiceManager diceManager, IScoreManager scoreManager) {
		this.playerManager = playerManager;
		this.diceManager = diceManager;
		this.scoreManager = scoreManager;
	}
	
	// Game-related actions
	public void endTurn() {
		System.err.println("End turn action not implemented.");
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
}
