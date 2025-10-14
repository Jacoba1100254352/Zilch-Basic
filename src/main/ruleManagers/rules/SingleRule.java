package ruleManagers.rules;

import models.GameOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SingleRule implements Rule {
    private static final int PRIORITY = 50;
    private static final List<Integer> SCORING_SINGLES = List.of(1, 5);

    @Override
    public GameOption.Type getType() {
        return GameOption.Type.SINGLE;
    }

    @Override
    public Collection<GameOption> evaluate(RuleContext context) {
        Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
        if (diceSetMap.isEmpty()) {
            return List.of();
        }

        List<GameOption> options = new ArrayList<>();
        for (int value : SCORING_SINGLES) {
            if (diceSetMap.getOrDefault(value, 0) > 0) {
                options.add(new GameOption(getType(), value));
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

        context.getPlayerManager().scoreSingle(option.value());
        context.getPlayerManager().removeDice(option.value(), 1);
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
