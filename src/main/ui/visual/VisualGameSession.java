package ui.visual;

import controllers.state.TurnContext;
import model.entities.GameOption;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.DiceManager;
import model.managers.GameOptionManager;
import model.managers.PlayerManager;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import rules.variable.FirstRollBustRule;
import rules.variable.IRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Event-loop friendly game coordinator for visual UIs. It reuses the existing
 * model and rule strategies, but exposes non-blocking actions for buttons.
 */
public class VisualGameSession
{
	public static final int MIN_PLAYERS = 1;
	public static final int MAX_PLAYERS = 6;
	public static final int MIN_SCORE_LIMIT = 1000;

	private static final int DEFAULT_PLAYERS = 2;
	private static final int DEFAULT_SCORE_LIMIT = 5000;
	private static final int SCORE_LIMIT_STEP = 500;
	private static final int FALLBACK_FIRST_ROLL_BUST_POINTS = 50;

	private final RuleRegistry setupRuleRegistry;
	private final List<IRule> selectableRules;
	private final Map<RuleType, Object> selectedRules = new LinkedHashMap<>();

	private int playerCount = DEFAULT_PLAYERS;
	private int scoreLimit = DEFAULT_SCORE_LIMIT;
	private PlayerManager playerManager;
	private ActionManager actionManager;
	private GameOptionManager gameOptionManager;
	private TurnContext turnContext;
	private List<GameOption> currentOptions = List.of();
	private Phase phase = Phase.SETUP;
	private boolean finalRound;
	private Player gameEndingPlayer;
	private String notice = "Choose the game setup, then start.";

	public VisualGameSession() {
		setupRuleRegistry = new RuleRegistry();
		selectableRules = setupRuleRegistry.getAvailableRules()
		                                   .stream()
		                                   .filter(IRule::isSelectableAtSetup)
		                                   .sorted(Comparator.comparing(IRule::getDisplayName))
		                                   .toList();
		selectedRules.putAll(setupRuleRegistry.getDefaultConfig());
	}

	public enum Phase
	{
		SETUP,
		AWAITING_ROLL,
		AWAITING_OPTION,
		AWAITING_DECISION,
		GAME_OVER
	}

	public void startGame() {
		if (!canStart()) {
			notice = "Enable at least one scoring rule before starting.";
			return;
		}

		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(selectedRules);
		gameOptionManager = new GameOptionManager(ruleManager);
		playerManager = new PlayerManager(buildDefaultPlayerNames());
		actionManager = new ActionManager(playerManager, new DiceManager(), scoreLimit);
		finalRound = false;
		gameEndingPlayer = null;
		beginTurn("Game started.");
	}

	public void resetToSetup() {
		playerManager = null;
		actionManager = null;
		gameOptionManager = null;
		turnContext = null;
		currentOptions = List.of();
		finalRound = false;
		gameEndingPlayer = null;
		phase = Phase.SETUP;
		notice = "Choose the game setup, then start.";
	}

	public void roll() {
		if (phase != Phase.AWAITING_ROLL) {
			return;
		}

		actionManager.rollDice();
		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
		currentOptions = gameOptionManager.getGameOptions();
		if (currentOptions.isEmpty()) {
			handleNoScoringOptions();
			return;
		}

		phase = Phase.AWAITING_OPTION;
		notice = "Choose a scoring option for " + getCurrentPlayer().name() + ".";
	}

	public void chooseOption(GameOption option) {
		if (phase != Phase.AWAITING_OPTION || option == null || !currentOptions.contains(option)) {
			return;
		}

		turnContext.setSelectedOption(option);
		gameOptionManager.setSelectedGameOption(option);
		gameOptionManager.applyGameOption(turnContext.toRuleContext(), option);
		currentOptions = List.of();

		String scoredMessage = getCurrentPlayer().name() + " scored " + option.pointsAwarded() + " points.";
		if (getCurrentPlayer().dice().getNumDiceInPlay() == 0) {
			actionManager.replenishAllDice();
			scoredMessage += " Hot dice - all six dice are back in play.";
		}

		phase = Phase.AWAITING_DECISION;
		if (canBankCurrentTurn()) {
			notice = scoredMessage + " Roll again or bank the round.";
		} else {
			notice = scoredMessage + " You need at least 1000 round points before banking.";
		}
	}

	public void rollAgain() {
		if (phase != Phase.AWAITING_DECISION) {
			return;
		}
		phase = Phase.AWAITING_ROLL;
		gameOptionManager.setSelectedGameOption(null);
		notice = getCurrentPlayer().name() + " is rolling again.";
	}

	public void bank() {
		if (phase != Phase.AWAITING_DECISION) {
			return;
		}
		if (!canBankCurrentTurn()) {
			notice = "You need at least 1000 round points before banking.";
			return;
		}

		Player completedPlayer = getCurrentPlayer();
		int bankedPoints = completedPlayer.score().getRoundScore();
		actionManager.bankCurrentRound(completedPlayer);
		String message = completedPlayer.name() + " banked " + bankedPoints + " points.";

		if (!finalRound && actionManager.hasReachedScoreLimit(completedPlayer)) {
			finalRound = true;
			gameEndingPlayer = completedPlayer;
			actionManager.setGameEndingPlayer(completedPlayer);
			message += " Final round started - everyone else gets one more turn.";
		}

		advanceTurn(message);
	}

	public void setPlayerCount(int playerCount) {
		if (phase != Phase.SETUP) {
			return;
		}
		this.playerCount = Math.max(MIN_PLAYERS, Math.min(MAX_PLAYERS, playerCount));
	}

	public void adjustPlayerCount(int delta) {
		setPlayerCount(playerCount + delta);
	}

