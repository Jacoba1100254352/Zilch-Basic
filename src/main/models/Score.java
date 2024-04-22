package models;


public class Score
{
	private int permanentScore;
	private int roundScore;
	private int scoreFromMultiples;
	
	public Score(int permanentScore, int roundScore, int scoreFromMultiples) {
		this.permanentScore = permanentScore;
		this.roundScore = roundScore;
		this.scoreFromMultiples = scoreFromMultiples;
	}
	
	public Score() {
		this.permanentScore = 0;
		this.roundScore = 0;
		this.scoreFromMultiples = 0;
	}
	
	
	///   Getters and Setters   ///
	
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
}
