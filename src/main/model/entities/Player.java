package model.entities;


import java.util.Objects;


/**
 * Runtime player data, including whether gameplay decisions are local or automated.
 */
public record Player(
		String name,
		Dice dice,
		Score score,
		PlayerType type,
		ComputerDifficulty difficulty
) {
	/**
	 * Backward-compatible constructor for a local human player.
	 */
	public Player(String name, Dice dice, Score score) {
		this(name, dice, score, PlayerType.HUMAN, null);
	}

	public Player(PlayerConfiguration configuration, Dice dice, Score score) {
		this(
				configuration.name(),
				dice,
				score,
				configuration.type(),
				configuration.difficulty()
		);
	}

	public Player {
		Objects.requireNonNull(name, "name cannot be null.");
		Objects.requireNonNull(dice, "dice cannot be null.");
		Objects.requireNonNull(score, "score cannot be null.");
		Objects.requireNonNull(type, "type cannot be null.");
		difficulty = type == PlayerType.COMPUTER
				? Objects.requireNonNullElse(difficulty, ComputerDifficulty.MEDIUM)
				: null;
	}

	public boolean isComputer() {
		return type == PlayerType.COMPUTER;
	}
}
