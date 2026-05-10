package eventHandling.listeners;


import controllers.GameServer;
import eventHandling.dispatchers.SimpleEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import model.entities.Player;
import model.managers.ActionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameOverListenerTest
{
	@Test
	void handleGameOverAnnouncesWinner() throws Exception {
		TrackingActionManager actionManager = new TrackingActionManager();
		RecordingMessage uiManager = new RecordingMessage();
		SpyGameServer gameServer = new SpyGameServer(actionManager, uiManager);
		GameOverListener listener = new GameOverListener(5000, gameServer, actionManager, uiManager);
		Player winner = TestDoubles.player("Jacob");
		winner.score().increasePermanentScore(6500);

		Event event = new Event(GameEventType.GAME_OVER);
		event.setData(EventDataKey.WINNER, winner);
		listener.handleEvent(event);

		assertEquals(List.of(winner), uiManager.winningPlayers);
		assertEquals(List.of(6500), uiManager.winningScores);
		assertEquals(0, gameServer.handleLastTurnsCalls);
	}

	@Test
	void scoreUpdatedAtOrAboveLimitStartsLastTurnFlowOnce() throws Exception {
		TrackingActionManager actionManager = new TrackingActionManager();
		RecordingMessage uiManager = new RecordingMessage();
		SpyGameServer gameServer = new SpyGameServer(actionManager, uiManager);
		GameOverListener listener = new GameOverListener(5000, gameServer, actionManager, uiManager);
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(5000);

		Event scoreUpdated = new Event(GameEventType.SCORE_UPDATED);
		scoreUpdated.setData(EventDataKey.PLAYER, player);

		listener.handleEvent(scoreUpdated);
		listener.handleEvent(scoreUpdated);

		assertSame(player, actionManager.recordedGameEndingPlayer);
		assertEquals(1, actionManager.setGameEndingCalls);
		assertEquals(List.of(player), uiManager.lastRoundPlayers);
		assertEquals(1, gameServer.handleLastTurnsCalls);
	}

	@Test
	void scoreUpdatedBelowLimitDoesNothing() throws Exception {
		TrackingActionManager actionManager = new TrackingActionManager();
		RecordingMessage uiManager = new RecordingMessage();
		SpyGameServer gameServer = new SpyGameServer(actionManager, uiManager);
		GameOverListener listener = new GameOverListener(5000, gameServer, actionManager, uiManager);
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(4999);

		Event event = new Event(GameEventType.SCORE_UPDATED);
		event.setData(EventDataKey.PLAYER, player);
		listener.handleEvent(event);

		assertEquals(0, actionManager.setGameEndingCalls);
		assertEquals(0, gameServer.handleLastTurnsCalls);
		assertTrue(uiManager.lastRoundPlayers.isEmpty());
	}

	private static final class TrackingActionManager extends ActionManager
	{
		private Player recordedGameEndingPlayer;
		private int setGameEndingCalls;

		TrackingActionManager() {
			super(new StubPlayerManager(List.of(TestDoubles.player("Alice"))), new SequencedDiceManager(), 5000);
		}

		@Override
		public void setGameEndingPlayer(Player player) {
			super.setGameEndingPlayer(player);
			recordedGameEndingPlayer = player;
			setGameEndingCalls++;
		}
	}

	private static final class SpyGameServer extends GameServer
	{
		private int handleLastTurnsCalls;

		SpyGameServer(ActionManager actionManager, RecordingMessage uiManager) throws IOException {
			super(
					new SimpleEventDispatcher(),
					actionManager,
					configuredRuleManager(),
					uiManager,
					5000,
					new ScriptedUserInteraction(),
					"game-id"
			);
		}

		@Override
		public void handleLastTurns() {
			handleLastTurnsCalls++;
		}
	}

	private static RuleManager configuredRuleManager() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
		selectedRules.put(RuleType.SINGLE, Set.of(1, 5));
		ruleManager.initializeRules(selectedRules);
		return ruleManager;
	}
}
