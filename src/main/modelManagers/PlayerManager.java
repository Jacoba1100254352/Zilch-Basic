package modelManagers;

import models.Dice;
import models.Player;
import models.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class PlayerManager {
    private final Vector<Player> players;
    private Player currentPlayer;

    public PlayerManager(List<String> playerNames, int scoreLimit) {
        this.players = playerNames.stream()
                .map(playerName -> new Player(playerName, new Dice(new HashMap<>()), new Score(scoreLimit, 0, 0, 0)))
                .collect(Collectors.toCollection(Vector::new));
        this.currentPlayer = players.isEmpty() ? null : players.firstElement();
    }

    public void switchToNextPlayer() {
        int currentPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
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
}
