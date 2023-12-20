package modelManagers;

import managers.GameCoordinator;
import models.Dice;
import models.Player;
import models.Score;

public class ScoreManager {
    private final GameCoordinator gameCoordinator;

    public ScoreManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }

    public void scoreStraits() {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().increaseRoundScore(1000);
    }

    public void scoreSets() {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().increaseRoundScore(1000);
    }

    public void scoreMultiple(int dieValue) {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        Dice dice = currentPlayer.dice();
        Score score = currentPlayer.score();

        int mScore = calculateMultipleScore(dieValue, dice);
        score.increaseRoundScore(mScore);
    }

    private int calculateMultipleScore(int dieValue, Dice dice) {
        int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
        int numMultiples = dice.diceSetMap().get(dieValue) - 3;
        return (int) Math.pow(2, numMultiples) * baseScore;
    }

    public void scoreSingle(int dieValue) {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        Score score = currentPlayer.score();

        int singleScore = (dieValue == 1) ? 100 : 50;
        score.increaseRoundScore(singleScore);
    }
}
