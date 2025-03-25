package managers;


import models.Score;
import ui.UserInputHandler;

import java.util.List;


/**
 * A no-op (fake) user-input handler for JUnit tests.
 * It never blocks, and it can simulate user actions automatically.
 */
public class FakeUserInputHandler implements UserInputHandler
{
	
	private final GameCoordinator gameCoordinator;
	
	public FakeUserInputHandler(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
	}
	
	@Override
	public int getValidScoreLimit() {
		// For tests, just return something
		return 2000;
	}
	
	@Override
	public List<String> getPlayerNames() {
		// Return a simple list of test players
		return List.of("TestPlayer");
	}
	
	@Override
	public void inputGameOption() {
		// For tests, we can do something trivial:
		// e.g. just always "end turn" immediately
		gameCoordinator.getGameStateManager().setReroll(false);
		gameCoordinator.getGameStateManager().setContinueTurn(false);
		
		Score score = gameCoordinator.getPlayerManager().getCurrentPlayer().score();
		if (score.getRoundScore() + score.getPermanentScore() >= 1000) {
			score.increasePermanentScore(score.getRoundScore());
			score.setRoundScore(0);
		}
	}
	
	@Override
	public void pauseAndContinue() {
		// Do nothing—no real console I/O
	}
}
