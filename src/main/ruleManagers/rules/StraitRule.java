package ruleManagers.rules;

import models.GameOption;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class StraitRule implements Rule {
    private static final int PRIORITY = 10;

    @Override
    public GameOption.Type getType() {
        return GameOption.Type.STRAIT;
    }

    @Override
    public Collection<GameOption> evaluate(RuleContext context) {
        Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
        if (!isStrait(diceSetMap)) {
            return List.of();
        }
        return List.of(new GameOption(getType(), null));
    }

    @Override
    public void apply(GameOption option, RuleContext context) {
        if (!supports(option)) {
            return;
        }

        context.getPlayerManager().scoreStraits();
        context.getPlayerManager().removeAllDice();
        context.clearPreviouslySelectedMultipleValue();
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    boolean isStrait(Map<Integer, Integer> diceSetMap) {
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
