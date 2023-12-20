package managers;

import models.Player;

import java.util.List;

public class GameFlowManager {
    private final GameCoordinator gameCoordinator;

    public GameFlowManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }

    public boolean checkGameEndCondition(Player player) {
        return player.score().getPermanentScore() >= player.score().getScoreLimit();
    }

    public void handleGameEnd() {
        Player gameEndingPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        gameCoordinator.getGameplayUI().displayLastRoundMessage(gameEndingPlayer);
        gameCoordinator.getPlayerManager().switchToNextPlayer();

        do {
            gameCoordinator.getGameplayUI().displayLastTurnMessage(gameCoordinator.getPlayerManager().getCurrentPlayer().name());
            gameCoordinator.getDiceManager().replenishAllDice();
            gameCoordinator.getGameStateManager().initializeRollCycle();

            while (gameCoordinator.getGameStateManager().shouldContinueTurn()) {
                gameCoordinator.getGameplayUI().displayHighScoreInfo(gameCoordinator.getPlayerManager().getCurrentPlayer(), gameEndingPlayer.name());
                gameCoordinator.getDiceManager().rollDice(gameCoordinator.getPlayerManager().getCurrentPlayer().dice());

                if (gameCoordinator.getGameStateManager().shouldContinueTurn()) {
                    gameCoordinator.processPlayerInput();
                }
            }

            gameCoordinator.getPlayerManager().switchToNextPlayer();
        } while (gameCoordinator.getPlayerManager().getCurrentPlayer() != gameEndingPlayer);

        manageTiedEnding();
    }

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

    public void playTurn(Player player) {
        while (gameCoordinator.getGameStateManager().shouldContinueTurn()) {
            gameCoordinator.getDiceManager().rollDice(player.dice());

            gameCoordinator.getGameOptionManager().updateCurrentGameOption();

            if (gameCoordinator.getGameStateManager().shouldContinueTurn()) {
                gameCoordinator.processPlayerInput();
            }
        }
    }
}


