package config;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

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
		assertEquals(java.util.List.of("Alice", "Bob"), config.getPlayerNames());
		assertEquals(5000, config.getScoreLimit());
		assertEquals(1000, config.getOpeningScoreLimit());
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
	}
}
