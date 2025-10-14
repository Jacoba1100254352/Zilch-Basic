package models;


public class Score
{
	private final int scoreLimit;
	private int permanentScore;
	private int roundScore;
        private int scoreFromMultiples;
        private int scoreFromMultiplesDieValue;

        public Score(int scoreLimit, int permanentScore, int roundScore, int scoreFromMultiples, int scoreFromMultiplesDieValue) {
                this.scoreLimit = scoreLimit;
                this.permanentScore = permanentScore;
                this.roundScore = roundScore;
                this.scoreFromMultiples = scoreFromMultiples;
                this.scoreFromMultiplesDieValue = scoreFromMultiplesDieValue;
        }
	
	
	///   Getters and Setters   ///
	
	public int getScoreLimit() {
		return this.scoreLimit;
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
	
	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
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

        public int getScoreFromMultiplesDieValue() {
                return this.scoreFromMultiplesDieValue;
        }

        public void setScoreFromMultiplesDieValue(int dieValue) {
                this.scoreFromMultiplesDieValue = dieValue;
        }
}
