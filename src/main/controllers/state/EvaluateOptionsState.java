package controllers.state;


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
			if (turnContext.isFirstRoll() && gameOptionManager.isRuleActive(RuleType.FIRST_ROLL_BUST)) {
				int pointsAwarded = getFirstRollBustPoints();
				turnContext.getPlayer().score().increaseRoundScore(pointsAwarded);
				uiManager.displayAndWait("First-roll bust! Awarded " + pointsAwarded + " points. Roll again.\n");
				return GamePhase.ROLL_DICE;
			}

			turnContext.markBusted();
			turnContext.getPlayer().score().setRoundScore(0);
			turnContext.getPlayer().score().setScoreFromMultiples(0);
			uiManager.displayAndWait("Bust! No scoring options are available.\n");
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
}
