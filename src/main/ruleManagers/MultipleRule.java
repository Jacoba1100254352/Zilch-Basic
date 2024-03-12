package ruleManagers;

import java.util.Map;

public class MultipleRule implements Rule {
    private int value;

    public MultipleRule() {
    }

    public MultipleRule(int value) {
        this.value = value;
    }

    @Override
    public boolean isValid(Map<Integer, Integer> diceSetMap) {
        return diceSetMap.values().stream().anyMatch(count -> count >= 3);
    }

    public boolean isDesiredMultipleAvailable(Map<Integer, Integer> diceSetMap) {
        return diceSetMap.getOrDefault(value, 0) >= 3;
    }
}
