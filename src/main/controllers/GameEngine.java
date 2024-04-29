package controllers;


import eventHandling.dispatchers.IEventDispatcher;
import model.entities.Player;
import model.managers.AbstractManager;
import model.managers.ActionManager;
import model.managers.GameOptionManager;


public class GameEngine extends AbstractManager
{
	private final GameStateManager gameStateManager;
	private final IEventDispatcher eventDispatcher;
	private final GameOptionManager gameOptionManager;
	private final ActionManager actionManager;
	// private boolean gameOver = false;
	
	public GameEngine(
			IEventDispatcher eventDispatcher, GameStateManager gameStateManager, ActionManager actionManager,
			GameOptionManager gameOptionManager
	) {
		this.eventDispatcher = eventDispatcher;
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.gameStateManager = gameStateManager;
	}
	
	public void processGameTurn() {
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("No current player available.");
			return;
		}
		
		actionManager.rollDice();
		gameOptionManager.evaluateGameOptions(currentPlayer);
		
		if (!gameOptionManager.getGameOptions().isEmpty() && gameOptionManager.getSelectedGameOption() != null) {
			gameOptionManager.applyGameOption(gameOptionManager.getSelectedGameOption());
		} else {
			System.out.println("No options available, turn skipped.");
		}
		
		checkGameOver(currentPlayer);
	}
	
	public boolean isGameOver() {
		return gameStateManager.isBust();
	}
	
	private void checkGameOver(Player player) {
		if (canTurnEnd(player)) { // gameStateManager.canTurnEnd(player)
			// gameOver = true;
			System.out.println(player.name() + " has won the game!");
			// eventDispatcher.dispatchEvent(GameEventType.GAME_STATE_CHANGED, new Event(player.name() + " wins!"));
		}
	}
	
	private boolean canTurnEnd(Player player) {
		return player.score().getRoundScore() >= 1000 || player.score().getPermanentScore() >= 1000;
	}
	
	@Override
	public void doInitialize() {
		// Initialization logic here
	}
}
