package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;
import ruleManagers.RuleContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class StraitRule implements Rule
{
        @Override
        public String id() {
                return "strait";
        }

        @Override
        public String displayName() {
                return "Straights";
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context) {
                if (isStraight(context.getDiceSetMap())) {
                        return List.of(new GameOption(GameOption.Type.STRAIT, null));
                }
                return Collections.emptyList();
        }

        private boolean isStraight(Map<Integer, Integer> diceSetMap) {
                if (diceSetMap.size() != FULL_SET_OF_DICE) {
                        return false;
                }
                for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
                        if (diceSetMap.getOrDefault(i, 0) != 1) {
                                return false;
                        }
                }
                return true;
        }

        @Override
        public void apply(RuleContext context, GameOption option) {
                context.getPlayerManager().scoreStraits();
                context.getPlayerManager().removeAllDice();
                context.getGameOptionManager().setPreviouslySelectedMultipleValue(null);
        }

        @Override
        public String describe(GameOption option) {
                return "Score a Strait";
        }
}
