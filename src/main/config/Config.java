package config;


import java.io.FileInputStream;
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
		FileInputStream in = new FileInputStream(filename);
		properties.load(in);
		in.close();
		
		this.numPlayers = Integer.parseInt(properties.getProperty("numPlayers"));
		this.playerNames = Arrays.asList(properties.getProperty("playerNames").split(","));
		this.scoreLimit = Integer.parseInt(properties.getProperty("scoreLimit"));
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
		FileOutputStream out = new FileOutputStream(filename);
		properties.store(out, null);
		out.close();
	}
}
