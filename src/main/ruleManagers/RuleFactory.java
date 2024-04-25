package ruleManagers;


import abstracts.AbstractRule;
import rules.*;


public class RuleFactory
{
	public static AbstractRule getRule(RuleType ruleType, Integer value) {
		return switch (ruleType) {
			case ADD_MULTIPLE -> new AddMultipleRule(value);
			case MULTIPLE -> new MultipleRule(value);
			case SINGLE -> new SingleRule(value);
			case SET -> new SetRule(value);
			case STRAIT -> new StraitRule(value);
		};
	}
}
