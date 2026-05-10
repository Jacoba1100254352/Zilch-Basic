package controllers;


import controllers.state.GamePhase;
import controllers.state.GameTurnState;
import controllers.state.TurnContext;
import org.junit.jupiter.api.Test;
import support.TestDoubles;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AbstractGameStateManagerTest
{
	@Test
	void processTurnAdvancesThroughRegisteredStatesUntilEndTurn() throws Exception {
		List<GamePhase> visitedPhases = new ArrayList<>();
		Map<GamePhase, GameTurnState> states = new EnumMap<>(GamePhase.class);
		states.put(GamePhase.START_TURN, turnContext -> {
			visitedPhases.add(GamePhase.START_TURN);
			return GamePhase.ROLL_DICE;
		});
		states.put(GamePhase.ROLL_DICE, turnContext -> {
			visitedPhases.add(GamePhase.ROLL_DICE);
			return GamePhase.END_TURN;
		});
		states.put(GamePhase.END_TURN, turnContext -> {
			visitedPhases.add(GamePhase.END_TURN);
			return GamePhase.END_TURN;
		});

		TestStateManager gameStateManager = new TestStateManager(states);
		gameStateManager.processTurn(new TurnContext(TestDoubles.player("Jacob")));

		assertEquals(List.of(GamePhase.START_TURN, GamePhase.ROLL_DICE, GamePhase.END_TURN), visitedPhases);
		assertEquals(GamePhase.END_TURN, gameStateManager.getCurrentPhase());
	}

	@Test
	void processTurnThrowsWhenAPhaseHasNoRegisteredState() {
		Map<GamePhase, GameTurnState> states = new EnumMap<>(GamePhase.class);
		states.put(GamePhase.START_TURN, turnContext -> GamePhase.ROLL_DICE);

		TestStateManager gameStateManager = new TestStateManager(states);

		assertThrows(IllegalStateException.class, () -> gameStateManager.processTurn(new TurnContext(TestDoubles.player("Jacob"))));
	}

	private static final class TestStateManager extends AbstractGameStateManager
	{
		TestStateManager(Map<GamePhase, GameTurnState> states) {
			super(states);
		}
	}
}
