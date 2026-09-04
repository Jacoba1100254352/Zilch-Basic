package config;


import model.entities.ComputerDifficulty;
import model.entities.PlayerConfiguration;
import model.entities.PlayerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ConfigTest
{
	@TempDir
	Path tempDirectory;

	@Test
	void missingConfigurationUsesCanonicalSharedDefaults() throws Exception {
		Path configPath = tempDirectory.resolve("defaults.properties");

		Config config = new Config(configPath.toString());

		assertEquals(2, config.getNumPlayers());
		assertEquals(List.of("Alice", "Bob"), config.getPlayerNames());
		assertEquals(5000, config.getScoreLimit());
		assertEquals(1000, config.getOpeningScoreLimit());
		assertEquals(
				List.of(
						PlayerConfiguration.human("Alice"),
						PlayerConfiguration.human("Bob")
				),
				config.getPlayerConfigurations()
		);
	}

	@Test
	void openingScoreLimitIsPersistedWithTheOtherGameSettings() throws Exception {
		Path configPath = tempDirectory.resolve("zilch.properties");
		Config config = new Config(configPath.toString());
		config.setOpeningScoreLimit(1500);
		config.saveConfig();

		Config reloaded = new Config(configPath.toString());

		assertEquals(1500, reloaded.getOpeningScoreLimit());
	}

	@Test
	void legacyConfigurationWithoutOpeningScoreUsesTheTraditionalDefault() throws Exception {
		Path configPath = tempDirectory.resolve("legacy.properties");
		Files.writeString(
				configPath,
				"numPlayers=2\nplayerNames=Alice,Bob\nscoreLimit=5000\n"
		);

		Config config = new Config(configPath.toString());

		assertEquals(1000, config.getOpeningScoreLimit());
		assertEquals(PlayerType.HUMAN, config.getPlayerConfigurations().get(0).type());
		assertEquals(PlayerType.HUMAN, config.getPlayerConfigurations().get(1).type());
	}

	@Test
	void computerPlayersAndDifficultyArePersisted() throws Exception {
		Path configPath = tempDirectory.resolve("players.properties");
		Config config = new Config(configPath.toString());
		config.setPlayerConfigurations(List.of(
				PlayerConfiguration.human("Alice"),
				PlayerConfiguration.computer("Computer", ComputerDifficulty.HARD)
		));
		config.saveConfig();

		Config reloaded = new Config(configPath.toString());

		assertEquals(2, reloaded.getNumPlayers());
		assertEquals(List.of("Alice", "Computer"), reloaded.getPlayerNames());
		assertEquals(PlayerType.COMPUTER, reloaded.getPlayerConfigurations().get(1).type());
		assertEquals(ComputerDifficulty.HARD, reloaded.getPlayerConfigurations().get(1).difficulty());
	}

	@Test
	void missingOrInvalidComputerDifficultyMigratesToMedium() throws Exception {
		Path configPath = tempDirectory.resolve("legacy-computer.properties");
		Files.writeString(
				configPath,
				"numPlayers=2\nplayerNames=Alice,Computer\nplayerTypes=HUMAN,COMPUTER\n" +
						"computerDifficulties=NONE,unknown\nscoreLimit=5000\n"
		);

		Config config = new Config(configPath.toString());

		assertEquals(ComputerDifficulty.MEDIUM, config.getPlayerConfigurations().get(1).difficulty());
	}
}
