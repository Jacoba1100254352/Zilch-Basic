package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;
import ruleManagers.RuleStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddMultipleRuleStrategy implements RuleStrategy
{
        public static final String ID = "add-multiple";

        @Override
        public String id()
        {
                return ID;
        }

        @Override
        public String displayName()
        {
                return "Add Multiple";
        }

        @Override
        public String description()
        {
                return "Add to a previously scored multiple.";
        }

        @Override
        public int priority()
        {
                return 40;
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context)
        {
                Integer previous = context.previouslySelectedMultiple();
                if (previous == null) {
                        return Collections.emptyList();
                }
                Map<Integer, Integer> dice = context.currentDice();
                int numDiceInPlay = dice.values().stream().mapToInt(Integer::intValue).sum();
                if (numDiceInPlay <= 3 && dice.getOrDefault(previous, 0) > 0) {
                                return List.of(new GameOption(id(), GameOption.Type.ADD_MULTIPLE, previous,
                                                              "Score Add Multiple " + previous));
                }
                return Collections.emptyList();
        }

        @Override
        public void apply(GameOption option, RuleContext context)
        {
                Integer value = option.value();
                if (value == null) {
                        throw new IllegalArgumentException("Add multiple option requires a value");
                }
                context.playerManager().scoreMultiple(value);
                context.playerManager().eliminateDice(value);
        }
}
