package ui;


import interfaces.IGameplayUI;
import interfaces.IInputManager;
import modelManagers.PlayerActionManager;
import models.Dice;
import models.GameOption;
import models.Player;
import models.Score;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import static interfaces.IScoreManager.scoreLimit;


/**
 * Manages the user interface for displaying game-related information and interactions.
 */
public class ConsoleGameplayUI implements IGameplayUI
{
	private final PlayerActionManager playerActionManager;
	private final IInputManager inputManager;
	
	
	/**
	 * Constructs a new ConsoleGameplayUI object.
	 *
	 * @param playerActionManager The coordinator of the game's overall logic and flow.
	 */
	public ConsoleGameplayUI(PlayerActionManager playerActionManager, IInputManager inputManager) {
		this.playerActionManager = playerActionManager;
		this.inputManager = inputManager;
	}
	
	///   Main Functions   ///
	
	/**
	 * Displays the welcome message at the start of the game.
	 */
	@Override
	public void displayWelcomeMessage() {
		clear();
		System.out.println(getWelcomeMessage());
	}
	
	/**
	 * Displays the list of available game options for the player to choose from.
	 *
	 * @param gameOptions The list of game options currently available.
	 */
	@Override
	public void displayGameOptions(List<GameOption> gameOptions) {
		// Create local score variable
		Score score = playerActionManager.getScore();
		
		///   Display Game Options   ///
		System.out.println("\nAvailable Options:");
		int optionNumber = 1;
		
		// Display options
		for (GameOption option : gameOptions) {
			// Switch case to determine the description of each game option
			String optionDescription = switch (option.type()) {
				case STRAIT -> "Score a Strait";
				case SET -> "Score a Set";
				case MULTIPLE -> "Score Multiple " + option.value();
				case SINGLE -> "Score Single " + option.value();
			};
			System.out.printf("%d. %s\n", optionNumber++, optionDescription);
		}
		
		// Option to roll again if a score has been made and an option selected in the current roll.
		if (score.getRoundScore() > 0 && gameCoordinator.getGameOptionManager().isOptionSelectedForCurrentRoll()) {
			System.out.printf("%d. Roll again\n", optionNumber++);
		}
		// Option to end turn if the score exceeds 1000.
		if (score.getPermanentScore() >= 1000 || score.getRoundScore() >= 1000) {
			System.out.printf("%d. End Turn\n", optionNumber);
		}
	}
	
	@Override // TODO: Finish this class
	public void displayCurrentScore(String playerName, int roundScore) {
		clear();
		System.out.println(playerName + "'s current score: " + roundScore);
	}
	
	/**
	 * Displays the current dice configuration for the player.
	 */
	@Override
	public void displayDice() {
		Dice dice = gameCoordinator.getPlayerManager().getCurrentPlayer().dice();
		dice.calculateNumDiceInPlay();
		System.out.println("\nYou have " + dice.getNumDiceInPlay() + " dice left.");
		System.out.println(buildDiceListString(dice));
	}
	
	/**
	 * Displays information about the current high score in the game.
	 *
	 * @param currentPlayer            The player currently playing.
	 * @param highestScoringPlayerName The name of the player with the highest score.
	 */
	@Override
	public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
		System.out.print(generateHighScoreMessage(currentPlayer, highestScoringPlayerName));
	}
	
	/**
	 * Displays a generic message to the player.
	 *
	 * @param message The message to display.
	 */
	@Override
	public void displayMessage(String message) {
		System.out.print(message);
	}
	
	/**
	 * Displays a message indicating the start of the last round after a player reaches the score limit.
	 *
	 * @param gameEndingPlayer The player who triggered the last round.
	 */
	@Override
	public void displayLastRoundMessage(Player gameEndingPlayer) {
		// TODO: Verify that scoreLimit here and below are updated values and not the default of 2000
		System.out.println(gameEndingPlayer.name() + " is over " + scoreLimit + " points!");
		System.out.println("Everyone else has one more chance to win");
		pauseAndContinue();
		System.out.println();
	}
	
	/**
	 * Announces a tie among players with the same high score.
	 *
	 * @param tiedPlayers The list of players who have tied.
	 * @param score       The score at which the players have tied.
	 */
	@Override
	public void announceTie(List<Player> tiedPlayers, int score) {
		System.out.println(buildTieAnnouncement(tiedPlayers, score));
	}
	
	/**
	 * Announces the winner of the game.
	 *
	 * @param winner The winning player.
	 * @param score  The winning score.
	 */
	@Override
	public void announceWinner(Player winner, int score) {
		System.out.println(winner.name() + " won with " + score + " Points!");
	}
	
	/**
	 * Clears the screen by printing new lines.
	 */
	@Override
	public void clear() {
		for (int i = 0; i < 50; ++i) System.out.println();
	}
	
	/**
	 * Pauses the game and waits for the player to press enter before continuing.
	 */
	@Override
	public void pauseAndContinue() {
		inputManager.waitForEnterKey();
		clear();
	}
	
	// Retrieves the welcome message string.
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
	
	
	///   Helper Functions   ///
	
	/**
	 * Displays the current score of the player.
	 *
	 * @param currentPlayer The player whose score is to be displayed.
	 */
	@Override
	public void displayCurrentScore(Player currentPlayer) {
		clear();
		System.out.println(currentPlayer.name() + "'s current score: " + currentPlayer.score().getRoundScore());
	}
	
	// Builds a string representing the list of dice values.
	private String buildDiceListString(Dice dice) {
		StringBuilder diceList = new StringBuilder();
		dice.diceSetMap().forEach((key, value) -> IntStream.range(0, value).forEach(i -> diceList.append(key).append(", ")));
		return !diceList.isEmpty() ? diceList.substring(0, diceList.length() - 2) : "";
	}
	
	// Generates a message about the current high score in the game.
	private String generateHighScoreMessage(Player currentPlayer, String highestScoringPlayerName) {
		StringBuilder message = new StringBuilder();
		Score score = currentPlayer.score();
		
		// Compare player's permanent score with score limit.
		if (score.getPermanentScore() < scoreLimit) {
			message.append(currentPlayer.name()).append("'s current score: ").append(score.getRoundScore());
		}
		// Message for players trailing the highest scorer.
		else if (!highestScoringPlayerName.equals(currentPlayer.name()) && score.getPermanentScore() > score.getRoundScore()) {
			message.append("\n\nYour current score of ").append(score.getRoundScore())
			       .append(" is ").append(score.getPermanentScore() - score.getRoundScore())
			       .append(" less than ").append(highestScoringPlayerName)
			       .append("'s High Score of ").append(score.getPermanentScore())
			       .append(". Keep going! :)");
		}
		// Message for players currently tied with the highest scorer.
		else if (!highestScoringPlayerName.equals(currentPlayer.name())) {
			message.append("You are currently tied with the highest scoring player!");
		}
		// Message for the highest scoring player.
		else {
			message.append("You are currently the highest scoring player.");
		}
		
		return message.toString();
	}
	
	
	// Constructs the tie announcement message.
	private String buildTieAnnouncement(List<Player> tiedPlayers, int score) {
		StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + score + " Points!");
		tiedPlayers.forEach(player -> joiner.add(player.name()));
		return joiner.toString();
	}
}
