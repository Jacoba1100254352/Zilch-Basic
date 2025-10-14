package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Handles the scoring of single die values that yield points (ones and fives).
 */
public class SingleRule extends AbstractRule
{
        public SingleRule() {
                super("single", "Singles", GameOption.Type.SINGLE);
        }

        @Override
        public List<GameOption> evaluate(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                List<GameOption> options = new ArrayList<>();
                diceSetMap.forEach((value, count) -> {
                        if (isScoringValue(value) && count > 0) {
                                options.add(new GameOption(optionType(), value));
                        }
                });
                options.sort(Comparator.comparing(GameOption::value));
                return options;
        }

        @Override
        public void apply(GameOption option, RuleContext context) {
                int dieValue = option.value();
                context.getPlayerManager().scoreSingle(dieValue);
                context.getPlayerManager().removeDice(dieValue, 1);
        }

        private boolean isScoringValue(int value) {
                return value == 1 || value == 5;
        }
}
