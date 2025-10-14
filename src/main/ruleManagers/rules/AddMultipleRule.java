package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;
import ruleManagers.RuleContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddMultipleRule implements Rule
{
        @Override
        public String id() {
                return "add-multiple";
        }

        @Override
        public String displayName() {
                return "Add to Multiples";
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context) {
                Integer previous = context.getPreviouslySelectedMultipleValue();
                if (previous == null) {
                        return Collections.emptyList();
                }

                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                int numDiceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
                if (numDiceInPlay <= 3 && diceSetMap.getOrDefault(previous, 0) > 0) {
                        return List.of(new GameOption(GameOption.Type.ADD_MULTIPLE, previous));
                }
                return Collections.emptyList();
        }

        @Override
        public void apply(RuleContext context, GameOption option) {
                if (option.value() == null) {
                        return;
                }
                context.getPlayerManager().scoreMultiple(option.value());
                context.getPlayerManager().eliminateDice(option.value());
        }

        @Override
        public String describe(GameOption option) {
                return "Score Add Multiple " + option.value();
        }
}
