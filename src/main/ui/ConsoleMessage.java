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


public class ConsoleMessage implements IMessage
{
	private final int scoreLimit;
	private final Scanner scanner; // We'll use this to wait for user input
	
	public ConsoleMessage() throws IOException {
		// Load the score limit from config
		scoreLimit = new Config("config.properties").getScoreLimit();
		scanner = new Scanner(System.in);
	}
	
	@Override
	public void displayWelcomeMessage() {
		// Optionally clear the screen (or not)
		clear();
		System.out.println(getWelcomeMessage());
		// Wait for user acknowledgment before proceeding.
		System.out.print("Press enter to continue...");
		scanner.nextLine();
	}
	
	@Override
	public void displayGameOptions(Score score, List<GameOption> gameOptions) {
		System.out.println("\nAvailable Options:");
		int optionNumber = 1;
		for (GameOption option : gameOptions) {
			String optionDescription = switch (option.type()) {
				case ADD_MULTIPLE -> "Score Added Multiple " + option.value();
				case STRAIT -> "Score a Strait";
				case SET -> "Score a Set";
				case MULTIPLE -> "Score Multiple " + option.value();
				case SINGLE -> "Score Single " + option.value();
				case ROLL_AGAIN -> "Roll Again";
				case END_TURN -> "End Turn";
			};
			System.out.printf("%d. %s\n", optionNumber++, optionDescription);
		}
	}
	
	@Override
	public void displayCurrentScore(String playerName, int roundScore) {
		// Optionally clear the screen (or not)
		clear();
		System.out.println(playerName + "'s current score: " + roundScore);
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
	}
	
	@Override
	public void displayAndWait(String message) {
		System.out.print(message);
		// Wait for user input before continuing.
		pauseAndContinue(() -> {
			System.out.print("Press enter to continue...");
			scanner.nextLine();
		});
	}
	
	@Override
	public void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction) {
		System.out.println(gameEndingPlayer.name() + " is over " + scoreLimit + " points!");
		System.out.println("Everyone else has one more chance to win");
		pauseAndContinue(waitFunction);
		System.out.println();
	}
	
	@Override
	public void announceTie(List<Player> tiedPlayers, int score) {
		System.out.println(buildTieAnnouncement(tiedPlayers, score));
	}
	
	@Override
	public void announceWinner(Player winner, int score) {
		System.out.println(winner.name() + " won with " + score + " Points!");
	}
	
	@Override
	public void displayRulesMenu() {
		System.out.println("Rules menu is not implemented yet.");
	}
	
	@Override
	public void clear() {
		// If you prefer not to clear the screen at all, simply do nothing.
		// (Alternatively, you could print a smaller number of blank lines.)
		// for (int i = 0; i < 50; ++i) {
		//     System.out.println();
		// }
	}
	
	@Override
	public void pauseAndContinue(Runnable waitFunction) {
		// Run the provided wait function but DO NOT clear the screen afterward.
		waitFunction.run();
	}
	
	private String getWelcomeMessage() {
		return """
				Welcome to Zilch!
				
				Here are the basic rules:
				1. You must score an initial 1000 points to start logging your points.
				2. Sets (three sets of two) and straits (1, 2, 3, 4, 5, 6) give 1000 points.
				3. A group of 3 identical dice give you 100 points times the value of that die. For example:
				\t- A roll of 3 3 3 will give you 300 points.
				\t- An exception is made for a roll of 1 1 1, which will give you 1000 points.
				4. Each additional number added to this group of three doubles the points received from it. For example:
				\t- A roll of 3 3 3 3 or a roll of 3 3 3 and a later roll of 3 in the same turn would give you 600 points.
				\t- If in a group of 3 1's, 2 more 1's are rolled, the score would be 1000*2*2 = 4000.
				5. Finally, only single 1's or 5's are worth points with a single 1 being 100 points and a 5 being 50.
				""";
	}
	
	private String buildDiceListString(Dice dice) {
		StringBuilder diceList = new StringBuilder();
		dice.getDiceSetMap().forEach((key, value) ->
				                             IntStream.range(0, value).forEach(_ -> diceList.append(key).append(", "))
		);
		return !diceList.isEmpty() ? diceList.substring(0, diceList.length() - 2) : "";
	}
	
	private String generateHighScoreMessage(Player currentPlayer, String highestScoringPlayerName) {
		StringBuilder message = new StringBuilder();
		Score score = currentPlayer.score();
		if (score.getPermanentScore() < scoreLimit) {
			message.append(currentPlayer.name()).append("'s current score: ").append(score.getRoundScore());
		} else if (!highestScoringPlayerName.equals(currentPlayer.name()) && score.getPermanentScore() > score.getRoundScore()) {
			message.append("\n\nYour current score of ").append(score.getRoundScore())
			       .append(" is ").append(score.getPermanentScore() - score.getRoundScore())
			       .append(" less than ").append(highestScoringPlayerName)
			       .append("'s High Score of ").append(score.getPermanentScore())
			       .append(". Keep going! :)");
		} else if (!highestScoringPlayerName.equals(currentPlayer.name())) {
			message.append("You are currently tied with the highest scoring player!");
		} else {
			message.append("You are currently the highest scoring player.");
		}
		return message.toString();
	}
	
	private String buildTieAnnouncement(List<Player> tiedPlayers, int score) {
		StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + score + " Points!");
		tiedPlayers.forEach(player -> joiner.add(player.name()));
		return joiner.toString();
	}
}
