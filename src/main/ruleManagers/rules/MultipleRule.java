package ruleManagers.rules;

import models.GameOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MultipleRule implements Rule {
    private static final int PRIORITY = 30;

    @Override
    public GameOption.Type getType() {
        return GameOption.Type.MULTIPLE;
    }

    @Override
    public Collection<GameOption> evaluate(RuleContext context) {
        Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
        if (diceSetMap.isEmpty()) {
            return List.of();
        }

        List<GameOption> options = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : diceSetMap.entrySet()) {
            if (entry.getValue() >= 3) {
                options.add(new GameOption(getType(), entry.getKey()));
            }
        }

        options.sort(getOptionComparator());
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

    @Override
    public Comparator<GameOption> getOptionComparator() {
        return Comparator.comparingInt(GameOption::value);
    }
}
