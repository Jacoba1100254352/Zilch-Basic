package ui;


import rules.managers.RuleType;

import java.util.List;
import java.util.Map;


public interface IUserInteraction
{
	int getNumberOfPlayers();
	
	List<String> getPlayerNames(int numPlayers);
	
	int getValidScoreLimit();
	
	Map<RuleType, Object> selectRules();
	
	Integer getOptionValue();
}
