package model.managers;


import model.entities.Dice;
import model.entities.Player;

import java.util.List;


/**
 * Centralizes non-rule game actions such as player rotation, banking scores,
 * dice rolling, and tracking whether the game has entered the final-round flow.
 */
public class ActionManager
{
	public static final int DEFAULT_OPENING_SCORE_LIMIT = 1000;

	private final IPlayerManager playerManager;
	private final IDiceManager diceManager;
	private final int scoreLimit;
	private final int openingScoreLimit;
	private Player gameEndingPlayer;

	/**
	 * Creates the action manager for a game with the supplied collaborators.
	 */
	public ActionManager(IPlayerManager playerManager, IDiceManager diceManager, int scoreLimit) {
		this(playerManager, diceManager, scoreLimit, Math.min(DEFAULT_OPENING_SCORE_LIMIT, scoreLimit));
	}

	/**
	 * Creates an action manager with explicit winning and opening thresholds.
	 */
	public ActionManager(
			IPlayerManager playerManager,
			IDiceManager diceManager,
			int scoreLimit,
			int openingScoreLimit
	) {
		if (scoreLimit <= 0) {
			throw new IllegalArgumentException("scoreLimit must be positive.");
		}
		if (openingScoreLimit < 0) {
			throw new IllegalArgumentException("openingScoreLimit cannot be negative.");
		}
		if (openingScoreLimit > scoreLimit) {
			throw new IllegalArgumentException("openingScoreLimit cannot exceed scoreLimit.");
		}
		this.playerManager = playerManager;
		this.diceManager = diceManager;
		this.scoreLimit = scoreLimit;
		this.openingScoreLimit = openingScoreLimit;
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
	 * Returns the players in turn order for score-aware decision strategies.
	 */
	public List<Player> getPlayers() {
		return List.copyOf(playerManager.getPlayers());
	}

	/**
	 * Finds the player with the highest permanent score.
	 */
	public Player findHighestScoringPlayer() {
		return playerManager.findHighestScoringPlayer();
	}

	/**
	 * Returns every player tied at the current permanent-score maximum, preserving
	 * player order so a no-ties game has a deterministic incumbent fallback.
	 */
	public List<Player> findHighestScoringPlayers() {
		Player highestScoringPlayer = findHighestScoringPlayer();
		if (highestScoringPlayer == null) {
			return List.of();
		}
		int highestScore = highestScoringPlayer.score().getPermanentScore();
		return playerManager.getPlayers()
		                    .stream()
		                    .filter(player -> player.score().getPermanentScore() == highestScore)
		                    .toList();
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
		return hasOpened(player) || player.score().getRoundScore() >= openingScoreLimit;
	}

	/**
	 * Returns whether the player has already banked the configured opening score.
	 */
	public boolean hasOpened(Player player) {
		return player.score().getPermanentScore() >= openingScoreLimit;
	}

	public int getOpeningScoreLimit() {
		return openingScoreLimit;
	}

	public int getScoreLimit() {
		return scoreLimit;
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
