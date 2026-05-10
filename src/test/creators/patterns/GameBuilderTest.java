package creators.patterns;


import controllers.GameServer;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class GameBuilderTest
{
	@Test
	void buildThrowsWhenRequiredFieldsAreMissing() {
		GameBuilder builder = new GameBuilder();

		assertThrows(IllegalStateException.class, builder::build);
	}

	@Test
	void buildCreatesGameServerWhenRequiredFieldsArePresent() throws IOException {
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction();

		GameServer gameServer = new GameBuilder()
				.setPlayerNames(List.of("Alice", "Bob"))
				.setUiManager(uiManager)
				.setUserInteraction(userInteraction)
				.setSelectedRules(Map.of(RuleType.SINGLE, Set.of(1, 5)))
				.build();

		assertNotNull(gameServer);
	}
}
