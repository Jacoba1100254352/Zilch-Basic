package controllers.state;


import controllers.StealingManager;
import model.managers.GameOptionManager;
import rules.managers.RuleType;
import rules.variable.FirstRollBustRule;
import ui.IMessage;


/**
 * Evaluates the roll against the active rules and detects busts.
 */
public class EvaluateOptionsState implements GameTurnState
{
	private static final int FIRST_ROLL_BUST_POINTS = 50;

	private final GameOptionManager gameOptionManager;
	private final IMessage uiManager;
	private final StealingManager stealingManager;

	/**
	 * Creates the state that evaluates the current roll against active rules.
	 */
	public EvaluateOptionsState(GameOptionManager gameOptionManager, IMessage uiManager) {
		this(gameOptionManager, uiManager, new StealingManager(false, 0));
	}

	public EvaluateOptionsState(
			GameOptionManager gameOptionManager,
			IMessage uiManager,
			StealingManager stealingManager
	) {
		this.gameOptionManager = gameOptionManager;
		this.uiManager = uiManager;
		this.stealingManager = stealingManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
		if (gameOptionManager.getGameOptions().isEmpty()) {
			if (turnContext.isFirstRoll() && gameOptionManager.isRuleActive(RuleType.FIRST_ROLL_BUST)) {
				int pointsAwarded = getFirstRollBustPoints();
				turnContext.getPlayer().score().increaseRoundScore(pointsAwarded);
				displayTurnNotice(turnContext, "First-roll bust! Awarded " + pointsAwarded + " points. Roll again.\n");
				return GamePhase.ROLL_DICE;
			}

			turnContext.markBusted();
			turnContext.getPlayer().score().setRoundScore(0);
			turnContext.getPlayer().score().setScoreFromMultiples(0);
			stealingManager.clearContinuation();
			displayTurnNotice(turnContext, "Bust! No scoring options are available.\n");
			return GamePhase.END_TURN;
		}
		return GamePhase.SELECT_OPTION;
	}

	private int getFirstRollBustPoints() {
		if (gameOptionManager.getRule(RuleType.FIRST_ROLL_BUST) instanceof FirstRollBustRule firstRollBustRule) {
			return firstRollBustRule.getPointsAwarded();
		}
		return FIRST_ROLL_BUST_POINTS;
	}

	private void displayTurnNotice(TurnContext turnContext, String message) {
		if (turnContext.getPlayer().isComputer()) {
			uiManager.displayMessage(message);
		} else {
			uiManager.displayAndWait(message);
		}
	}
}
