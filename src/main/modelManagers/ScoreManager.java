package modelManagers;

import models.Dice;

public class ScoreManager extends PlayerActionManager {

    public ScoreManager(PlayerManager playerManager) {
        super(playerManager);
    }


    ///   Main Functions   ///

    public void scoreStraits() {
        getScore().increaseRoundScore(1000);
    }

    public void scoreSets() {
        getScore().increaseRoundScore(1000);
    }

    public void scoreSingle(int dieValue) {
        int singleScore = (dieValue == 1) ? 100 : 50;
        getScore().increaseRoundScore(singleScore);
    }

    public void scoreMultiple(int dieValue) {
        int mScore = calculateMultipleScore(dieValue, getDice());

        if (getScore().getScoreFromMultiples() == 0) {
            getScore().increaseRoundScore(mScore);
        } else { // Increase the round score by the difference between the new multiple score and the previous multiple score
            getScore().increaseRoundScore(mScore - getScore().getScoreFromMultiples());
        }

        getScore().setScoreFromMultiples(mScore);
    }


    ///   Helper Functions   ///

    private int calculateMultipleScore(int dieValue, Dice dice) {
        int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
        int numMultiples = dice.diceSetMap().get(dieValue) - 3;
        return baseScore * (int) Math.pow(2, numMultiples);
    }
}
