package model.managers;


import model.entities.GameOption;
import model.entities.Player;
import rules.managers.IRuleManager;

import java.util.ArrayList;
import java.util.List;


public class GameOptionManager
{
	private final ActionManager actionManager;
	private final IRuleManager ruleManager;
	private final List<GameOption> gameOptions = new ArrayList<>();
	private GameOption selectedGameOption = null;
	
	public GameOptionManager(ActionManager actionManager, IRuleManager ruleManager) {
		this.actionManager = actionManager;
		this.ruleManager = ruleManager;
	}
	
	public void evaluateGameOptions(Player player) {
		gameOptions.clear();
		gameOptions.addAll(ruleManager.evaluateRules(player, isOptionSelected()));
	}
	
	public void applyGameOption(GameOption option) {
		System.out.println("Applying game option: " + option.type());
		
		switch (option.type()) {
			case STRAIT -> actionManager.scoreStraits();
			case SET -> actionManager.scoreSets();
			case MULTIPLE -> actionManager.scoreMultiple(option.value());
			case SINGLE -> actionManager.scoreSingle(option.value());
			case ROLL_AGAIN -> actionManager.rollDice();
			case END_TURN -> actionManager.endTurn();
			default -> System.out.println("Invalid game option.");
		}
		// Event dispatch could be triggered here if needed
	}
	
	public List<GameOption> getGameOptions() {
		return new ArrayList<>(gameOptions);
	}
	
	public GameOption getSelectedGameOption() {
		return selectedGameOption;
	}
	
	public void setSelectedGameOption(GameOption gameOption) {
		this.selectedGameOption = gameOption;
	}
	
	public int isOptionSelected() {
		return (selectedGameOption != null) ? 1 : 0;
	}
}
