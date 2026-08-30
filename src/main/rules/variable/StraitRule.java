package rules.variable;


import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static model.entities.Dice.FULL_SET_OF_DICE;


public class StraitRule extends AbstractVariableRule
{
	private Integer minNumStraitValues = FULL_SET_OF_DICE;

	public StraitRule() {
		super(RuleType.STRAIT, "Straight", "Score a straight sequence of dice.");
	}

	@Override
	protected void setConfigValue(Object value) {
		this.minNumStraitValues = (Integer) value;
	}

	@Override
	public Object getDefaultConfig() {
		return FULL_SET_OF_DICE;
	}

	@Override
	public List<model.entities.GameOption> evaluate(RuleContext context) {
		Map<Integer, Integer> consumedDice = findStraight(context.diceSetMap());
		if (consumedDice.size() < minNumStraitValues) {
			return List.of();
		}
		return List.of(buildOption(null, 1000, consumedDice));
	}

	private Map<Integer, Integer> findStraight(Map<Integer, Integer> diceSetMap) {
		Map<Integer, Integer> longestStraight = new LinkedHashMap<>();
		Map<Integer, Integer> currentStraight = new LinkedHashMap<>();

		for (int value = 1; value <= FULL_SET_OF_DICE; value++) {
			if (diceSetMap.getOrDefault(value, 0) > 0) {
				currentStraight.put(value, 1);
				if (currentStraight.size() > longestStraight.size()) {
					longestStraight = new LinkedHashMap<>(currentStraight);
				}
			} else {
				currentStraight.clear();
			}
		}

		return longestStraight;
	}
}
