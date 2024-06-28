package config;


import java.util.List;


public interface ReadOnlyConfig
{
	int getNumPlayers();
	
	List<String> getPlayerNames();
	
	int getScoreLimit();
}
