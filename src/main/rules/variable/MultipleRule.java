package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;


public class MultipleRule extends AbstractVariableRule
{
	private Integer minimumMultiples = 3;

	public MultipleRule() {
		super(RuleType.MULTIPLE, "Multiple", "Score three or more of the same die.");
	}

	@Override
	protected void setConfigValue(Object value) {
		this.minimumMultiples = (Integer) value;
	}

	@Override
	public Object getDefaultConfig() {
		return 3;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return context.diceSetMap().entrySet().stream()
		              .filter(entry -> entry.getValue() >= minimumMultiples)
		              .filter(entry -> context.scoredMultiples().getOrDefault(entry.getKey(), 0) < minimumMultiples)
		              .sorted(Map.Entry.comparingByKey())
		              .map(entry -> buildOption(
				              entry.getKey(),
				              calculateMultipleScore(entry.getValue(), entry.getKey()),
				              Map.of(entry.getKey(), entry.getValue())
		              ))
		              .toList();
	}

	@Override
	protected void afterApply(RuleContext context, GameOption option) {
		Integer dieValue = option.selectedValue();
		if (dieValue == null) {
			return;
		}
		int consumedCount = option.consumedDice().getOrDefault(dieValue, 0);
		context.scoredMultiples().put(dieValue, context.scoredMultiples().getOrDefault(dieValue, 0) + consumedCount);
	}

	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		return baseScore * (int) Math.pow(2, numMultiples - 3);
	}
}
