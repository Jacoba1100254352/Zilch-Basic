package rules.variable;

import rules.context.RuleContext;
import rules.context.ScoreContext;


public interface IVariableRule extends IRule
{
	boolean isValid(RuleContext validationContext);
	void score(ScoreContext scoreContext);
}
