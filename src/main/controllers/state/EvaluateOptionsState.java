package controllers.state;


import model.managers.GameOptionManager;
import ui.IMessage;


/**
 * Evaluates the roll against the active rules and detects busts.
 */
public class EvaluateOptionsState implements GameTurnState
{
	private final GameOptionManager gameOptionManager;
	private final IMessage uiManager;

	/**
	 * Creates the state that evaluates the current roll against active rules.
	 */
	public EvaluateOptionsState(GameOptionManager gameOptionManager, IMessage uiManager) {
		this.gameOptionManager = gameOptionManager;
		this.uiManager = uiManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
		if (gameOptionManager.getGameOptions().isEmpty()) {
			turnContext.markBusted();
			turnContext.getPlayer().score().setRoundScore(0);
			turnContext.getPlayer().score().setScoreFromMultiples(0);
			uiManager.displayAndWait("Bust! No scoring options are available.\n");
			return GamePhase.END_TURN;
		}
		return GamePhase.SELECT_OPTION;
	}
}
