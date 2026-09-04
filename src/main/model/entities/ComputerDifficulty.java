package model.entities;


/**
 * Supported decision strengths for an automated Zilch player.
 */
public enum ComputerDifficulty
{
	EASY("Easy"),
	MEDIUM("Medium"),
	HARD("Hard");

	private final String displayName;

	ComputerDifficulty(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
