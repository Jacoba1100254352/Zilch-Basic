package models;

public class Player {
    private final String name;
    private final Dice dice;
    private final Score score;

    public Player(String playerName) {
        this.name = playerName;
        this.dice = new Dice();
        this.score = new Score();
    }

    // No "sets" are included for these as the names don't change and everything else is pass by reference and defined in the constructor
    public Dice getDice() {
        return dice;
    }

    public Score getScore() {
        return score;
    }

    public String getName() {
        return name;
    }
}
