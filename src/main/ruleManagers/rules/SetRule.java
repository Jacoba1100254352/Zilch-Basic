package ruleManagers.rules;

import models.GameOption;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetRule implements Rule {
    private static final int PRIORITY = 20;

    @Override
    public GameOption.Type getType() {
        return GameOption.Type.SET;
    }

    @Override
    public Collection<GameOption> evaluate(RuleContext context) {
        Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
        if (diceSetMap.size() != 3) {
            return List.of();
        }

        boolean allPairs = diceSetMap.values().stream().allMatch(count -> count == 2);
        if (!allPairs) {
            return List.of();
        }

        boolean hasStrait = new StraitRule().isStrait(diceSetMap);
        boolean hasMultiple = diceSetMap.values().stream().anyMatch(count -> count >= 3);

        if (hasStrait || hasMultiple) {
            return List.of();
        }

        return List.of(new GameOption(getType(), null));
    }

    @Override
    public void apply(GameOption option, RuleContext context) {
        if (!supports(option)) {
            return;
        }

        context.getPlayerManager().scoreSets();
        context.getPlayerManager().removeAllDice();
        context.clearPreviouslySelectedMultipleValue();
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
