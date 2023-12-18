package models;

public class Score {
    private int scoreLimit;
    private int permanentScore;
    private int roundScore;
    private int scoreFromMultiples;

    public Score() {
        this.scoreLimit = 2000;
        this.permanentScore = 0;
        this.roundScore = 0;
        this.scoreFromMultiples = 0;
    }

    public int getScoreLimit() {
        return this.scoreLimit;
    }

    public void setScoreLimit(int limit) {
        this.scoreLimit = limit;
    }

    public int getPermanentScore() {
        return this.permanentScore;
    }

    public void increasePermanentScore(int score) {
        this.permanentScore += score;
    }

    public int getRoundScore() {
        return this.roundScore;
    }

    public void setRoundScore(int score) {
        this.roundScore = score;
    }

    public void increaseRoundScore(int score) {
        this.roundScore += score;
    }

    public int getScoreFromMultiples() {
        return this.scoreFromMultiples;
    }

    public void setScoreFromMultiples(int score) {
        this.scoreFromMultiples = score;
    }

    public void displayCurrentScore(String playerName) {
        System.out.println(playerName + "'s current score: " + this.roundScore);
    }

    public void displayHighScoreInfo(String currentPlayerName, String highestScoringPlayerName) {
        ///   if: High permanent score below limit: Ask to end or keep playing   ///
        int highestScore = permanentScore;
        if (highestScore < scoreLimit)
            displayCurrentScore(currentPlayerName);
            ///   else if: Someone has surpassed the limit: try to beat them   ///
        else if (highestScore > roundScore) {
            System.out.print("\n\nYour current score of " + roundScore + " is " + (highestScore - roundScore));
            System.out.println(" less than " + currentPlayerName + "'s High Score of " + highestScore + " so keep going! :)");
            ///   else if: Tied for the highest   ///
        } else if (!highestScoringPlayerName.equals(currentPlayerName))
            System.out.print("You are currently tied with the highest scoring player!");
            ///   else: You are the highest! Ask to end or keep playing   ///
        else System.out.print("You are currently the highest scoring player");
    }
}
