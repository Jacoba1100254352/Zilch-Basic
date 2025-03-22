// GameManager.java
import java.util.ArrayList;
import java.util.List;

public class GameManager {
	private final List<Player> players;
	private Player currentPlayer;
	private int valueOfChosenMultiple;
	private boolean selectedOptionStatus;
	private boolean turnContinuationStatus;
	private boolean selectionContinuationStatus;
	private int currentPlayerIndex;  // To track which player is current
	
	public enum PrintOptions {
		ENTER, NEXT, REENTER
	}
	
	public GameManager() {
		players = new ArrayList<>();
		currentPlayer = null;
		valueOfChosenMultiple = 0;
		selectedOptionStatus = false;
		turnContinuationStatus = true;
		selectionContinuationStatus = true;
		currentPlayerIndex = 0;
	}
	
	public void initializeRollCycle() {
		turnContinuationStatus = true;
		selectedOptionStatus = false;
		selectionContinuationStatus = true;
		valueOfChosenMultiple = 0;
		currentPlayer.getScore().setScoreFromMultiples(0);
	}
	
	public void playGame() {
		GameUI.displayGameInfo();
		GameUI.pauseAndContinue();
		GameUI.clear();
		
		enterAndAddPlayers();
		enterAndSetScoreLimit();
		
		while (true) {
			for (Player player : players) {
				if (player.getScore().getPermanentScore() >= player.getScore().getScoreLimit()) {
					manageLastTurnOpportunity();
					manageTiedEnding();
					return;
				}
				currentPlayer = player;
				manageDiceCount(Constants.FULL_SET_OF_DICE);
				while (turnContinuationStatus) {
					currentPlayer.getDice().rollDice();
					new Checker(this).check();
				}
			}
		}
	}
	
	public void manageLastTurnOpportunity() {
		Player gameEndingPlayer = currentPlayer;
		System.out.println(gameEndingPlayer.getName() + " is over " + gameEndingPlayer.getScore().getScoreLimit());
		System.out.println("Everyone else has one more chance to win");
		GameUI.pauseAndContinue();
		System.out.println("\n");
		
		switchToNextPlayer();
		
		do {
			System.out.println("It is " + currentPlayer.getName() + "'s last turn");
			manageDiceCount(Constants.FULL_SET_OF_DICE);
			while (turnContinuationStatus) {
				gameEndingPlayer.getScore().displayHighScoreInfo(currentPlayer.getName(), gameEndingPlayer.getName());
				currentPlayer.getDice().rollDice();
				new Checker(this).check();
			}
			switchToNextPlayer();
		} while (currentPlayer != gameEndingPlayer);
	}
	
	public void manageTiedEnding() {
		Player playerWithHighestScore = findHighestScoringPlayer();
		int highestScore = playerWithHighestScore.getScore().getPermanentScore();
		List<Player> tie = new ArrayList<>();
		
		for (Player player : players) {
			if (player.getScore().getPermanentScore() == highestScore) {
				tie.add(player);
			}
		}
		
		if (tie.size() > 1) {
			for (int i = 0; i < tie.size(); i++) {
				System.out.print(tie.get(i).getName());
				if (i != tie.size() - 1) {
					System.out.print((i == tie.size() - 2) ? " and " : ", ");
				}
			}
			System.out.println(" have tied with " + highestScore + " Points!");
		} else {
			System.out.println(playerWithHighestScore.getName() + " won with " + highestScore + " Points!");
		}
	}
	
	public void manageDiceCount(int numOfDice) {
		if (numOfDice == 0) {
			numOfDice = Constants.FULL_SET_OF_DICE;
		}
		currentPlayer.getDice().setNumDiceInPlay(numOfDice);
		if (numOfDice == Constants.FULL_SET_OF_DICE) {
			initializeRollCycle();
		}
	}
	
