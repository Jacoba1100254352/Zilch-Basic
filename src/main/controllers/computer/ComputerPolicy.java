package controllers.computer;


import java.util.Map;
import java.util.Objects;


/**
 * Tunable decision weights shared by computer option and banking choices.
 */
public record ComputerPolicy(
		String name,
		Map<Integer, Integer> bankThresholdByDice,
		double scoreWeight,
		double remainingDiceWeight,
		double hotDiceWeight,
		double multipleWeight,
		double leadFactor,
		double trailFactor,
		double closingFactor,
		double rollBias
) {
	public ComputerPolicy {
		Objects.requireNonNull(name, "name cannot be null.");
		Objects.requireNonNull(bankThresholdByDice, "bankThresholdByDice cannot be null.");
		bankThresholdByDice = Map.copyOf(bankThresholdByDice);
	}

	public int bankThreshold(int diceInPlay) {
		return bankThresholdByDice.getOrDefault(diceInPlay, 700);
	}
}
