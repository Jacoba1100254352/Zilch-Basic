package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;
import ruleManagers.RuleStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class MultipleRuleStrategy implements RuleStrategy
{
        public static final String ID = "multiple";

        @Override
        public String id()
        {
                return ID;
        }

        @Override
        public String displayName()
        {
                return "Multiple";
        }

        @Override
        public String description()
        {
                return "Score sets of three or more identical dice.";
        }

        @Override
        public int priority()
        {
                return 30;
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context)
        {
                Map<Integer, Integer> dice = context.currentDice();
                List<GameOption> options = new ArrayList<>();
                for (int face = 1; face <= FULL_SET_OF_DICE; face++) {
                        int count = dice.getOrDefault(face, 0);
                        if (count >= 3) {
                                options.add(new GameOption(id(), GameOption.Type.MULTIPLE, face,
                                                           "Score Multiple " + face));
                        }
                }
                return options;
        }

        @Override
        public void apply(GameOption option, RuleContext context)
        {
                Integer value = option.value();
                if (value == null) {
                        throw new IllegalArgumentException("Multiple option requires a value");
                }
                context.playerManager().scoreMultiple(value);
                context.playerManager().eliminateDice(value);
                context.setPreviouslySelectedMultiple(value);
        }
}
