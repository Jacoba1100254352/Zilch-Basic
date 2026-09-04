package ui;


import java.util.Locale;


/**
 * Formats player names in user-facing text, including the legacy second-person
 * name "You".
 */
public final class PlayerText
{
	private PlayerText() {
	}

	/**
	 * Returns whether the supplied display name represents the person playing.
	 */
	public static boolean isSecondPerson(String name) {
		return name != null && name.trim().toLowerCase(Locale.ROOT).equals("you");
	}

	/**
	 * Returns a lower-case possessive phrase suitable within a sentence.
	 */
	public static String possessive(String name) {
		return isSecondPerson(name) ? "your" : name + "'s";
	}

	/**
	 * Returns a possessive phrase suitable at the start of a heading or sentence.
	 */
	public static String possessiveSubject(String name) {
		return isSecondPerson(name) ? "Your" : name + "'s";
	}

	/**
	 * Conjugates a present-tense phrase for either "You" or a named third person.
	 */
	public static String withPresentVerb(String name, String secondPersonVerb, String thirdPersonVerb) {
		return name + " " + (isSecondPerson(name) ? secondPersonVerb : thirdPersonVerb);
	}

	/**
	 * Formats the active-player heading used by visual interfaces.
	 */
	public static String turnHeading(String name) {
		return isSecondPerson(name) ? "Your Turn" : name + "'s Turn";
	}
}
