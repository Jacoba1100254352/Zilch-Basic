package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;
import ruleManagers.RuleStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleRuleStrategy implements RuleStrategy
{
        public static final String ID = "single";

        @Override
        public String id()
        {
                return ID;
        }

        @Override
        public String displayName()
        {
                return "Single";
        }

        @Override
        public String description()
        {
                return "Score individual 1s or 5s.";
        }

        @Override
        public int priority()
        {
                return 50;
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context)
        {
                Map<Integer, Integer> dice = context.currentDice();
                List<GameOption> options = new ArrayList<>();
                addSingleOption(dice, options, 1);
                addSingleOption(dice, options, 5);
                return options;
        }

        @Override
        public void apply(GameOption option, RuleContext context)
        {
                Integer value = option.value();
                if (value == null) {
                        throw new IllegalArgumentException("Single option requires a value");
                }
                context.playerManager().scoreSingle(value);
                context.playerManager().removeDice(value, 1);
        }

        private void addSingleOption(Map<Integer, Integer> dice, List<GameOption> options, int value)
        {
                if (dice.getOrDefault(value, 0) > 0) {
                        options.add(new GameOption(id(), GameOption.Type.SINGLE, value,
                                                   "Score Single " + value));
                }
        }
}
