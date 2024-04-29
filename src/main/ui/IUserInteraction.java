package ui;


import java.util.List;


public interface IUserInteraction
{
	int getNumberOfPlayers();
	
	List<String> getPlayerNames(int numPlayers);
	
	int getValidScoreLimit();
}
