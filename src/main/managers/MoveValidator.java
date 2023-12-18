package managers;

import models.Dice;
import models.Player;
import models.Score;

import java.util.Map;
import java.util.Scanner;

import static managers.GameManager.printOptions.ENTER;
import static managers.GameManager.printOptions.NEXT;
import static models.Dice.FULL_SET_OF_DICE;

public class MoveValidator {
    private final GameManager game;

    public MoveValidator(GameManager game) {
        this.game = game;
    }

    ///   OTHER   ///
    public static void ignoreRemainingInput(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public static boolean enterEndTurnOption(GameManager game, Scanner scanner) {
        // Variables
        int playOrEndTurn;

        // Enter Decision
        System.out.print("Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
        while (!scanner.hasNextInt() || (playOrEndTurn = scanner.nextInt()) < 0 || playOrEndTurn > 2) {
            // Handle incorrect input
            System.out.print("Invalid input. Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
            ignoreRemainingInput(scanner); // Clear the buffer
        }

        return playOrEndTurn == 2 && game.getCurrentPlayer().getScore().getRoundScore() >= 1000;
    }

    /********************
     *   CHECK HELPERS   *
     ********************/

    ///   BUST   ///
    void handleFirstRollBust() {
        // bust UI
        game.getCurrentPlayer().getDice().displayDice();
        System.out.println("\nYou have busted on the first roll, try again");
        game.getGameplayUI().pauseAndContinue();

        // Turn and Score updates
        game.getCurrentPlayer().getScore().increaseRoundScore(50);
        game.setTurnContinuationStatus(true, true);
    }

    void handleBust() {
        // bust UI
        game.getCurrentPlayer().getDice().displayDice();
        System.out.println("\nYou have busted");
        game.getGameplayUI().pauseAndContinue();

        // Turn and Score updates
        game.getCurrentPlayer().getScore().setRoundScore(0);
        game.setTurnContinuationStatus(false, true);
    }

    ///   MULTIPLE   ///
    void printPossibleMultipleAddition() {
        if (canAddMultiples() && (game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200))
            System.out.println("You can add to your multiple of " + game.getValueOfChosenMultiple() + "!");
    }

    void updateGameStatus(int playOrEndTurn, boolean isBust) {
        game.setTurnContinuationStatus(playOrEndTurn != 2, isBust);
        game.setSelectionContinuationStatus(playOrEndTurn == 1);
    }

    void handleNoOptionsLeft() {
        game.getGameplayUI().clear();
        game.getCurrentPlayer().getScore().displayCurrentScore(game.getCurrentPlayer().getName());
        // TODO: May be unnecessary
        game.getCurrentPlayer().getDice().displayDice();
        System.out.print("\nThere are no options left");
        game.getGameplayUI().pauseAndContinue();
    }

    /************
     *   CHECK   *
     ************/

    void check() {
        Player currentPlayer = game.getCurrentPlayer();
        Dice dice = currentPlayer.getDice();
        Score score = currentPlayer.getScore();

        // game.getGameplayUI().clear();
        game.setSelectedOptionStatus(false);

        // Handle Busts or full-hand dice re-rolls
        if (!isOptionAvailable()) {
            if (dice.getNumDiceInPlay() == FULL_SET_OF_DICE && score.getRoundScore() == 0) {
                handleFirstRollBust();
            } else if (dice.getNumDiceInPlay() >= 1) {
                handleBust();
            } else {
                System.out.println("\nYou have a full set of dice now");
                game.getGameplayUI().pauseAndContinue();
                game.manageDiceCount(FULL_SET_OF_DICE);
            }
        } else checkUserInput();
    }

    void checkUserInput() {
        Player currentPlayer = game.getCurrentPlayer();
        Score score = currentPlayer.getScore();

        do {
            game.getGameplayUI().clear(); // Clear screen.

            printPossibleMultipleAddition(); // Display options for multiple addition.

            // Print instructions based on game status.
            if (!game.getSelectedOptionStatus())
                game.getGameplayUI().printInstructions(game, ENTER);
            else if (isOptionAvailable())
                game.getGameplayUI().printInstructions(game, NEXT);
            else break;

            Scanner scanner = new Scanner(System.in);
            ignoreRemainingInput(scanner); // Clear remaining input.
            readInput(scanner); // Read user input.

            // Process scoring and end of turn options.
            if (score.getRoundScore() >= 1000) {
                score.displayHighScoreInfo(currentPlayer.getName(), game.findHighestScoringPlayer().getName());

                //
                if (enterEndTurnOption(game, scanner)) {
                    final int playOrEndTurn = 0;
                    // Apply options and update game status if user chose to end turn.
                    if (playOrEndTurn == 2 && score.getRoundScore() >= 1000)
                        applyPossibleOptions();
                    updateGameStatus(playOrEndTurn, false);
                    game.getGameplayUI().clear(); // Clear screen.
                }

                // Handle scenario where no options are left.
            } else if (!isOptionAvailable())
                handleNoOptionsLeft();
        }
        while (game.getTurnContinuationStatus() && game.getSelectionContinuationStatus() && isOptionAvailable());
        // Loop as long as options available and turn continues.
    }


    void checkStraits() {
        if (!isStrait())
            return;

        game.getCurrentPlayer().getScore().increaseRoundScore(1000);
        game.getCurrentPlayer().getDice().getDiceSetMap().clear();
        game.setSelectedOptionStatus(true);
    }

    void checkSet() {
        if (!isSet())
            return;

        game.getCurrentPlayer().getScore().increaseRoundScore(1000);
        game.getCurrentPlayer().getDice().getDiceSetMap().clear();
        game.setSelectedOptionStatus(true);
    }

    void checkMultiple(int dieValue) {
        // Variables
        Player currentPlayer = game.getCurrentPlayer();
        Dice dice = currentPlayer.getDice();
        Score score = currentPlayer.getScore();

        ///   MULTIPLE   ///
        if (isMultiple() && dice.getDiceSetMap().getOrDefault(dieValue, 0) >= 3) {
            // Error Checking
            if (dice.getDiceSetMap().get(dieValue) > FULL_SET_OF_DICE) {
                throw new IllegalArgumentException(
                        "Error: You have " + dice.getDiceSetMap().get(dieValue) +
                                " dice. \nYou cannot have more than 6 dice.");
            }

            // Variables
            int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
            int numMultiples = dice.getDiceSetMap().get(dieValue) - 3;
            int mScore = (int) Math.pow(2, numMultiples) * baseScore;

            // Handle Multiples Found
            score.setScoreFromMultiples(mScore);
            score.increaseRoundScore(mScore);
            game.setValueOfChosenMultiple(dieValue);

            ///   ADDING MULTIPLE   ///
        } else if (canAddMultiples()) {
            // Error Checking
            if ((score.getScoreFromMultiples() < 200) || (dice.getDiceSetMap().getOrDefault(dieValue, 0) > 3)) {
                throw new IllegalArgumentException(
                        "Error: You must have had a multiple selected and cannot have more than 6 dice or 3 added multiples");
            }

            // Variables
            int mScore = (int) Math.pow(2, dice.getDiceSetMap().getOrDefault(dieValue, 0)) * score.getScoreFromMultiples();

            // Handle Adding Multiple
            score.increaseRoundScore(mScore - score.getScoreFromMultiples());
            score.setScoreFromMultiples(mScore);
        } else {
            return;
        }

        // Multiple Updates (adding and regular)
        game.manageDiceCount(currentPlayer.getDice().getNumDiceInPlay() - dice.getDiceSetMap().getOrDefault(dieValue, 0));
        dice.eliminateDice(dieValue);
        dice.calculateMultipleAvailability();
        game.setSelectedOptionStatus(true);
    }

    void checkSingle(int dieValue) {
        // Error Checking
        if (!((dieValue == 1 && isSingle(dieValue)) || (dieValue == 5 && isSingle(dieValue)))) {
            return;
        }

        // Variables
        Player currentPlayer = game.getCurrentPlayer();
        Score score = currentPlayer.getScore();
        Map<Integer, Integer> diceSetMap = currentPlayer.getDice().getDiceSetMap();

        // Force Multiple option if Available
        if ((game.getValueOfChosenMultiple() == dieValue && canAddMultiples()) ||
                (diceSetMap.getOrDefault(dieValue, 0) >= 3 && isMultiple())) {
            System.out.println("The option to choose multiples has been automatically applied.\n");
            checkMultiple(dieValue);
        } else if (1 <= diceSetMap.getOrDefault(dieValue, 0) && diceSetMap.get(dieValue) < 3) {
            // Apply Singles if available
            score.increaseRoundScore((dieValue == 1) ? 100 : 50);
            diceSetMap.put(dieValue, diceSetMap.get(dieValue) - 1);
            game.manageDiceCount(currentPlayer.getDice().getNumDiceInPlay() - 1);
            game.setSelectedOptionStatus(true);
        }
    }

    void applyPossibleOptions() {
        while (isOptionAvailable()) {
            if (isStrait())
                checkStraits();
            if (isSet())
                checkSet();
            for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++)
                if ((isMultiple() && isDesiredMultipleAvailable(dieValue)) || (
                        (game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200) && (
                                game.getValueOfChosenMultiple() == dieValue) && canAddMultiples()))
                    checkMultiple(dieValue);

            if (isSingle(1))
                checkSingle(1);
            if (isSingle(5))
                checkSingle(5);
        }
    }

    boolean isStrait() {
        Map<Integer, Integer> diceSetMap = game.getCurrentPlayer().getDice().getDiceSetMap();
        return (diceSetMap.size() == FULL_SET_OF_DICE) &&
                diceSetMap.entrySet().stream().allMatch(e -> e.getKey() == (e.getValue() == 1 ? 1 : 0));
    }

    boolean isSet() {
        return (game.getCurrentPlayer().getDice().getDiceSetMap().size() == 3 && !isStrait() && !isMultiple()) &&
                game.getCurrentPlayer().getDice().getDiceSetMap().values().stream()
                        .allMatch(count -> count == 2);
    }

    boolean isMultiple() {
        return game.getCurrentPlayer().getDice().isMultipleAvailable();
    }

    boolean isDesiredMultipleAvailable(int desiredMultiple) {
        return game.getCurrentPlayer().getDice().getDiceSetMap().getOrDefault(desiredMultiple, 0) >= 3;
    }

    boolean canAddMultiples() {
        return game.getCurrentPlayer().getDice().getDiceSetMap().containsKey(game.getValueOfChosenMultiple());
    }

    boolean isSingle(int single) {
        return ((single == 1 || single == 5) &&
                game.getCurrentPlayer().getDice().getDiceSetMap().containsKey(single));
    }

    boolean isOptionAvailable() {
        return (isStrait() || isSet() || isMultiple() || canAddMultiples() || isSingle(1) || isSingle(5));
    }


    /*******************************
     *   INPUT HANDLING FUNCTIONS   *
     *******************************/

    public void readInput(Scanner input) {
        Player currentPlayer = game.getCurrentPlayer();
        Score score = currentPlayer.getScore();

        char ch;
        int val;

        // Keep reading input until we get a meaningful character
        do {
            String next = input.next();
            ch = !next.isEmpty() ? next.charAt(0) : '\0';
        } while (ch == ' ' || ("stel123456am0?".indexOf(ch) == -1));

        switch (ch) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
                val = Character.getNumericValue(ch); // Convert char digit to number
                if (isStrait() || isSet() || (isMultiple() && isDesiredMultipleAvailable(val))
                        || ((score.getScoreFromMultiples() >= 200) && (game.getValueOfChosenMultiple() == val) && (
                        canAddMultiples()))
                        || isSingle(val)) {
                    executeSelection(val);
                } else {
                    displayImpossibleOptionMessage();
                }
                break;
            case 's':
                executeSecondaryCommand(input);
                break;
            case 'a':
                ch = input.next().charAt(0);
                game.getGameplayUI().clear();
                input.nextLine(); // Ignore the rest of the line
                if (ch == 'l') {
                    applyPossibleOptions();
                }
                // Falls through to case 'm' intentionally
            case 'm':
                val = input.nextInt();
                if (canProcessMultiple(val)) {
                    checkMultiple(val);
                } else {
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

    // Execute dice selection based on the value
    private void executeSelection(int val) {
        if (isStrait())
            checkStraits();
        else if (isSet())
            checkSet();
        else if (isMultiple() && isDesiredMultipleAvailable(val))
            checkMultiple(val);
        else if (isSingle(val))
            checkSingle(val);
    }

    // Handles 's' command
    private void executeSecondaryCommand(Scanner input) {
        char ch = input.next().charAt(0);
        if ((ch == 't' && isStrait()) || (ch == 'e' && isSet())) {
            executeSelection(ch);
        } else if ((ch == '1' && isSingle(1)) || (ch == '5' && isSingle(5))) {
            checkSingle(Character.getNumericValue(ch));
        } else {
            displayImpossibleOptionMessage();
        }
    }

    // Handles failed 'm' command
    private boolean canProcessMultiple(int val) {
        return (isMultiple() && isDesiredMultipleAvailable(val)) || (
                (game.getCurrentPlayer().getScore().getScoreFromMultiples() >= 200) &&
                        (game.getValueOfChosenMultiple() == val) && (canAddMultiples()));
    }

    private void handleFailedMultiple(int chosenMultiple) {
        System.out.println("You have selected an impossible option");

        if (chosenMultiple != 0) {
            for (int key : game.getCurrentPlayer().getDice().getDiceSetMap().keySet()) {
                if (isDesiredMultipleAvailable(key))
                    System.out.println("\tMultiples, value: " + key);
                else if (game.getValueOfChosenMultiple() == key)
                    System.out.println("Add to Multiple, value: " + key);
            }
        } else {
            game.getGameplayUI().clear();
            System.out.println("There is no available multiple");
        }
    }

    // Handles '0' command
    private void processZeroCommand() {
        Score score = game.getCurrentPlayer().getScore();
        if (game.getSelectedOptionStatus()) {
            game.setSelectionContinuationStatus(false);
        } else if ((score.getRoundScore() >= 1000) && isOptionAvailable()) {
            score.increasePermanentScore(score.getRoundScore());
            System.out.println("Your official score is now: " + score.getPermanentScore());
        } else if (!isOptionAvailable()) {
            game.getGameplayUI().clear();
        } else {
            game.getGameplayUI().clear();
            System.out.println("You cannot end without a score higher than 1000");
            game.getGameplayUI().pauseAndContinue();
        }
    }

    // Handles '?' command
    private void displayPossibleOptions() {
        System.out.println("Possible options: ");
        if (isStrait())
            System.out.println("\tStrait");
        if (isSet())
            System.out.println("\tSets");
        if (isMultiple()) {
            for (int fst : game.getCurrentPlayer().getDice().getDiceSetMap().keySet()) {
                if (isDesiredMultipleAvailable(fst))
                    System.out.println("\tMultiples, value: " + fst);
            }
        }
        if (canAddMultiples())
            System.out.println("\tAddons, value: " + game.getValueOfChosenMultiple());
        if (isSingle(1) || isSingle(5))
            System.out.println("\tSingles");
        game.getGameplayUI().pauseAndContinue();
    }

    // Display common impossible option message
    private void displayImpossibleOptionMessage() {
        game.getGameplayUI().clear();
        System.out.println("You have selected an impossible option");
    }
}
