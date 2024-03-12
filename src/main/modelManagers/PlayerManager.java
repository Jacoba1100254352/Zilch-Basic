package modelManagers;

import models.Dice;
import models.Player;
import models.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class PlayerManager {
    private final DiceManager diceManager;
    private final ScoreManager scoreManager;
    private final Vector<Player> players;
    private Player currentPlayer;

    public PlayerManager(List<String> playerNames, int scoreLimit) {
        this.diceManager = new DiceManager(this);
        this.scoreManager = new ScoreManager(this);
        this.players = playerNames.stream()
                .map(playerName -> new Player(playerName, new Dice(new HashMap<>()), new Score(scoreLimit, 0, 0, 0)))
                .collect(Collectors.toCollection(Vector::new));
        this.currentPlayer = players.isEmpty() ? null : players.firstElement();
    }


    ///   Main Functions   ///

    public void switchToNextPlayer() {
        int currentPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    public Player findHighestScoringPlayer() {
        Player highestScoringPlayer = currentPlayer;
        for (Player player : players) {
            if (player.score().getPermanentScore() > highestScoringPlayer.score().getPermanentScore()) {
                highestScoringPlayer = player;
            }
        }
        return highestScoringPlayer;
    }


    ///   Getters and Setters   ///
    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }


    /*************
     *    DICE   *
     ************/
    public Map<Integer, Integer> getDice(Player player) {
        return player.dice().diceSetMap();
    }

    public void rollDice() {
        diceManager.rollDice();
    }

    public void replenishAllDice() {
        diceManager.replenishAllDice();
    }

    public void eliminateDice(int dieValue) {
        diceManager.eliminateDice(dieValue);
    }

    public void removeDice(int dieValue, int numToRemove) {
        diceManager.removeDice(dieValue, numToRemove);
    }

    public void removeAllDice() {
        diceManager.removeAllDice();
    }


    /**************
     *    SCORE   *
     *************/
    public Score getScore() {
        return scoreManager.getScore();
    }

    public void scoreStraits() {
        scoreManager.scoreStraits();
    }

    public void scoreSets() {
        scoreManager.scoreSets();
    }

    public void scoreSingle(int dieValue) {
        scoreManager.scoreSingle(dieValue);
    }

    public void scoreMultiple(int dieValue) {
        scoreManager.scoreMultiple(dieValue);
    }
}
