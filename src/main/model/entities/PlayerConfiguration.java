package model.entities;


import java.util.Objects;


/**
 * Immutable setup data used to construct a human or computer player.
 */
public record PlayerConfiguration(
		String name,
		PlayerType type,
		ComputerDifficulty difficulty
) {
	public PlayerConfiguration {
		Objects.requireNonNull(name, "name cannot be null.");
		Objects.requireNonNull(type, "type cannot be null.");
		difficulty = type == PlayerType.COMPUTER
				? Objects.requireNonNullElse(difficulty, ComputerDifficulty.MEDIUM)
				: null;
	}

	public static PlayerConfiguration human(String name) {
		return new PlayerConfiguration(name, PlayerType.HUMAN, null);
	}

	public static PlayerConfiguration computer(String name, ComputerDifficulty difficulty) {
		return new PlayerConfiguration(name, PlayerType.COMPUTER, difficulty);
	}

	public boolean isComputer() {
		return type == PlayerType.COMPUTER;
	}
}
