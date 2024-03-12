package ruleManagers;

public class Singles {
    private int value;

    public Singles(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValidSingle() {
        return value == 1 || value == 5;
    }
}
