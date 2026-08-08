package controllers;


import controllers.state.TurnContext;
import model.entities.Dice;
import model.entities.Player;
import model.entities.TurnContinuation;

import java.util.Optional;


/**
 * Owns the cross-player continuation offered by the optional Stealing variant.
 * A continuation is consumed by either accepting it, declining it, or busting.
 */
public class StealingManager
{
	private final boolean enabled;
	private final int openingScoreLimit;
	private TurnContinuation availableContinuation;
	private Player sourcePlayer;

	public StealingManager(boolean enabled, int openingScoreLimit) {
		if (openingScoreLimit < 0) {
			throw new IllegalArgumentException("openingScoreLimit cannot be negative.");
		}
		this.enabled = enabled;
		this.openingScoreLimit = openingScoreLimit;
	}

	/**
	 * Captures a successfully stopped turn when one to five dice remain. A bust,
	 * hot-dice completion, or disabled variant clears any prior offer.
	 */
	public void offerContinuation(TurnContext turnContext) {
		Player player = turnContext.getPlayer();
		int diceInPlay = player.dice().getNumDiceInPlay();
		int roundScore = player.score().getRoundScore();
		if (!enabled || turnContext.isBusted() || roundScore <= 0 ||
				diceInPlay <= 0 || diceInPlay >= Dice.FULL_SET_OF_DICE) {
			clearContinuation();
			return;
		}

		availableContinuation = new TurnContinuation(
				player.name(),
				roundScore,
				diceInPlay,
				turnContext.getScoredMultiples()
		);
		sourcePlayer = player;
	}

	/**
	 * Returns whether the current player may accept the available continuation.
	 * Stealing never helps a player reach their opening score: they must already
	 * have that amount banked before the offer is available to them.
	 */
	public boolean canSteal(Player player) {
		return enabled && availableContinuation != null && player != sourcePlayer &&
				player.score().getPermanentScore() >= openingScoreLimit;
	}

	/**
	 * Applies and consumes the available continuation.
	 */
	public TurnContinuation acceptContinuation(TurnContext turnContext) {
		if (!canSteal(turnContext.getPlayer())) {
			throw new IllegalStateException("No eligible stealing continuation is available.");
		}

		TurnContinuation continuation = availableContinuation;
		clearContinuation();
		turnContext.continueFrom(continuation);
		return continuation;
	}

	public Optional<TurnContinuation> getAvailableContinuation() {
		return Optional.ofNullable(availableContinuation);
	}

	public boolean hasAvailableContinuation() {
		return availableContinuation != null;
	}

	public void clearContinuation() {
		availableContinuation = null;
		sourcePlayer = null;
	}
}
