package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class Config implements ReadOnlyConfig
{
	private final Properties properties;
	private final String filename;
	private int numPlayers;
	private List<String> playerNames;
	private int scoreLimit;
	
	public Config(String filename) throws IOException {
		this.filename = filename;
		properties = new Properties();
		
		try (FileInputStream in = new FileInputStream(filename)) {
			properties.load(in);
		} catch (FileNotFoundException e) {
			System.out.println("Configuration file not found. Creating a new one with default values.");
			// Set default values
			this.numPlayers = 3;
			this.playerNames = Arrays.asList("Alice", "Bob", "Charlie");
			this.scoreLimit = 5000;
			properties.setProperty("numPlayers", "3");
			properties.setProperty("playerNames", "Alice,Bob,Charlie");
			properties.setProperty("scoreLimit", "5000");
			saveConfig();
		} catch (IOException e) {
			throw new IOException("Error reading configuration file.", e);
		}
		
		if (properties.getProperty("numPlayers") != null) {
			this.numPlayers = Integer.parseInt(properties.getProperty("numPlayers"));
			this.playerNames = Arrays.asList(properties.getProperty("playerNames").split(","));
			this.scoreLimit = Integer.parseInt(properties.getProperty("scoreLimit"));
		}
	}
	
	@Override
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
		properties.setProperty("numPlayers", String.valueOf(numPlayers));
	}
	
	@Override
	public List<String> getPlayerNames() {
		return playerNames;
	}
	
	public void setPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
		properties.setProperty("playerNames", String.join(",", playerNames));
	}
	
	@Override
	public int getScoreLimit() {
		return scoreLimit;
	}
	
	public void setScoreLimit(int scoreLimit) {
		this.scoreLimit = scoreLimit;
		properties.setProperty("scoreLimit", String.valueOf(scoreLimit));
	}
	
	public void saveConfig() throws IOException {
		try (FileOutputStream out = new FileOutputStream(filename)) {
			properties.store(out, null);
		}
	}
}
