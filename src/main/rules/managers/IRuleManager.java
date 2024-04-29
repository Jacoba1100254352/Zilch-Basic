package rules.managers;


import model.entities.GameOption;
import model.entities.Player;

import java.util.List;


public interface IRuleManager
{
	List<GameOption> evaluateRules(Player player, boolean isOptionSelectedForCurrentRoll);
}
