package rules.models;


public class RollAgainRule implements IConstantRule
{
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public boolean isValid(Integer roundScore, Integer numOptionsSelected) {
		return roundScore > 0 && numOptionsSelected > 0;
	}
}
