package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;
import ruleManagers.RuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleRule implements Rule
{
        @Override
        public String id() {
                return "single";
        }

        @Override
        public String displayName() {
                return "Single Scores";
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                List<GameOption> options = new ArrayList<>();
                for (int value : new int[] {1, 5}) {
                        if (diceSetMap.getOrDefault(value, 0) > 0) {
                                options.add(new GameOption(GameOption.Type.SINGLE, value));
                        }
                }
                return options;
        }

        @Override
        public void apply(RuleContext context, GameOption option) {
                if (option.value() == null) {
                        return;
                }
                context.getPlayerManager().scoreSingle(option.value());
                context.getPlayerManager().removeDice(option.value(), 1);
        }

        @Override
        public String describe(GameOption option) {
                return "Score Single " + option.value();
        }
}
