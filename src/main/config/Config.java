package config;


import model.entities.ComputerDifficulty;
import model.entities.PlayerConfiguration;
import model.entities.PlayerType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;


public class Config implements ReadOnlyConfig
{
	private final Properties properties;
	private final String filename;
	private int numPlayers;
	private List<String> playerNames;
	private List<PlayerConfiguration> playerConfigurations;
	private int scoreLimit;
	private int openingScoreLimit;

	public Config(String filename) throws IOException {
		this.filename = filename;
		properties = new Properties();

		try (FileInputStream in = new FileInputStream(filename)) {
			properties.load(in);
		} catch (FileNotFoundException exception) {
			System.out.println("Configuration file not found. Creating a new one with default values.");
			setDefaults();
			saveConfig();
		} catch (IOException exception) {
			throw new IOException("Error reading configuration file.", exception);
		}

		if (properties.getProperty("numPlayers") != null) {
			numPlayers = Integer.parseInt(properties.getProperty("numPlayers"));
			playerNames = Arrays.asList(properties.getProperty("playerNames").split(","));
			scoreLimit = Integer.parseInt(properties.getProperty("scoreLimit"));
			openingScoreLimit = Integer.parseInt(properties.getProperty("openingScoreLimit", "1000"));
			playerConfigurations = readPlayerConfigurations();
		}
		if (playerConfigurations == null) {
			playerConfigurations = playerNames.stream().map(PlayerConfiguration::human).toList();
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
		this.playerNames = List.copyOf(playerNames);
		properties.setProperty("playerNames", String.join(",", playerNames));
		setPlayerMetadata(playerNames.stream().map(PlayerConfiguration::human).toList());
	}

	@Override
	public List<PlayerConfiguration> getPlayerConfigurations() {
		return List.copyOf(playerConfigurations);
	}

	public void setPlayerConfigurations(List<PlayerConfiguration> playerConfigurations) {
		this.playerConfigurations = List.copyOf(playerConfigurations);
		playerNames = playerConfigurations.stream().map(PlayerConfiguration::name).toList();
		numPlayers = playerConfigurations.size();
		properties.setProperty("numPlayers", String.valueOf(numPlayers));
		properties.setProperty("playerNames", String.join(",", playerNames));
		setPlayerMetadata(playerConfigurations);
	}

	@Override
	public int getScoreLimit() {
		return scoreLimit;
	}

	public void setScoreLimit(int scoreLimit) {
		this.scoreLimit = scoreLimit;
		properties.setProperty("scoreLimit", String.valueOf(scoreLimit));
	}

	@Override
	public int getOpeningScoreLimit() {
		return openingScoreLimit;
	}

	public void setOpeningScoreLimit(int openingScoreLimit) {
		this.openingScoreLimit = openingScoreLimit;
		properties.setProperty("openingScoreLimit", String.valueOf(openingScoreLimit));
	}

	public void saveConfig() throws IOException {
		try (FileOutputStream out = new FileOutputStream(filename)) {
			properties.store(out, null);
		}
	}

	private void setDefaults() {
		numPlayers = 2;
		playerNames = List.of("Alice", "Bob");
		playerConfigurations = playerNames.stream().map(PlayerConfiguration::human).toList();
		scoreLimit = 5000;
		openingScoreLimit = 1000;
		properties.setProperty("numPlayers", "2");
		properties.setProperty("playerNames", "Alice,Bob");
		properties.setProperty("playerTypes", "HUMAN,HUMAN");
		properties.setProperty("computerDifficulties", "NONE,NONE");
		properties.setProperty("scoreLimit", "5000");
		properties.setProperty("openingScoreLimit", "1000");
	}

	private List<PlayerConfiguration> readPlayerConfigurations() {
		String[] typeValues = properties.getProperty("playerTypes", "").split(",", -1);
		String[] difficultyValues = properties.getProperty("computerDifficulties", "").split(",", -1);
		List<PlayerConfiguration> configurations = new ArrayList<>();
		for (int index = 0; index < playerNames.size(); index++) {
			PlayerType type = parsePlayerType(typeValues, index);
			ComputerDifficulty difficulty = type == PlayerType.COMPUTER
					? parseDifficulty(difficultyValues, index)
					: null;
			configurations.add(new PlayerConfiguration(playerNames.get(index), type, difficulty));
		}
		return List.copyOf(configurations);
	}

	private PlayerType parsePlayerType(String[] values, int index) {
		if (index >= values.length || values[index].isBlank()) {
			return PlayerType.HUMAN;
		}
		try {
			return PlayerType.valueOf(values[index].trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException exception) {
			return PlayerType.HUMAN;
		}
	}

	private ComputerDifficulty parseDifficulty(String[] values, int index) {
		if (index >= values.length || values[index].isBlank() || values[index].equalsIgnoreCase("NONE")) {
			return ComputerDifficulty.MEDIUM;
		}
		try {
			return ComputerDifficulty.valueOf(values[index].trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException exception) {
			return ComputerDifficulty.MEDIUM;
		}
	}

	private void setPlayerMetadata(List<PlayerConfiguration> configurations) {
		playerConfigurations = List.copyOf(configurations);
		properties.setProperty(
				"playerTypes",
				configurations.stream()
				              .map(configuration -> configuration.type().name())
				              .collect(Collectors.joining(","))
		);
		properties.setProperty(
				"computerDifficulties",
				configurations.stream()
				              .map(configuration -> configuration.difficulty() == null
						              ? "NONE"
						              : configuration.difficulty().name())
				              .collect(Collectors.joining(","))
		);
	}
}
