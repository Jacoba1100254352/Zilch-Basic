package ruleManagers;

import java.util.Map;

public class AddMultipleRule implements Rule {
    private final int value;

    public AddMultipleRule(int value) {
        this.value = value;
    }

    @Override
    public boolean isValid(Map<Integer, Integer> diceSetMap) {
        return diceSetMap.getOrDefault(value, 0) >= 3;
    }
}