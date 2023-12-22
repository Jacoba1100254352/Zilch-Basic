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
        } else { // Increment round score by the additional points from the new multiple, not the total multiple score.
            getScore().increaseRoundScore(mScore - getScore().getScoreFromMultiples());
        }

        getScore().setScoreFromMultiples(mScore);
    }


    ///   Helper Functions   ///

    private int calculateMultipleScore(int dieValue, Dice dice) {
        int scoreForMultiples;
        int scoreFromPreviousMultiples = getScore().getScoreFromMultiples();
        if (scoreFromPreviousMultiples == 0) { // First Multiples Scored
            int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
            int numMultiples = dice.diceSetMap().get(dieValue) - 3;
            scoreForMultiples = (int) Math.pow(2, numMultiples) * baseScore;
        } else { // Adding Multiples
            int numMultiples = dice.diceSetMap().get(dieValue);
            scoreForMultiples = (int) Math.pow(2, numMultiples) * scoreFromPreviousMultiples;
        }
        return scoreForMultiples;
    }
}
