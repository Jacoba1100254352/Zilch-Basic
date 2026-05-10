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


class GameFactoryTest
{
	@Test
	void createGameServerBuildsConcreteServer() throws IOException {
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction();

		GameServer gameServer = new GameFactory().createGameServer(
				List.of("Alice", "Bob"),
				uiManager,
				"game-id",
				5000,
				userInteraction,
				Map.of(RuleType.SINGLE, Set.of(1, 5))
		);

		assertNotNull(gameServer);
	}
}
