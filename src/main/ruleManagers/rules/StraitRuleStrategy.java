package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;
import ruleManagers.RuleStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class StraitRuleStrategy implements RuleStrategy
{
        public static final String ID = "strait";

        @Override
        public String id()
        {
                return ID;
        }

        @Override
        public String displayName()
        {
                return "Strait";
        }

        @Override
        public String description()
        {
                return "Score a six-dice straight (1-6).";
        }

        @Override
        public int priority()
        {
                return 10;
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context)
        {
                Map<Integer, Integer> dice = context.currentDice();
                if (isStrait(dice)) {
                        return List.of(new GameOption(id(), GameOption.Type.STRAIT, null, "Score a Strait"));
                }
                return Collections.emptyList();
        }

        @Override
        public void apply(GameOption option, RuleContext context)
        {
                context.playerManager().scoreStraits();
                context.playerManager().removeAllDice();
                context.setPreviouslySelectedMultiple(null);
        }

        private boolean isStrait(Map<Integer, Integer> diceSetMap)
        {
                if (diceSetMap.size() != FULL_SET_OF_DICE) {
                        return false;
                }
                for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
                        if (!diceSetMap.containsKey(i) || diceSetMap.get(i) != 1) {
                                return false;
                        }
                }
                return true;
        }
}
