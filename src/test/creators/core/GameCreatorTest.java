package creators.core;


import controllers.GameServer;
import creators.patterns.GameBuilder;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class GameCreatorTest
{
	@Test
	void createSimpleGameServerCreatesServerThroughFactoryPath() throws IOException {
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction();

		GameServer gameServer = new GameCreator().createSimpleGameServer(
				List.of("Alice", "Bob"),
				uiManager,
				"game-id",
				5000,
				userInteraction,
				Map.of(RuleType.SINGLE, Set.of(1, 5))
		);

		assertNotNull(gameServer);
	}

	@Test
	void newGameServerBuilderReturnsBuilder() {
		GameBuilder builder = new GameCreator().newGameServerBuilder();

		assertNotNull(builder);
	}
}
