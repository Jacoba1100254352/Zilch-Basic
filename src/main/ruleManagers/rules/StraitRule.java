package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;

import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

/**
 * Detects a straight (1-6) roll.
 */
public class StraitRule extends AbstractRule
{
        public StraitRule() {
                super("strait", "Straights", GameOption.Type.STRAIT);
        }

        @Override
        public List<GameOption> evaluate(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                if (diceSetMap.size() != FULL_SET_OF_DICE) {
                                return List.of();
                }

                for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
                        if (diceSetMap.getOrDefault(i, 0) != 1) {
                                return List.of();
                        }
                }

                return List.of(new GameOption(optionType(), null));
        }

        @Override
        public void apply(GameOption option, RuleContext context) {
                context.getPlayerManager().scoreStraits();
                context.getPlayerManager().removeAllDice();
                context.clearPreviouslySelectedMultipleValue();
        }
}
