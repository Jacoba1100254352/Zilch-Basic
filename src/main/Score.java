// Score.java
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
		return scoreLimit;
	}
	
	public void setScoreLimit(int limit) {
		scoreLimit = limit;
	}
	
	public int getPermanentScore() {
		return permanentScore;
	}
	
	public void increasePermanentScore(int score) {
		permanentScore += score;
	}
	
	public int getRoundScore() {
		return roundScore;
	}
	
	public void setRoundScore(int score) {
		roundScore = score;
	}
	
	public void increaseRoundScore(int score) {
		roundScore += score;
	}
	
	public int getScoreFromMultiples() {
		return scoreFromMultiples;
	}
	
	public void setScoreFromMultiples(int score) {
		scoreFromMultiples = score;
	}
	
	public void displayCurrentScore(String playerName) {
		System.out.println(playerName + "'s current score: " + roundScore);
	}
	
	public void displayHighScoreInfo(String currentPlayerName, String highestScoringPlayerName) {
		int highestScore = permanentScore;
		if (highestScore < scoreLimit) {
			displayCurrentScore(currentPlayerName);
		} else if (highestScore > roundScore) {
			System.out.println("\n\nYour current score of " + roundScore + " is " + (highestScore - roundScore)
					                   + " less than " + currentPlayerName + "'s High Score of " + highestScore + " so keep going! :)");
		} else if (!highestScoringPlayerName.equals(currentPlayerName)) {
			System.out.println("You are currently tied with the highest scoring player!");
		} else {
			System.out.println("You are currently the highest scoring player");
		}
	}
}
