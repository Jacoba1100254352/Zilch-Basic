package creators.core;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class GameIDManager
{
	private final Set<String> gameIDs;
	
	public GameIDManager() {
		gameIDs = new HashSet<>();
	}
	
	public String generateGameID() {
		String gameID;
		do {
			gameID = UUID.randomUUID().toString();
		} while (gameIDs.contains(gameID));
		
		gameIDs.add(gameID);
		return gameID;
	}
	
	public boolean isGameIDUnique(String gameID) {
		return !gameIDs.contains(gameID);
	}
	
	public void addGameID(String gameID) {
		gameIDs.add(gameID);
	}
	
	// Placeholder for future database integration
	public void loadGameIDsFromDatabase() {
		// Implement database loading logic here
	}
	
	public void saveGameIDsToDatabase() {
		// Implement database saving logic here
	}
}
