package model.managers;


import model.entities.Dice;
import model.entities.Player;
import model.entities.Score;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;


public class PlayerManager extends AbstractPlayerManager
{
	public PlayerManager(List<String> playerNames) {
		super.players = playerNames.stream()
		                           .map(name -> new Player(name, new Dice(new HashMap<>()), new Score()))
		                           .collect(Collectors.toCollection(Vector::new));
		super.currentPlayer = players.isEmpty() ? null : players.get(0);
	}
	
	@Override
	public void switchToNextPlayer() {
		int currentPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
		super.currentPlayer = players.get(currentPlayerIndex);
	}
	
	@Override
	public Player findHighestScoringPlayer() {
		return super.players.stream()
		                    .max(Comparator.comparing(p -> p.score().getPermanentScore()))
		                    .orElse(null);
	}
}
