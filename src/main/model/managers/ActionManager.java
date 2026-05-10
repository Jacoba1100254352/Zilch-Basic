package model.managers;


import model.entities.Dice;
import model.entities.Player;


/**
 * Centralizes non-rule game actions such as player rotation, banking scores,
 * dice rolling, and tracking whether the game has entered the final-round flow.
 */
public class ActionManager
{
	private static final int OPENING_SCORE_LIMIT = 1000;

	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final int scoreLimit;
	private Player gameEndingPlayer;

	/**
	 * Creates the action manager for a game with the supplied collaborators.
	 */
	public ActionManager(IPlayerManager playerManager, IDiceManager diceManager, int scoreLimit) {
		this.playerManager = playerManager;
		this.diceManager = diceManager;
		this.scoreLimit = scoreLimit;
	}

	/**
	 * Advances to the next player in turn order.
	 */
	public void switchToNextPlayer() {
		playerManager.switchToNextPlayer();
	}

	/**
	 * Returns the player whose turn is currently active.
	 */
	public Player getCurrentPlayer() {
		return playerManager.getCurrentPlayer();
	}

	/**
	 * Finds the player with the highest permanent score.
	 */
	public Player findHighestScoringPlayer() {
		return playerManager.findHighestScoringPlayer();
	}

	/**
	 * Returns the player who triggered the end-game flow, if any.
	 */
	public Player getGameEndingPlayer() {
		return gameEndingPlayer;
	}

	/**
	 * Records which player triggered the final-round sequence.
	 */
	public void setGameEndingPlayer(Player player) {
		this.gameEndingPlayer = player;
	}

	/**
	 * Returns whether the game has entered its terminal phase.
	 */
	public boolean isGameOver() {
		return gameEndingPlayer != null;
	}

	/**
	 * Returns whether the player has reached the configured game-ending score.
	 */
	public boolean hasReachedScoreLimit(Player player) {
		return player.score().getPermanentScore() >= scoreLimit;
	}

	/**
	 * Determines whether the current player is allowed to bank this round's
	 * points. A player must either have already opened or reach the opening
	 * threshold during the current turn.
	 */
	public boolean canBankPoints(Player player) {
		return player.score().getPermanentScore() >= OPENING_SCORE_LIMIT ||
				player.score().getRoundScore() >= OPENING_SCORE_LIMIT;
	}

	/**
	 * Moves the accumulated round score into the permanent score and resets the
	 * turn-local multiple bookkeeping.
	 */
	public void bankCurrentRound(Player player) {
		player.score().increasePermanentScore(player.score().getRoundScore());
		player.score().setRoundScore(0);
		player.score().setScoreFromMultiples(0);
	}

	private Dice getDice() {
		return playerManager.getCurrentPlayer().dice();
	}

	/**
	 * Rolls the dice currently in play for the active player.
	 */
	public void rollDice() {
		diceManager.rollDice(getDice());
	}

	/**
	 * Restores all six dice for the active player.
	 */
	public void replenishAllDice() {
		diceManager.replenishAllDice(getDice());
	}

	/**
	 * Removes all dice of the supplied value from the active player's pool.
	 */
	public void removeDice(int dieValue) {
		diceManager.removeDice(getDice(), dieValue);
	}

	/**
	 * Removes a specific number of dice of the supplied value.
	 */
	public void removeDice(int dieValue, int numToRemove) {
		diceManager.removeDice(getDice(), dieValue, numToRemove);
	}

	/**
	 * Removes every die from the active player's pool.
	 */
	public void removeAllDice() {
		diceManager.removeAllDice(getDice());
	}
}
