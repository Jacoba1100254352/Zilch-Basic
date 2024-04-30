package model.managers;


public abstract class AbstractScoreManager implements IScoreManager
{
	protected int scoreLimit;
	
	public AbstractScoreManager(int scoreLimit) {
		this.scoreLimit = scoreLimit;
	}
	
	@Override
	public int getScoreLimit() {
		return scoreLimit;
	}
}
