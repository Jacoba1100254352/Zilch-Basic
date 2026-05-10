package rules.managers;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.variable.IRule;

import java.util.List;
import java.util.Map;


public interface IRuleManager
{
	void initializeRules(Map<RuleType, Object> config);

	List<GameOption> evaluateRules(RuleContext context);

	IRule getRule(RuleType ruleType);

	void applyRule(RuleContext context, GameOption option);

	List<IRule> getAvailableRules();
}
