package modelManagers;


public class ScoreManager extends PlayerActionManager
{
	
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
		int mScore;
		
		// Multiple
		if (getScore().getScoreFromMultiples() == 0) {
			int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
			int numMultiples = getDice().getDiceSetMap().get(dieValue) - 3;
			mScore = (int) Math.pow(2, numMultiples) * baseScore;
			
			// Increase the round score by the multiple score
			getScore().increaseRoundScore(mScore);
		}
		// Add Multiple
		else {
			mScore = (int) Math.pow(2, getDice().getDiceSetMap().get(dieValue)) * getScore().getScoreFromMultiples();
			
			// Increase the round score by the difference between the new multiple score and the previous multiple score
			getScore().increaseRoundScore(mScore - getScore().getScoreFromMultiples());
		}
		
		// Set or update the score from multiples
		getScore().setScoreFromMultiples(mScore);
	}
}
