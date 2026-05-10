package model.entities;


/**
 * Holds both permanent and turn-local score state for a player.
 */
public class Score
{
	private int permanentScore;
	private int roundScore;
	private int scoreFromMultiples;
	
	/**
	 * Creates a score with explicit permanent, round, and multiple-derived values.
	 */
	public Score(int permanentScore, int roundScore, int scoreFromMultiples) {
		this.permanentScore = permanentScore;
		this.roundScore = roundScore;
		this.scoreFromMultiples = scoreFromMultiples;
	}
	
	/**
	 * Creates a score object initialized to zero.
	 */
	public Score() {
		this(0, 0, 0);
	}
	
	
	///   Getters and Setters   ///
	
	/**
	 * Returns the player's banked score across completed turns.
	 */
	public int getPermanentScore() {
		return this.permanentScore;
	}
	
	/**
	 * Adds points to the player's permanent score total.
	 */
	public void increasePermanentScore(int score) {
		this.permanentScore += score;
	}
	
	/**
	 * Returns the player's accumulated score for the current turn.
	 */
	public int getRoundScore() {
		return this.roundScore;
	}
	
	/**
	 * Replaces the current round score.
	 */
	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
	}
	
	/**
	 * Adds points to the current round score.
	 */
	public void increaseRoundScore(int score) {
		this.roundScore += score;
	}
	
	/**
	 * Returns the portion of the round score currently attributed to multiples.
	 */
	public int getScoreFromMultiples() {
		return this.scoreFromMultiples;
	}
	
	/**
	 * Stores the currently scored multiple total used for incremental updates.
	 */
	public void setScoreFromMultiples(int score) {
		this.scoreFromMultiples = score;
	}
}
