// Checker.java
import java.util.Map;

public class Checker {
	private final GameManager game;
	
	public Checker(GameManager game) {
		this.game = game;
	}
	
	public void handleFirstRollBust() {
		game.getCurrentPlayer().getDice().displayDice();
		System.out.println("\nYou have busted on the first roll, try again");
		GameUI.pauseAndContinue();
		game.getCurrentPlayer().getScore().increaseRoundScore(50);
		game.setTurnContinuationStatus(true, true);
	}
	
	public void handleBust() {
		game.getCurrentPlayer().getDice().displayDice();
		System.out.println("\nYou have busted");
		GameUI.pauseAndContinue();
		game.getCurrentPlayer().getScore().setRoundScore(0);
		game.setTurnContinuationStatus(false, true);
	}
	
	public void printPossibleMultipleAddition() {
		if (canAddMultiples() && game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200) {
			System.out.println("You can add to your multiple of " + game.getValueOfChosenMultiple() + "!\n");
		}
	}
	
	// In Java with Scanner, we generally do not need to “ignore remaining input”
	public static void ignoreRemainingInput() {
		// This method is a no-op when using Scanner properly.
	}
	
	public boolean enterEndTurnOption() {
		int playOrEndTurn = -1;
		System.out.print("Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
		while (true) {
			if (InputReader.scanner.hasNextInt()) {
				playOrEndTurn = InputReader.scanner.nextInt();
				InputReader.scanner.nextLine(); // consume newline
				if (playOrEndTurn >= 0 && playOrEndTurn <= 2) {
					break;
				}
			} else {
				InputReader.scanner.nextLine();
			}
			GameUI.clear();
			System.out.print("Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
		}
		return (playOrEndTurn == 2) && (game.getCurrentPlayer().getScore().getRoundScore() >= 1000);
	}
	
	public void updateGameStatus(int playOrEndTurn) {
		game.setTurnContinuationStatus(playOrEndTurn != 2, false);
		game.setSelectionContinuationStatus(playOrEndTurn == 1);
	}
	
	public void handleNoOptionsLeft() {
		GameUI.clear();
		game.getCurrentPlayer().getScore().displayCurrentScore(game.getCurrentPlayer().getName());
		game.getCurrentPlayer().getDice().displayDice();
		System.out.println("\nThere are no options left");
		GameUI.pauseAndContinue();
	}
	
	public void check() {
		Player currentPlayer = game.getCurrentPlayer();
		Dice dice = currentPlayer.getDice();
		Score score = currentPlayer.getScore();
		
		game.setSelectedOptionStatus(false);
		
		if (!isOptionAvailable()) {
			if (dice.getNumDiceInPlay() == Constants.FULL_SET_OF_DICE && score.getRoundScore() == 0) {
				handleFirstRollBust();
			} else if (dice.getNumDiceInPlay() >= 1) {
				handleBust();
			} else {
				System.out.println("\nYou have a full set of dice now");
				GameUI.pauseAndContinue();
				game.manageDiceCount(Constants.FULL_SET_OF_DICE);
			}
		} else {
			checkUserInput();
		}
	}
	
	public void checkUserInput() {
		Score score = game.getCurrentPlayer().getScore();
		do {
			GameUI.clear();
			printPossibleMultipleAddition();
			if (!game.getSelectedOptionStatus()) {
				GameUI.printInstructions(game, GameManager.PrintOptions.ENTER);
			} else if (isOptionAvailable()) {
				GameUI.printInstructions(game, GameManager.PrintOptions.NEXT);
			} else {
				break;
			}
			ignoreRemainingInput();
			readInput();
			if (score.getRoundScore() >= 1000) {
				score.displayHighScoreInfo(game.getCurrentPlayer().getName(), game.findHighestScoringPlayer().getName());
				if (enterEndTurnOption()) {
					int playOrEndTurn = 0; // (logic from C++ sample)
					if (playOrEndTurn == 2 && score.getRoundScore() >= 1000) {
						applyPossibleOptions();
					}
					updateGameStatus(playOrEndTurn);
					GameUI.clear();
				}
			} else if (!isOptionAvailable()) {
				handleNoOptionsLeft();
			}
		} while (game.getTurnContinuationStatus() && game.getSelectionContinuationStatus() && isOptionAvailable());
	}
	
	public void checkStraits() {
		if (!isStrait())
			return;
		game.getCurrentPlayer().getScore().increaseRoundScore(1000);
		game.getCurrentPlayer().getDice().diceSetMap.clear();
		game.setSelectedOptionStatus(true);
	}
	
	public void checkSet() {
		if (!isSet())
			return;
		game.getCurrentPlayer().getScore().increaseRoundScore(1000);
		game.getCurrentPlayer().getDice().diceSetMap.clear();
		game.setSelectedOptionStatus(true);
	}
	
	public void checkMultiple(int dieValue) {
		Player currentPlayer = game.getCurrentPlayer();
		Dice dice = currentPlayer.getDice();
		Score score = currentPlayer.getScore();
		
		if (isMultiple() && dice.diceSetMap.getOrDefault(dieValue, 0) >= 3) {
			if (dice.diceSetMap.get(dieValue) > Constants.FULL_SET_OF_DICE) {
				throw new IndexOutOfBoundsException("Error: You have " + dice.diceSetMap.get(dieValue) +
						                                    " dice. \nYou cannot have more than 6 dice.");
			}
			int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
			int numMultiples = dice.diceSetMap.get(dieValue) - 3;
			int mScore = (int) (Math.pow(2, numMultiples) * baseScore);
			score.setScoreFromMultiples(mScore);
			score.increaseRoundScore(mScore);
			game.setValueOfChosenMultiple(dieValue);
		} else if (canAddMultiples()) {
			if ((score.getScoreFromMultiples() < 200) || (dice.diceSetMap.getOrDefault(dieValue, 0) > 3)) {
				throw new IndexOutOfBoundsException("Error: You must have had a multiple selected and cannot have more than 6 dice or 3 added multiples");
			}
			int mScore = (int) (Math.pow(2, dice.diceSetMap.getOrDefault(dieValue, 0)) * score.getScoreFromMultiples());
			score.increaseRoundScore(mScore - score.getScoreFromMultiples());
			score.setScoreFromMultiples(mScore);
		} else {
			return;
		}
		game.manageDiceCount(currentPlayer.getDice().getNumDiceInPlay() - dice.diceSetMap.getOrDefault(dieValue, 0));
		dice.eliminateDice(dieValue);
		dice.calculateMultipleAvailability();
		game.setSelectedOptionStatus(true);
	}
	
	public void checkSingle(int dieValue) {
		if (!((dieValue == 1 && isSingle(dieValue)) || (dieValue == 5 && isSingle(dieValue))))
			return;
		Player currentPlayer = game.getCurrentPlayer();
		Score score = currentPlayer.getScore();
		int count = currentPlayer.getDice().diceSetMap.getOrDefault(dieValue, 0);
		if ((game.getValueOfChosenMultiple() == dieValue && canAddMultiples()) ||
				(count >= 3 && isMultiple())) {
			System.out.println("The option to choose multiples has been automatically applied.\n");
			checkMultiple(dieValue);
		} else if (count >= 1 && count < 3) {
			score.increaseRoundScore((dieValue == 1) ? 100 : 50);
			currentPlayer.getDice().diceSetMap.put(dieValue, count - 1);
			game.manageDiceCount(currentPlayer.getDice().getNumDiceInPlay() - 1);
			game.setSelectedOptionStatus(true);
		}
	}
	
	public void applyPossibleOptions() {
		while (isOptionAvailable()) {
			if (isStrait())
				checkStraits();
			if (isSet())
				checkSet();
			for (int dieValue = 1; dieValue <= Constants.FULL_SET_OF_DICE; dieValue++) {
				if ((isMultiple() && isDesiredMultipleAvailable(dieValue)) ||
						((game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200) &&
								(game.getValueOfChosenMultiple() == dieValue) && canAddMultiples()))
					checkMultiple(dieValue);
			}
			if (isSingle(1))
				checkSingle(1);
			if (isSingle(5))
				checkSingle(5);
		}
	}
	
	public boolean isStrait() {
		Map<Integer, Integer> diceSetMap = game.getCurrentPlayer().getDice().diceSetMap;
		if (diceSetMap.size() != Constants.FULL_SET_OF_DICE)
			return false;
		for (int i = 1; i <= Constants.FULL_SET_OF_DICE; i++) {
			if (diceSetMap.getOrDefault(i, 0) != 1)
				return false;
		}
		return true;
	}
	
	public boolean isSet() {
		Map<Integer, Integer> diceSetMap = game.getCurrentPlayer().getDice().diceSetMap;
		if (diceSetMap.size() != 3 || isStrait() || isMultiple())
			return false;
		for (int count : diceSetMap.values()) {
			if (count != 2)
				return false;
		}
		return true;
	}
	
	public boolean isMultiple() {
		return game.getCurrentPlayer().getDice().isMultipleAvailable();
	}
	
	public boolean isDesiredMultipleAvailable(int desiredMultiple) {
		return game.getCurrentPlayer().getDice().diceSetMap.getOrDefault(desiredMultiple, 0) >= 3;
	}
	
	public boolean canAddMultiples() {
		for (Map.Entry<Integer, Integer> entry : game.getCurrentPlayer().getDice().diceSetMap.entrySet()) {
			if (entry.getKey() == game.getValueOfChosenMultiple()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSingle(int single) {
		if (!(single == 1 || single == 5))
			return false;
		for (int key : game.getCurrentPlayer().getDice().diceSetMap.keySet()) {
			if (key == single)
				return true;
		}
		return false;
	}
	
	public boolean isOptionAvailable() {
		return isStrait() || isSet() || isMultiple() || canAddMultiples() || isSingle(1) || isSingle(5);
	}
	
	public void readInput() {
		char ch = '\0';
		int val = 0;
		// Read until a non-space input is found that is allowed.
		while (true) {
			String inputLine = InputReader.scanner.nextLine().trim();
			if (!inputLine.isEmpty()) {
				ch = inputLine.charAt(0);
				if ("stel123456am0?".indexOf(ch) != -1)
					break;
			}
		}
		switch (ch) {
			case '1': case '2': case '3': case '4': case '5': case '6':
				val = ch - '0';
				if (isStrait() || isSet() || (isMultiple() && isDesiredMultipleAvailable(val))
						|| ((game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200)
						&& (game.getValueOfChosenMultiple() == val) && canAddMultiples())
						|| isSingle(val))
					executeSelection(val);
				else
					displayImpossibleOptionMessage();
				break;
			case 's':
				executeSecondaryCommand();
				break;
			case 'a':
				if (InputReader.scanner.hasNext()) {
					String next = InputReader.scanner.next();
					if (next.equals("l")) {
						InputReader.scanner.nextLine();
						applyPossibleOptions();
					}
				}
				// Fall through to case 'm'
			case 'm':
				if (InputReader.scanner.hasNextInt()) {
					val = InputReader.scanner.nextInt();
					InputReader.scanner.nextLine();
					if (canProcessMultiple(val))
						checkMultiple(val);
					else
						handleFailedMultiple(val);
				}
				break;
			case '0':
				processZeroCommand();
				break;
			case '?':
				displayPossibleOptions();
				break;
			default:
				displayImpossibleOptionMessage();
				break;
		}
	}
	
	public void executeSelection(int val) {
		if (isStrait())
			checkStraits();
		else if (isSet())
			checkSet();
		else if (isMultiple() && isDesiredMultipleAvailable(val))
			checkMultiple(val);
		else if (isSingle(val))
			checkSingle(val);
	}
	
	public void executeSecondaryCommand() {
		char ch = '\0';
		if (!InputReader.scanner.hasNext())
			return;
		ch = InputReader.scanner.next().charAt(0);
		if ((ch == 't' && isStrait()) || (ch == 'e' && isSet()))
			executeSelection(ch - '0');  // (Conversion of char to int as in the original)
		else if ((ch == '1' && isSingle(1)) || (ch == '5' && isSingle(5)))
			checkSingle(ch - '0');
		else
			displayImpossibleOptionMessage();
	}
	
	public boolean canProcessMultiple(int val) {
		return (isMultiple() && isDesiredMultipleAvailable(val)) ||
				((game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200) &&
						(game.getValueOfChosenMultiple() == val) && canAddMultiples());
	}
	
	public void handleFailedMultiple(int chosenMultiple) {
		System.out.println("You have selected an impossible option");
		if (chosenMultiple != 0) {
			for (Map.Entry<Integer, Integer> entry : game.getCurrentPlayer().getDice().diceSetMap.entrySet()) {
				if (isDesiredMultipleAvailable(entry.getKey()))
					System.out.println("\tMultiples, value: " + entry.getKey());
				else if (game.getValueOfChosenMultiple() == entry.getKey())
					System.out.println("Add to Multiple, value: " + entry.getKey());
			}
		} else {
			GameUI.clear();
			System.out.println("There is no available multiple");
		}
	}
	
	public void processZeroCommand() {
		Score score = game.getCurrentPlayer().getScore();
		if (game.getSelectedOptionStatus()) {
			game.setSelectionContinuationStatus(false);
		} else if (score.getRoundScore() >= 1000 && isOptionAvailable()) {
			score.increasePermanentScore(score.getRoundScore());
			System.out.println("Your official score is now: " + score.getPermanentScore());
		} else if (!isOptionAvailable()) {
			GameUI.clear();
		} else {
			GameUI.clear();
			System.out.println("You cannot end without a score higher than 1000");
			GameUI.pauseAndContinue();
		}
	}
	
	public void displayPossibleOptions() {
		System.out.println("Possible options: ");
		if (isStrait())
			System.out.println("\tStrait");
		if (isSet())
			System.out.println("\tSets");
		if (isMultiple()) {
			for (Map.Entry<Integer, Integer> entry : game.getCurrentPlayer().getDice().diceSetMap.entrySet()) {
				if (isDesiredMultipleAvailable(entry.getKey()))
					System.out.println("\tMultiples, value: " + entry.getKey());
			}
		}
		if (canAddMultiples())
			System.out.println("\tAddons, value: " + game.getValueOfChosenMultiple());
		if (isSingle(1) || isSingle(5))
			System.out.println("\tSingles");
		GameUI.pauseAndContinue();
	}
	
	public static void displayImpossibleOptionMessage() {
		GameUI.clear();
		System.out.println("You have selected an impossible option");
	}
}
