package config;


import model.entities.PlayerConfiguration;

import java.util.List;


public interface ReadOnlyConfig
{
	int getNumPlayers();
	
	List<String> getPlayerNames();

	default List<PlayerConfiguration> getPlayerConfigurations() {
		return getPlayerNames().stream().map(PlayerConfiguration::human).toList();
	}
	
	int getScoreLimit();

	int getOpeningScoreLimit();
}
