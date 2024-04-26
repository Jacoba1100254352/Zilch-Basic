package modelManagers;


import abstracts.AbstractManager;
import models.GameOption;
import models.Player;
import ruleManagers.RuleManager;

import java.util.ArrayList;
import java.util.List;


public class GameOptionManager extends AbstractManager
{
	private final List<GameOption> gameOptions = new ArrayList<>();
	private final ActionManager actionManager;
	private final RuleManager ruleManager;
	private GameOption selectedGameOption = null;
	
	public GameOptionManager(ActionManager actionManager, RuleManager ruleManager) {
		this.actionManager = actionManager;
		this.ruleManager = ruleManager;
	}
	
	public void evaluateGameOptions(Player player) {
		gameOptions.clear();
		gameOptions.addAll(ruleManager.evaluateRules(player));
	}
	
	public void applyGameOption(GameOption option) {
		System.out.println("Applying game option: " + option.type());
		
		switch (option.type()) {
			case STRAIT:
				actionManager.scoreStraits();
				break;
			case SET:
				actionManager.scoreSets();
				break;
			case MULTIPLE:
				actionManager.scoreMultiple(option.value());
				break;
			case SINGLE:
				actionManager.scoreSingle(option.value());
				break;
			default:
				System.out.println("Invalid game option.");
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
	
	public boolean isOptionSelected() {
		return selectedGameOption != null;
	}
	
	@Override
	protected void initialize() {
		// Initialize if necessary
	}
}
