package modelManagers;

import abstracts.AbstractManager;
import events.Event;
import interfaces.IEventDispatcher;
import models.Player;
import ruleManagers.RuleManager;


public class GameEngine extends AbstractManager
{
	private final IEventDispatcher eventDispatcher;
	private final GameOptionManager gameOptionManager;
	private final ActionManager actionManager;
	// private boolean gameOver = false;
	
	public GameEngine(IEventDispatcher eventDispatcher, ActionManager actionManager, RuleManager ruleManager) {
		this.eventDispatcher = eventDispatcher;
		this.actionManager = actionManager;
		this.gameOptionManager = new GameOptionManager(actionManager, ruleManager);
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
	
	private void checkGameOver(Player player) {
		if (canTurnEnd(player)) {
			// gameOver = true;
			System.out.println(player.name() + " has won the game!");
			eventDispatcher.dispatchEvent("gameStateChanged", new Event(player.name() + " wins!"));
		}
	}
	
	private boolean canTurnEnd(Player player) {
		return player.score().getRoundScore() >= 1000 || player.score().getPermanentScore() >= 1000;
	}
	
	@Override
	protected void initialize() {
		// Initialization logic here
	}
}
