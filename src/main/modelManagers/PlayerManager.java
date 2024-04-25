package modelManagers;


import abstracts.AbstractManager;
import interfaces.IDiceManager;
import interfaces.IScoreManager;
import models.Dice;
import models.Player;
import models.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;


public class PlayerManager extends AbstractManager
{
	private final IDiceManager diceManager;
	private final IScoreManager scoreManager;
	private final Vector<Player> players;
	private Player currentPlayer;
	
	public PlayerManager(List<String> playerNames, IDiceManager diceManager, IScoreManager scoreManager) {
		this.diceManager = diceManager;
		this.scoreManager = scoreManager;
		this.players = playerNames.stream()
		                          .map(name -> new Player(name, new Dice(new HashMap<>()), new Score()))
		                          .collect(Collectors.toCollection(Vector::new));
		this.currentPlayer = players.isEmpty() ? null : players.firstElement();
	}
	
	
	///   Main Functions   ///
	
	public void switchToNextPlayer() {
		int currentPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);
	}
	
	public Player findHighestScoringPlayer() {
		Player highestScoringPlayer = currentPlayer;
		for (Player player : players) {
			if (player.score().getPermanentScore() > highestScoringPlayer.score().getPermanentScore()) {
				highestScoringPlayer = player;
			}
		}
		return highestScoringPlayer;
	}
	
	
	///   Getters and Setters   ///
	public List<Player> getPlayers() {
		return players;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	
	/*************
	 *    DICE   *
	 ************/
	public Map<Integer, Integer> getDice(Player player) {
		return player.dice().diceSetMap();
	}
	
	public Dice getDice() {
		return currentPlayer.dice();
	}
	
	public void rollDice() {
		diceManager.rollDice(getDice());
	}
	
	public void replenishAllDice() {
		diceManager.replenishAllDice(getDice());
	}
	
	public void eliminateDice(int dieValue) {
		diceManager.eliminateDice(getDice(), dieValue);
	}
	
	public void removeDice(int dieValue, int numToRemove) {
		diceManager.removeDice(getDice(), dieValue, numToRemove);
	}
	
	public void removeAllDice() {
		diceManager.removeAllDice(getDice());
	}
	
	
	/**************
	 *    SCORE   *
	 *************/
	public Score getScore() {
		return currentPlayer.score();
	}
	
	public void scoreStraits() {
		scoreManager.scoreStraits(getScore());
	}
	
	public void scoreSets() {
		scoreManager.scoreSets(getScore());
	}
	
	public void scoreSingle(int dieValue) {
		scoreManager.scoreSingle(getScore(), dieValue);
	}
	
	public void scoreMultiple(int dieValue) {
		scoreManager.scoreMultiple(getScore(), this.getDice(this.currentPlayer).get(dieValue), dieValue);
	}
	
	public void initializePlayers() {
	
	}
	
	@Override
	protected void initialize() {
	
	}
}
