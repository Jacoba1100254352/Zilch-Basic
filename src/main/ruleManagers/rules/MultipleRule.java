package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Detects triples (or more) of a die face and applies the appropriate scoring.
 */
public class MultipleRule extends AbstractRule
{
        public MultipleRule() {
                super("multiple", "Multiples", GameOption.Type.MULTIPLE);
        }

        @Override
        public List<GameOption> evaluate(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                List<GameOption> options = new ArrayList<>();
                diceSetMap.forEach((value, count) -> {
                        if (count >= 3) {
                                options.add(new GameOption(optionType(), value));
                        }
                });
                options.sort(Comparator.comparing(GameOption::value));
                return options;
        }

        @Override
        public void apply(GameOption option, RuleContext context) {
                int dieValue = option.value();
                context.getPlayerManager().scoreMultiple(dieValue);
                context.getPlayerManager().eliminateDice(dieValue);
                context.setPreviouslySelectedMultipleValue(dieValue);
        }
}
