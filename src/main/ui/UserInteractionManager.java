package ui;

import models.Score;
import models.GameOption;
import managers.GameStateManager;
import modelManagers.GameOptionManager;
import rules.RuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public record UserInteractionManager(GameplayUI gameplayUI, GameOptionManager gameOptionManager, RuleManager ruleManager, GameStateManager gameStateManager) {

    public static void ignoreRemainingInput(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public int getNumberOfPlayers(Scanner scanner) {
        int numPlayers;
        while (true) {
            System.out.print("Enter the number of players (1-6): ");
            numPlayers = scanner.nextInt();

            if (numPlayers >= 1 && numPlayers <= 6) {
                break;
            }

            System.out.println("Invalid number of players. Please try again.");
            scanner.nextLine(); // Clear buffer
        }
        return numPlayers;
    }

    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int numPlayers = getNumberOfPlayers(scanner);

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.next();
            playerNames.add(playerName);
        }

        return playerNames;
    }

    public void processInput(Scanner scanner) {
        ignoreRemainingInput(scanner); // Clear remaining input.
        readInput(scanner); // Read user input.
    }

    public int getValidScoreLimit() {
        Scanner scanner = new Scanner(System.in);
        final int MIN_SCORE_LIMIT = 1000;
        int limit;

        while (true) {
            System.out.print("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
            limit = scanner.nextInt();

            if (limit >= MIN_SCORE_LIMIT) {
                break;
            }

            System.out.println("Invalid score limit. Please try again.");
            scanner.nextLine(); // Clears the buffer.
        }
        return limit;
    }


    public boolean enterEndTurnOption(Scanner scanner, Score playerScore) {
        // Variables
        int playOrEndTurn;

        // Enter Decision
        System.out.print("Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
        while (!scanner.hasNextInt() || (playOrEndTurn = scanner.nextInt()) < 0 || playOrEndTurn > 2) {
            // Handle incorrect input
            System.out.print("Invalid input. Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
            ignoreRemainingInput(scanner); // Clear the buffer
        }

        return playOrEndTurn == 2 && playerScore.getRoundScore() >= 1000;
    }

    // Handles 's' command
    private void executeSecondaryCommand(char ch) {
        GameOption.Type optionType;
        int val = Character.getNumericValue(ch);

        optionType = switch (ch) {
            case 't' -> ruleManager.isStrait() ? GameOption.Type.STRAIT : null;
            case 'e' -> ruleManager.isSet() ? GameOption.Type.SET : null;
            case '1' -> ruleManager.isSingle(1) ? GameOption.Type.SINGLE : null;
            case '5' -> ruleManager.isSingle(5) ? GameOption.Type.SINGLE : null;
            default -> null;
        };

        if (optionType != null) {
            GameOption gameOption = new GameOption(optionType, val);
            gameOptionManager.processMove(gameOption);
        } else {
            gameplayUI.displayImpossibleOptionMessage();
        }
    }

    public void readInput(Scanner input) {
        char ch = readValidCommand(input);

        switch (ch) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
                processOptionInt(Character.getNumericValue(ch));
                break;
            case 's':
                executeSecondaryCommand(input.next().charAt(0));
                break;
            case 'a':
                processOptionA(input);
                // Falls through to case 'm' intentionally
            case 'm':
                processOptionM(input.nextInt());
                break;
            case '0':
                gameStateManager.processZeroCommand();
                break;
            case '?':
                gameplayUI.displayPossibleOptions();
                break;
            default:
                gameplayUI.displayImpossibleOptionMessage();
                break;
        }
    }

    private char readValidCommand(Scanner input) {
        String validCommands = "stel123456am0?";
        String pattern = "[" + validCommands + "]";

        String next;
        do {
            next = input.next();
        } while (!next.matches(pattern));

        return next.charAt(0);
    }

    private void processOptionInt(int val) {
        GameOption.Type type = determineGameOptionType(val);
        if (type != null) {
            GameOption gameOption = new GameOption(type, val);
            gameOptionManager.processMove(gameOption);
        } else {
            gameplayUI.displayImpossibleOptionMessage();
        }
    }

    private void processOptionA(Scanner input) {
        char ch = input.next().charAt(0);
        gameplayUI.clear();
        input.nextLine(); // Ignore the rest of the line
        if (ch == 'l') {
            gameOptionManager.applyAllPossibleOptions();
        }
    }

    private void processOptionM(int val) {
        GameOption.Type multipleType = determineGameOptionType(val);
        if (multipleType == GameOption.Type.MULTIPLE) {
            GameOption gameOption = new GameOption(multipleType, val);
            gameOptionManager.processMove(gameOption);
        } else {
            gameplayUI.displayFailedMultipleMessage(val);
        }
    }

    private GameOption.Type determineGameOptionType(int val) {
        if (ruleManager.isStrait()) {
            return GameOption.Type.STRAIT;
        }
        if (ruleManager.isSet()) {
            return GameOption.Type.SET;
        }
        if (ruleManager.isDesiredMultipleAvailable(val)) {
            return GameOption.Type.MULTIPLE;
        }
        if (ruleManager.isSingle(val)) {
            return GameOption.Type.SINGLE;
        }
        return null; // Default case if no type matches
    }
}