	private int getNumberOfPlayers() {
		int numPlayers;
		while (true) {
			System.out.print("Enter the number of players (1-6): ");
			if (InputReader.scanner.hasNextInt()) {
				numPlayers = InputReader.scanner.nextInt();
				InputReader.scanner.nextLine(); // consume newline
				if (numPlayers >= 1 && numPlayers <= 6) {
					break;
				}
			} else {
				InputReader.scanner.nextLine(); // consume invalid input
			}
			System.out.println("Invalid number of players. Please try again.");
		}
		return numPlayers;
	}
	
	public void enterAndAddPlayers() {
		int numPlayers = getNumberOfPlayers();
		players.clear();
		for (int i = 0; i < numPlayers; i++) {
			System.out.print("Enter the name of player " + (i + 1) + ": ");
			String playerName = InputReader.scanner.nextLine();
			addPlayer(playerName);
		}
		currentPlayer = players.getFirst();
		currentPlayerIndex = 0;
	}
	
	public void addPlayer(String playerName) {
		players.add(new Player(playerName));
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void switchToNextPlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);
	}
	
	public void enterAndSetScoreLimit() {
		int limit;
		final int MIN_SCORE_LIMIT = 1000;
		while (true) {
			System.out.print("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
			if (InputReader.scanner.hasNextInt()) {
				limit = InputReader.scanner.nextInt();
				InputReader.scanner.nextLine(); // consume newline
				if (limit >= MIN_SCORE_LIMIT) {
					break;
				}
			} else {
				InputReader.scanner.nextLine(); // consume invalid input
			}
			System.out.println("Invalid score limit. Please try again.");
		}
		
		for (Player player : players) {
			player.getScore().setScoreLimit(limit);
		}
	}
	
	public Player findHighestScoringPlayer() {
		Player highestScoringPlayer = currentPlayer;
		for (Player player : players) {
			if (player.getScore().getPermanentScore() > highestScoringPlayer.getScore().getPermanentScore()) {
				highestScoringPlayer = player;
			}
		}
		return highestScoringPlayer;
	}
	
	// Getters and setters for various game state fields.
	public int getValueOfChosenMultiple() {
		return valueOfChosenMultiple;
	}
	
	public void setValueOfChosenMultiple(int chosenMultipleValue) {
		valueOfChosenMultiple = chosenMultipleValue;
	}
	
	public boolean getSelectedOptionStatus() {
		return selectedOptionStatus;
	}
	
	public void setSelectedOptionStatus(boolean isOptionSelected) {
		selectedOptionStatus = isOptionSelected;
	}
	
	public boolean getTurnContinuationStatus() {
		return turnContinuationStatus;
	}
	
	// If not busting, end-of-turn processing is performed.
	public void setTurnContinuationStatus(boolean continueTurn, boolean isBust) {
		if (isBust) {
			turnContinuationStatus = continueTurn;
			return;
		}
		if (currentPlayer.getScore().getRoundScore() >= 1000) {
			currentPlayer.getScore().increasePermanentScore(currentPlayer.getScore().getRoundScore());
			currentPlayer.getScore().setRoundScore(0);
			System.out.println("\n" + currentPlayer.getName() + "'s permanent score has been logged and is now: "
					                   + currentPlayer.getScore().getPermanentScore() + "\n");
			currentPlayer.getDice().setNumDiceInPlay(0);
		} else if (!selectedOptionStatus) {
			System.out.println("You are not allowed to end without a permanent or running score higher than 1000");
			continueTurn = true;
		}
		turnContinuationStatus = continueTurn;
	}
	
	public boolean getSelectionContinuationStatus() {
		return selectionContinuationStatus;
	}
	
	public void setSelectionContinuationStatus(boolean continueSelecting) {
		if (!continueSelecting && !selectedOptionStatus) {
			System.out.println("You must select at least one option");
			continueSelecting = true;
		}
		selectionContinuationStatus = continueSelecting;
	}
}
