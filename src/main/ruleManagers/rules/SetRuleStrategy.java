package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;
import ruleManagers.RuleStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetRuleStrategy implements RuleStrategy
{
        public static final String ID = "set";

        @Override
        public String id()
        {
                return ID;
        }

        @Override
        public String displayName()
        {
                return "Set";
        }

        @Override
        public String description()
        {
                return "Score three pairs of dice.";
        }

        @Override
        public int priority()
        {
                return 20;
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context)
        {
                Map<Integer, Integer> dice = context.currentDice();
                if (dice.size() == 3 && dice.values().stream().allMatch(count -> count == 2) && !isStrait(dice)) {
                        return List.of(new GameOption(id(), GameOption.Type.SET, null, "Score a Set"));
                }
                return Collections.emptyList();
        }

        @Override
        public void apply(GameOption option, RuleContext context)
        {
                context.playerManager().scoreSets();
                context.playerManager().removeAllDice();
                context.setPreviouslySelectedMultiple(null);
        }

        private boolean isStrait(Map<Integer, Integer> dice)
        {
                return dice.entrySet().stream().allMatch(entry -> entry.getValue() == 1);
        }
}
