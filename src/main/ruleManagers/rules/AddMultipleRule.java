package ruleManagers.rules;

import models.GameOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AddMultipleRule implements Rule {
    private static final int PRIORITY = 40;

    @Override
    public GameOption.Type getType() {
        return GameOption.Type.ADD_MULTIPLE;
    }

    @Override
    public Collection<GameOption> evaluate(RuleContext context) {
        Integer previousMultiple = context.getPreviouslySelectedMultipleValue();
        if (previousMultiple == null) {
            return List.of();
        }

        Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
        if (diceSetMap.isEmpty()) {
            return List.of();
        }

        int remainingDice = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
        if (remainingDice > 3) {
            return List.of();
        }

        if (diceSetMap.getOrDefault(previousMultiple, 0) <= 0) {
            return List.of();
        }

        List<GameOption> options = new ArrayList<>();
        options.add(new GameOption(getType(), previousMultiple));
        return options;
    }

    @Override
    public void apply(GameOption option, RuleContext context) {
        if (!supports(option)) {
            return;
        }

        context.getPlayerManager().scoreMultiple(option.value());
        context.getPlayerManager().eliminateDice(option.value());
        context.setPreviouslySelectedMultipleValue(option.value());
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
