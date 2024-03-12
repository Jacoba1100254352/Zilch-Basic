package ruleManagers;

import java.util.Map;

public class Multiples {
    private int value;

    public Multiples(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValidMultiple(Map<Integer, Integer> diceSetMap) {
        return diceSetMap.getOrDefault(value, 0) >= 3;
    }

    public int getCount(Map<Integer, Integer> diceSetMap) {
        return diceSetMap.getOrDefault(value, 0);
    }
}
