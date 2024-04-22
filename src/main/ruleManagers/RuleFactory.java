package ruleManagers;


import interfaces.IRule;
import rules.*;


public class RuleFactory
{
	
	public static IRule getRule(RuleType ruleType, Integer value) {
		return switch (ruleType) {
			case ADD_MULTIPLE -> new AddMultipleRule(value);
			case MULTIPLE -> new MultipleRule(value);
			case SINGLE -> new SingleRule(value);
			case SET -> new SetRule();
			case STRAIT -> new StraitRule();
		};
	}
}
