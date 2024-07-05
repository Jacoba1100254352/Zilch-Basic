package rules.constantModels;


public class RollAgainRule implements IConstantRule
{
	@Override
	public String getDescription() {
		return "Roll Again";
	}
	
	@Override
	public boolean isValid(Integer roundScore, Integer numOptionsSelected) {
		return roundScore > 0 && numOptionsSelected > 0;
	}
}