	public void setScoreLimit(int scoreLimit) {
		if (phase != Phase.SETUP) {
			return;
		}
		this.scoreLimit = Math.max(MIN_SCORE_LIMIT, scoreLimit);
	}

	public void adjustScoreLimit(int steps) {
		setScoreLimit(scoreLimit + (steps * SCORE_LIMIT_STEP));
	}

	public void toggleRule(IRule rule) {
		setRuleEnabled(rule.getRuleType(), !isRuleEnabled(rule));
	}

	public void setRuleEnabled(RuleType ruleType, boolean enabled) {
		if (phase != Phase.SETUP) {
			return;
		}

		if (enabled) {
			IRule rule = setupRuleRegistry.getRule(ruleType);
			if (rule != null && rule.isSelectableAtSetup()) {
				selectedRules.put(ruleType, rule.getDefaultConfig());
			}
			return;
		}

		selectedRules.remove(ruleType);
		if (!canStart()) {
			notice = "At least one scoring rule is required.";
		}
	}

	public boolean isRuleEnabled(IRule rule) {
		return selectedRules.containsKey(rule.getRuleType());
	}

	public boolean canStart() {
		return selectableRules.stream()
		                      .filter(rule -> selectedRules.containsKey(rule.getRuleType()))
		                      .anyMatch(IRule::isScoringRule);
	}

	public Phase getPhase() {
		return phase;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public int getScoreLimit() {
		return scoreLimit;
	}

	public List<IRule> getSelectableRules() {
		return selectableRules;
	}

	public List<Player> getPlayers() {
		if (playerManager == null) {
			return List.of();
		}
		return Collections.unmodifiableList(playerManager.getPlayers());
	}

	public Player getCurrentPlayer() {
		return actionManager == null ? null : actionManager.getCurrentPlayer();
	}

	public List<GameOption> getCurrentOptions() {
		return currentOptions;
	}

	public List<Integer> getCurrentDiceValues() {
		Player player = getCurrentPlayer();
		if (player == null) {
			return List.of();
		}

		List<Integer> values = new ArrayList<>();
		for (int value = 1; value <= 6; value++) {
			int count = player.dice().getDiceSetMap().getOrDefault(value, 0);
			for (int index = 0; index < count; index++) {
				values.add(value);
			}
		}
		return values;
	}

	public int getDiceInPlay() {
		Player player = getCurrentPlayer();
		return player == null ? 0 : player.dice().getNumDiceInPlay();
	}

	public String getNotice() {
		return notice;
	}

	public boolean canBankCurrentTurn() {
		return actionManager != null && actionManager.canBankPoints(getCurrentPlayer());
	}

	public boolean isFinalRound() {
		return finalRound;
	}

	public Player getGameEndingPlayer() {
		return gameEndingPlayer;
	}

	private void handleNoScoringOptions() {
		if (turnContext.isFirstRoll() && gameOptionManager.isRuleActive(RuleType.FIRST_ROLL_BUST)) {
			int pointsAwarded = getFirstRollBustPoints();
			getCurrentPlayer().score().increaseRoundScore(pointsAwarded);
			currentOptions = List.of();
			phase = Phase.AWAITING_ROLL;
			notice = "First-roll bust: " + getCurrentPlayer().name() +
					" gets " + pointsAwarded + " points and rolls again.";
			return;
		}

		Player bustedPlayer = getCurrentPlayer();
		bustedPlayer.score().setRoundScore(0);
		bustedPlayer.score().setScoreFromMultiples(0);
		turnContext.markBusted();
		currentOptions = List.of();
		advanceTurn(bustedPlayer.name() + " busted. No points banked this turn.");
	}

	private void advanceTurn(String previousTurnMessage) {
		actionManager.switchToNextPlayer();
		if (finalRound && actionManager.getCurrentPlayer() == gameEndingPlayer) {
			concludeGame();
			return;
		}
		beginTurn(previousTurnMessage);
	}

	private void beginTurn(String previousTurnMessage) {
		Player player = actionManager.getCurrentPlayer();
		turnContext = new TurnContext(player);
		turnContext.resetForNewTurn();
		player.score().setRoundScore(0);
		player.score().setScoreFromMultiples(0);
		gameOptionManager.setSelectedGameOption(null);
		actionManager.replenishAllDice();
		currentOptions = List.of();
		phase = Phase.AWAITING_ROLL;
		notice = previousTurnMessage + " " + player.name() + " is up.";
	}

	private void concludeGame() {
		Player winner = actionManager.findHighestScoringPlayer();
		int winningScore = winner == null ? 0 : winner.score().getPermanentScore();
		List<Player> tiedPlayers = getPlayers().stream()
		                                      .filter(player -> player.score().getPermanentScore() == winningScore)
		                                      .toList();
		currentOptions = List.of();
		phase = Phase.GAME_OVER;

		if (tiedPlayers.size() > 1) {
			notice = "Game over. " + tiedPlayers.stream().map(Player::name).reduce((a, b) -> a + ", " + b).orElse("")
					+ " tied with " + winningScore + " points.";
		} else {
			notice = "Game over. " + winner.name() + " wins with " + winningScore + " points.";
		}
	}

	private int getFirstRollBustPoints() {
		if (gameOptionManager.getRule(RuleType.FIRST_ROLL_BUST) instanceof FirstRollBustRule firstRollBustRule) {
			return firstRollBustRule.getPointsAwarded();
		}
		return FALLBACK_FIRST_ROLL_BUST_POINTS;
	}

	private List<String> buildDefaultPlayerNames() {
		List<String> names = new ArrayList<>();
		for (int index = 1; index <= playerCount; index++) {
			names.add("Player " + index);
		}
		return names;
	}
}
