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
                int count = getDice().getDiceSetMap().get(dieValue);
                int mScore;

                boolean isNewMultiple = getScore().getScoreFromMultiples() == 0
                                || getScore().getScoreFromMultiplesDieValue() != dieValue;

                if (isNewMultiple) {
                        int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
                        int numMultiples = count - 3;
                        mScore = (int) Math.pow(2, numMultiples) * baseScore;
                        getScore().increaseRoundScore(mScore);
                } else {
                        mScore = (int) Math.pow(2, count) * getScore().getScoreFromMultiples();
                        getScore().increaseRoundScore(mScore - getScore().getScoreFromMultiples());
                }

                getScore().setScoreFromMultiples(mScore);
                getScore().setScoreFromMultiplesDieValue(dieValue);
        }
}
