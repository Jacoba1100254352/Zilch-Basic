package modelManagers;


import abstracts.AbstractManager;
import interfaces.IPlayerManager;
import models.Dice;
import models.Player;
import models.Score;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;


public class PlayerManager extends AbstractManager implements IPlayerManager
{
	private final Vector<Player> players;
	private Player currentPlayer;
	
	public PlayerManager(List<String> playerNames) {
		this.players = playerNames.stream()
		                          .map(name -> new Player(name, new Dice(new HashMap<>()), new Score()))
		                          .collect(Collectors.toCollection(Vector::new));
		this.currentPlayer = players.isEmpty() ? null : players.firstElement();
	}
	
	@Override
	public void switchToNextPlayer() {
		int currentPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);
	}
	
	@Override
	public Player findHighestScoringPlayer() {
		return players.stream()
		              .max(Comparator.comparing(p -> p.score().getPermanentScore()))
		              .orElse(null);
	}
	
	@Override
	public List<Player> getPlayers() {
		return players;
	}
	
	@Override
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	@Override
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	@Override
	protected void initialize() {
		// Initialization logic for players if necessary
	}
}
