package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;
import ruleManagers.RuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class MultipleRule implements Rule
{
        @Override
        public String id() {
                return "multiple";
        }

        @Override
        public String displayName() {
                return "Multiples";
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                List<GameOption> options = new ArrayList<>();
                for (int value = 1; value <= FULL_SET_OF_DICE; value++) {
                        if (diceSetMap.getOrDefault(value, 0) >= 3) {
                                options.add(new GameOption(GameOption.Type.MULTIPLE, value));
                        }
                }
                return options;
        }

        @Override
        public void apply(RuleContext context, GameOption option) {
                if (option.value() == null) {
                        return;
                }
                context.getPlayerManager().scoreMultiple(option.value());
                context.getPlayerManager().eliminateDice(option.value());
                context.getGameOptionManager().setPreviouslySelectedMultipleValue(option.value());
        }

        @Override
        public String describe(GameOption option) {
                return "Score Multiple " + option.value();
        }
}
