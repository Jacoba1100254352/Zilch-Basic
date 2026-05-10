package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class AddMultipleRule extends AbstractVariableRule
{
	private Integer minimumAdditionalDice = 1;

	public AddMultipleRule() {
		super(RuleType.ADD_MULTIPLE, "Add Multiple", "Extend a previously scored multiple in the same turn.");
	}

	@Override
	protected void setConfigValue(Object value) {
		this.minimumAdditionalDice = (Integer) value;
	}

	@Override
	public Object getDefaultConfig() {
		return 1;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		List<GameOption> options = new ArrayList<>();

		for (Map.Entry<Integer, Integer> scoredEntry : context.scoredMultiples().entrySet()) {
			int dieValue = scoredEntry.getKey();
			int previousCount = scoredEntry.getValue();
			int currentCount = context.diceSetMap().getOrDefault(dieValue, 0);

			if (previousCount < 3 || currentCount < minimumAdditionalDice) {
				continue;
			}

			int totalCount = previousCount + currentCount;
			int incrementalScore = calculateMultipleScore(totalCount, dieValue) - calculateMultipleScore(previousCount, dieValue);
			options.add(buildOption(dieValue, incrementalScore, Map.of(dieValue, currentCount)));
		}

		options.sort(Comparator.comparing(GameOption::selectedValue));
		return options;
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
