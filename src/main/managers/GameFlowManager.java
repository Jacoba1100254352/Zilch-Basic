package managers;

import models.Player;

import java.util.List;

public class GameFlowManager {
    private final GameCoordinator gameCoordinator;

    public GameFlowManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }


    ///   Main Functions   ///

    public void playTurn(Player player, String gameEndingPlayerName) {
        while (true) {
            if (gameEndingPlayerName != null)
                gameCoordinator.getGameplayUI().displayHighScoreInfo(player, gameEndingPlayerName);

            // Roll dice and evaluate game options
            gameCoordinator.getPlayerManager().rollDice();
            gameCoordinator.getGameOptionManager().evaluateGameOptions();

            // Decide whether the turn ends or continues
            if (gameCoordinator.getGameStateManager().canContinueTurn()) {
                gameCoordinator.processPlayerInput();
            } else {
                break;
            }
        }
    }

    public boolean checkGameEndCondition(Player player) {
        return player.score().getPermanentScore() >= player.score().getScoreLimit();
    }

    public void handleGameEnd() {
        final Player gameEndingPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        gameCoordinator.getGameplayUI().displayLastRoundMessage(gameEndingPlayer);
        gameCoordinator.getPlayerManager().switchToNextPlayer();

        do {
            gameCoordinator.getGameplayUI().displayLastTurnMessage(gameCoordinator.getPlayerManager().getCurrentPlayer().name());
            gameCoordinator.getGameStateManager().initializeRollCycle();

            playTurn(gameCoordinator.getPlayerManager().getCurrentPlayer(), gameEndingPlayer.name());

            gameCoordinator.getPlayerManager().switchToNextPlayer();
        } while (gameCoordinator.getPlayerManager().getCurrentPlayer() != gameEndingPlayer);

        manageTiedEnding();
    }


    ///   Helper Functions   ///

    private void manageTiedEnding() {
        Player playerWithHighestScore = gameCoordinator.getPlayerManager().findHighestScoringPlayer();
        int highestScore = playerWithHighestScore.score().getPermanentScore();
        List<Player> tiedPlayers = gameCoordinator.getTiedPlayers(highestScore);

        if (gameCoordinator.isTie(tiedPlayers)) {
            gameCoordinator.getGameplayUI().announceTie(tiedPlayers, highestScore);
        } else {
            gameCoordinator.getGameplayUI().announceWinner(playerWithHighestScore, highestScore);
        }
    }
}


