package ui;


import rules.managers.IRuleManager;

import java.util.List;


public interface IUserInteraction
{
	int getNumberOfPlayers();
	
	List<String> getPlayerNames(int numPlayers);
	
	int getValidScoreLimit();
	
	void selectRules(IRuleManager ruleManager); // Map<RuleType, Boolean>
}
