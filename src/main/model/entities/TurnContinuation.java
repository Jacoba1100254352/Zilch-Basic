package model.entities;


import java.util.Map;
import java.util.Objects;


/**
 * Immutable snapshot of a banked partial turn that the next eligible player
 * may continue when the Stealing variant is enabled.
 *
 * @param sourcePlayerName player who created the continuation
 * @param inheritedScore score placed at risk when the continuation is accepted
 * @param diceInPlay number of remaining dice the next player must roll
 * @param scoredMultiples multiple bookkeeping carried across the continuation
 */
public record TurnContinuation(
		String sourcePlayerName,
		int inheritedScore,
		int diceInPlay,
		Map<Integer, Integer> scoredMultiples
) {
	public TurnContinuation {
		Objects.requireNonNull(sourcePlayerName, "sourcePlayerName cannot be null.");
		Objects.requireNonNull(scoredMultiples, "scoredMultiples cannot be null.");
		if (sourcePlayerName.isBlank()) {
			throw new IllegalArgumentException("sourcePlayerName cannot be blank.");
		}
		if (inheritedScore <= 0) {
			throw new IllegalArgumentException("inheritedScore must be positive.");
		}
		if (diceInPlay <= 0 || diceInPlay >= Dice.FULL_SET_OF_DICE) {
			throw new IllegalArgumentException("diceInPlay must represent a partial dice set.");
		}
		scoredMultiples = Map.copyOf(scoredMultiples);
	}
}
