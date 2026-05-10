package rules.variable;


import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;


public class SetRule extends AbstractVariableRule
{
	private Integer setMin = 3;

	public SetRule() {
		super(RuleType.SET, "Set", "Score three pairs.");
	}

	@Override
	protected void setConfigValue(Object value) {
		this.setMin = (Integer) value;
	}

	@Override
	public Object getDefaultConfig() {
		return 3;
	}

	@Override
	public List<model.entities.GameOption> evaluate(RuleContext context) {
		boolean isValidSet = context.diceSetMap().size() == setMin &&
				context.diceSetMap().values().stream().allMatch(count -> count == 2);

		if (!isValidSet) {
			return List.of();
		}

		return List.of(buildOption(null, 1000, Map.copyOf(context.diceSetMap())));
	}
}
