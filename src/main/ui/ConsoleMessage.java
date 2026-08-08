package ui;


import config.Config;
import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.IntStream;


/**
 * Console-backed message renderer for setup, turn prompts, and end-of-game
 * announcements.
 */
public class ConsoleMessage implements IMessage
{
	private final int scoreLimit;
	private final Scanner scanner;

	/**
	 * Creates the console UI with a shared scanner and loads the configured score limit.
	 */
	public ConsoleMessage(Scanner scanner) throws IOException {
		this.scoreLimit = new Config("config.properties").getScoreLimit();
		this.scanner = scanner;
	}

	/**
	 * Creates the console UI with a new scanner and loads the configured score limit.
	 */
	public ConsoleMessage() throws IOException {
		this(new Scanner(System.in));
	}

	@Override
	public void displayWelcomeMessage() {
		// Optionally clear the screen before showing the intro.
		clear();
		System.out.println(getWelcomeMessage());
		// Wait for user acknowledgment before proceeding.
		System.out.print("Press enter to continue...");
		System.out.flush();
		scanner.nextLine();
	}

	@Override
	public void displayGameOptions(Score score, List<GameOption> gameOptions) {
		System.out.println("\nAvailable Options:");
		System.out.println("Round score: " + score.getRoundScore());
		int optionNumber = 1;
		for (GameOption option : gameOptions) {
			String valueSuffix = option.selectedValue() == null ? "" : " [" + option.selectedValue() + "]";
			System.out.printf(
					"%d. %s%s - %d points. %s%n",
					optionNumber++,
					option.displayName(),
					valueSuffix,
					option.pointsAwarded(),
					option.description()
			);
		}
	}

	@Override
	public void displayCurrentScore(String playerName, int roundScore) {
		// Optionally clear the screen before printing the active player's score.
		clear();
		System.out.println(playerName + "'s current round score: " + roundScore);
	}

	@Override
	public void displayDice(Dice dice) {
		dice.calculateNumDiceInPlay();
		System.out.println("\nYou have " + dice.getNumDiceInPlay() + " dice left.");
		System.out.println(buildDiceListString(dice));
	}

	@Override
	public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
		System.out.print(generateHighScoreMessage(currentPlayer, highestScoringPlayerName));
	}

	@Override
	public void displayMessage(String message) {
		System.out.print(message);
		System.out.flush();
	}

	@Override
	public void displayAndWait(String message) {
		System.out.print(message);
		System.out.flush();
		// Wait for user input before continuing.
		pauseAndContinue(() -> {
			System.out.print("Press enter to continue...");
			System.out.flush();
			scanner.nextLine();
		});
	}

	@Override
	public void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction) {
		System.out.println(gameEndingPlayer.name() + " is over " + scoreLimit + " points!");
		System.out.println("Everyone else has one more chance to win.");
		pauseAndContinue(waitFunction);
		System.out.println();
	}

	@Override
	public void announceTie(List<Player> tiedPlayers, int score) {
		System.out.println(buildTieAnnouncement(tiedPlayers, score));
	}

	@Override
	public void announceWinner(Player winner, int score) {
		System.out.println(winner.name() + " won with " + score + " points!");
	}

	@Override
	public void displayRulesMenu() {
		System.out.println("Available rules:");
	}

	@Override
	public void clear() {
		// If you prefer not to clear the screen at all, simply do nothing.
		// Alternatively, you could print a few blank lines here.
	}

	@Override
	public void pauseAndContinue(Runnable waitFunction) {
		// Run the provided wait function but do not clear the screen afterward.
		waitFunction.run();
	}

	/**
	 * Builds the static game rules shown when the program starts.
	 */
	private String getWelcomeMessage() {
		return """
				Welcome to Zilch!

				Here are the basic rules:
				1. You must reach the configured opening (on) score before banking points.
				2. Sets (three pairs) and straits (1, 2, 3, 4, 5, 6) give 1000 points.
				3. A group of 3 identical dice gives 100 points times the value of that die.
				4. Each additional identical die doubles the score for that multiple.
				5. Single 1's are worth 100 points and single 5's are worth 50 points.
				""";
	}

	/**
	 * Expands the counted dice map into a comma-separated list for display.
	 */
	private String buildDiceListString(Dice dice) {
		StringBuilder diceList = new StringBuilder();
		dice.getDiceSetMap().forEach((key, value) ->
				IntStream.range(0, value).forEach(index -> diceList.append(key).append(", "))
		);
		return !diceList.isEmpty() ? diceList.substring(0, diceList.length() - 2) : "";
	}

	/**
	 * Generates the current-vs-high-score status message.
	 */
	private String generateHighScoreMessage(Player currentPlayer, String highestScoringPlayerName) {
		StringBuilder message = new StringBuilder();
		Score score = currentPlayer.score();
		if (score.getPermanentScore() < scoreLimit) {
			message.append(currentPlayer.name()).append("'s current score: ").append(score.getRoundScore());
		} else if (!highestScoringPlayerName.equals(currentPlayer.name()) &&
				score.getPermanentScore() > score.getRoundScore()) {
			message.append("\n\nYour current score of ").append(score.getRoundScore())
			       .append(" is ").append(score.getPermanentScore() - score.getRoundScore())
			       .append(" less than ").append(highestScoringPlayerName)
			       .append("'s high score of ").append(score.getPermanentScore())
			       .append(".");
		} else if (!highestScoringPlayerName.equals(currentPlayer.name())) {
			message.append("You are currently tied with the highest scoring player.");
		} else {
			message.append("You are currently the highest scoring player.");
		}
		return message.toString();
	}

	/**
	 * Formats a tie announcement for the supplied players.
	 */
	private String buildTieAnnouncement(List<Player> tiedPlayers, int score) {
		StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + score + " points!");
		tiedPlayers.forEach(player -> joiner.add(player.name()));
		return joiner.toString();
	}
}
