package model.managers;


import model.entities.GameOption;
import model.entities.Player;
import rules.managers.IRuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GameOptionManager
{
	private final IRuleManager ruleManager;
	private final List<GameOption> gameOptions = new ArrayList<>();
	private GameOption selectedGameOption = null;
	
	public GameOptionManager(IRuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}
	
	public boolean isValid() {
		return !this.gameOptions.isEmpty() && this.selectedGameOption != null;
	}
	
	public void evaluateGameOptions(Map<Integer, Integer> diceSetMap, Integer value) {
		gameOptions.clear();
		gameOptions.addAll(ruleManager.evaluateRules(diceSetMap, value));
	}
	
	public void applyGameOption(Player player, GameOption gameOption) {
		System.out.println("Applying game option: " + this.selectedGameOption.type());
		
		if (gameOption == null) {
			ruleManager.applyRule(player, this.selectedGameOption);
		} else {
			ruleManager.applyRule(player, gameOption);
		}
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
}
