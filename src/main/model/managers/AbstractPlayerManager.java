package model.managers;


import model.entities.Player;

import java.util.List;


public abstract class AbstractPlayerManager implements IPlayerManager
{
	protected Player currentPlayer;
	protected Player gameEndingPlayer;
	protected List<Player> players;
	
	@Override
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	@Override
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	@Override
	public Player getGameEndingPlayer() {
		return gameEndingPlayer;
	}
	
	@Override
	public void setGameEndingPlayer(Player gameEndingPlayer) {
		this.gameEndingPlayer = gameEndingPlayer;
	}
	
	@Override
	public List<Player> getPlayers() {
		return players;
	}
	
	@Override
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}
