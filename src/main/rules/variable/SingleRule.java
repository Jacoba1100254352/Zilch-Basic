package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SingleRule extends AbstractVariableRule
{
	private Set<Integer> acceptedValues = Set.of(1, 5);

	public SingleRule() {
		super(RuleType.SINGLE, "Single", "Score a single scoring die.");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setConfigValue(Object value) {
		this.acceptedValues = Set.copyOf((Set<Integer>) value);
	}

	@Override
	public Object getDefaultConfig() {
		return Set.of(1, 5);
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return acceptedValues.stream()
		                     .filter(value -> context.diceSetMap().getOrDefault(value, 0) > 0)
		                     .sorted(Comparator.naturalOrder())
		                     .map(value -> buildOption(value, value == 1 ? 100 : 50, Map.of(value, 1)))
		                     .toList();
	}
}
