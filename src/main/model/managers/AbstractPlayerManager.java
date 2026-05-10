package model.managers;


import model.entities.Player;

import java.util.List;


public abstract class AbstractPlayerManager implements IPlayerManager
{
	protected Player currentPlayer;
	protected Player gameEndingPlayer;
	protected List<Player> players;
	
	/** {@inheritDoc} */
	@Override
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	/** {@inheritDoc} */
	@Override
	public Player getGameEndingPlayer() {
		return gameEndingPlayer;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setGameEndingPlayer(Player gameEndingPlayer) {
		this.gameEndingPlayer = gameEndingPlayer;
	}
	
	/** {@inheritDoc} */
	@Override
	public List<Player> getPlayers() {
		return players;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}
