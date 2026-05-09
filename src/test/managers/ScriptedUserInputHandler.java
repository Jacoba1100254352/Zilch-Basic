package managers;


import models.GameOption;
import models.Score;
import ui.UserInputHandler;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;


class ScriptedUserInputHandler implements UserInputHandler
{
	enum ActionType
	{
		SELECT, ROLL_AGAIN, END_TURN
	}
	
	record Action(ActionType type, GameOption.Type optionType, Integer optionValue)
	{
	}
	
	private final GameCoordinator gameCoordinator;
	private final int scoreLimit;
	private final List<String> playerNames;
	private final Deque<Action> actions;
	private int pauseCount;
	
	ScriptedUserInputHandler(GameCoordinator gameCoordinator, int scoreLimit, List<String> playerNames, Action... actions) {
		this.gameCoordinator = gameCoordinator;
		this.scoreLimit = scoreLimit;
		this.playerNames = playerNames;
		this.actions = new ArrayDeque<>(Arrays.asList(actions));
		this.pauseCount = 0;
	}
	
	static Action select(GameOption.Type type, Integer value) {
		return new Action(ActionType.SELECT, type, value);
	}
	
	static Action rollAgain() {
		return new Action(ActionType.ROLL_AGAIN, null, null);
	}
	
	static Action endTurn() {
		return new Action(ActionType.END_TURN, null, null);
	}
	
	@Override
	public int getValidScoreLimit() {
		return scoreLimit;
	}
	
	@Override
	public List<String> getPlayerNames() {
		return playerNames;
	}
	
	@Override
	public void inputGameOption() {
		if (actions.isEmpty()) {
			throw new IllegalStateException("No scripted actions remain for this test.");
		}
		
		Action action = actions.removeFirst();
		switch (action.type()) {
			case SELECT -> {
				GameOption selectedOption = gameCoordinator.getGameOptionManager().getGameOptions().stream()
				                                           .filter(option -> option.type() == action.optionType()
						                                           && (option.value() == null || option.value().equals(action.optionValue())))
				                                           .findFirst()
				                                           .orElseThrow(() -> new IllegalStateException("Requested option was not available: " + action));
				gameCoordinator.getGameOptionManager().setSelectedGameOption(selectedOption);
				gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
				gameCoordinator.getGameOptionManager().processMove();
			}
			case ROLL_AGAIN -> gameCoordinator.getGameStateManager().setReroll(true);
			case END_TURN -> {
				gameCoordinator.getGameStateManager().setReroll(false);
				gameCoordinator.getGameStateManager().setContinueTurn(false);
				bankRoundScore();
			}
		}
	}
	
	@Override
	public void pauseAndContinue() {
		pauseCount++;
	}
	
	int getPauseCount() {
		return pauseCount;
	}
	
	private void bankRoundScore() {
		Score score = gameCoordinator.getPlayerManager().getCurrentPlayer().score();
		if (score.getRoundScore() + score.getPermanentScore() >= 1000) {
			score.increasePermanentScore(score.getRoundScore());
			score.setRoundScore(0);
		}
	}
}
