package ui.visual;

import controllers.StealingManager;
import controllers.computer.ComputerStrategy;
import controllers.state.TurnContext;
import model.entities.ComputerDifficulty;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.PlayerConfiguration;
import model.entities.TurnContinuation;
import model.managers.ActionManager;
import model.managers.DiceManager;
import model.managers.GameOptionManager;
import model.managers.IDiceManager;
import model.managers.PlayerManager;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import rules.variable.FirstRollBustRule;
import rules.variable.IRule;
import ui.PlayerText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Event-loop friendly game coordinator for visual UIs. It reuses the existing
 * model and rule strategies, but exposes non-blocking actions for buttons.
 */
public class VisualGameSession
{
	public static final int MIN_PLAYERS = 1;
	public static final int MAX_PLAYERS = 6;
	public static final int MIN_SCORE_LIMIT = 1000;
	public static final int MIN_OPENING_SCORE_LIMIT = 0;

	private static final int DEFAULT_PLAYERS = 2;
	private static final int DEFAULT_SCORE_LIMIT = 5000;
	private static final int SCORE_LIMIT_STEP = 500;
	private static final int DEFAULT_OPENING_SCORE_LIMIT = 1000;
	private static final int OPENING_SCORE_LIMIT_STEP = 250;
	private static final int FALLBACK_FIRST_ROLL_BUST_POINTS = 50;
	private static final float COMPUTER_ACTION_DELAY_SECONDS = 0.45f;

	private final RuleRegistry setupRuleRegistry;
	private final List<IRule> selectableRules;
	private final Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
	private final IDiceManager diceManager;

	private int playerCount = DEFAULT_PLAYERS;
	private int scoreLimit = DEFAULT_SCORE_LIMIT;
	private int openingScoreLimit = DEFAULT_OPENING_SCORE_LIMIT;
	private boolean computerOpponentEnabled;
	private ComputerDifficulty computerDifficulty = ComputerDifficulty.MEDIUM;
	private PlayerManager playerManager;
	private ActionManager actionManager;
	private GameOptionManager gameOptionManager;
	private StealingManager stealingManager;
	private ComputerStrategy computerStrategy;
	private float computerActionTimer;
	private TurnContext turnContext;
	private List<GameOption> currentOptions = List.of();
	private Phase phase = Phase.SETUP;
	private boolean finalRound;
	private boolean finalChaseEnabled;
	private boolean allowTies;
	private Player gameEndingPlayer;
	private Player incumbentHighScorer;
	private int incumbentHighScore = Integer.MIN_VALUE;
	private String notice = "Choose the game setup, then start.";

	public VisualGameSession() {
		this(new DiceManager());
	}

	VisualGameSession(IDiceManager diceManager) {
		this.diceManager = Objects.requireNonNull(diceManager, "diceManager cannot be null.");
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
		AWAITING_STEAL_DECISION,
		AWAITING_ROLL,
		AWAITING_OPTION,
		AWAITING_DECISION,
		AWAITING_BUST_ACKNOWLEDGEMENT,
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
		playerManager = PlayerManager.fromConfigurations(buildPlayerConfigurations());
		actionManager = new ActionManager(
				playerManager,
				diceManager,
				scoreLimit,
				openingScoreLimit
		);
		stealingManager = new StealingManager(
				selectedRules.containsKey(RuleType.STEALING),
				openingScoreLimit
		);
		finalChaseEnabled = selectedRules.containsKey(RuleType.FINAL_CHASE);
		allowTies = selectedRules.containsKey(RuleType.ALLOW_TIES);
		computerStrategy = new ComputerStrategy(
				actionManager,
				finalChaseEnabled,
				allowTies,
				selectedRules.containsKey(RuleType.STEALING)
		);
		computerActionTimer = 0;
		finalRound = false;
		gameEndingPlayer = null;
		incumbentHighScorer = null;
		incumbentHighScore = Integer.MIN_VALUE;
		beginTurn("Game started.");
	}

	public void resetToSetup() {
		playerManager = null;
		actionManager = null;
		gameOptionManager = null;
		stealingManager = null;
		computerStrategy = null;
		computerActionTimer = 0;
		turnContext = null;
		currentOptions = List.of();
		finalRound = false;
		finalChaseEnabled = false;
		allowTies = false;
		gameEndingPlayer = null;
		incumbentHighScorer = null;
		incumbentHighScore = Integer.MIN_VALUE;
		phase = Phase.SETUP;
		notice = "Choose the game setup, then start.";
	}

	/**
	 * Advances one computer decision after a short visual pause. Human turns are untouched.
	 */
	public void update(float deltaSeconds) {
		if (!isComputerTurn() || phase == Phase.SETUP || phase == Phase.GAME_OVER) {
			computerActionTimer = 0;
			return;
		}
		computerActionTimer += Math.max(0, deltaSeconds);
		if (computerActionTimer < COMPUTER_ACTION_DELAY_SECONDS) {
			return;
		}
		computerActionTimer = 0;
		performComputerAction();
	}

	public void steal() {
		if (phase != Phase.AWAITING_STEAL_DECISION || stealingManager == null) {
			return;
		}

		TurnContinuation continuation = stealingManager.acceptContinuation(turnContext);
		phase = Phase.AWAITING_ROLL;
		notice = getCurrentPlayer().name() + " accepted " +
				PlayerText.possessive(continuation.sourcePlayerName()) + " " + continuation.inheritedScore() +
				"-point continuation and must score with " +
				continuation.diceInPlay() + " dice to keep them.";
	}

	public void freshRoll() {
		if (phase != Phase.AWAITING_STEAL_DECISION || stealingManager == null) {
			return;
		}

		stealingManager.clearContinuation();
		phase = Phase.AWAITING_ROLL;
		notice = getCurrentPlayer().name() + " declined the steal and will roll all six dice.";
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

		String scoredMessage = getCurrentPlayer().name() + " scored " + option.pointsAwarded() + " points.";
		if (getCurrentPlayer().dice().getNumDiceInPlay() == 0) {
			actionManager.replenishAllDice();
			turnContext.clearScoredMultiples();
			currentOptions = List.of();
			scoredMessage += " Hot dice - all six dice are back in play.";
		} else {
			gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
			currentOptions = gameOptionManager.getGameOptions();
		}

		phase = Phase.AWAITING_DECISION;
		if (!currentOptions.isEmpty()) {
			scoredMessage += " More scoring options remain in this roll.";
		}
		if (canBankCurrentTurn()) {
			notice = scoredMessage + (currentOptions.isEmpty()
					? " Roll again or bank the round."
					: " Score more, roll again, or bank the round.");
		} else {
			notice = scoredMessage + " " + PlayerText.withPresentVerb(
					getCurrentPlayer().name(),
					"need",
					"needs"
			) + " at least " +
					openingScoreLimit + " round points before banking.";
		}
	}

	public void rollAgain() {
		if (phase != Phase.AWAITING_DECISION) {
			return;
		}
		phase = Phase.AWAITING_ROLL;
		currentOptions = List.of();
		gameOptionManager.setSelectedGameOption(null);
		notice = PlayerText.withPresentVerb(getCurrentPlayer().name(), "are", "is") + " rolling again.";
	}

	public void scoreMore() {
		if (phase != Phase.AWAITING_DECISION || currentOptions.isEmpty()) {
			return;
		}
		turnContext.setSelectedOption(null);
		gameOptionManager.setSelectedGameOption(null);
		phase = Phase.AWAITING_OPTION;
		notice = "Choose another scoring option from this roll.";
	}

	public void bank() {
		if (phase != Phase.AWAITING_DECISION) {
			return;
		}
		if (!canBankCurrentTurn()) {
			notice = PlayerText.withPresentVerb(getCurrentPlayer().name(), "need", "needs") +
					" at least " + openingScoreLimit +
					" round points before banking.";
			return;
		}

		Player completedPlayer = getCurrentPlayer();
		int bankedPoints = completedPlayer.score().getRoundScore();
		stealingManager.offerContinuation(turnContext);
		actionManager.bankCurrentRound(completedPlayer);
		recordHighScore(completedPlayer);
		String message = completedPlayer.name() + " banked " + bankedPoints + " points.";

		if (!finalRound && actionManager.hasReachedScoreLimit(completedPlayer)) {
			gameEndingPlayer = completedPlayer;
			actionManager.setGameEndingPlayer(completedPlayer);
			if (finalChaseEnabled) {
				finalRound = true;
				message += " Final chase started - everyone else gets one more turn.";
			} else {
				concludeGame();
				return;
			}
		}

		advanceTurn(message);
	}

	public void setPlayerCount(int playerCount) {
		if (phase != Phase.SETUP) {
			return;
		}
		int minimum = computerOpponentEnabled ? 2 : MIN_PLAYERS;
		this.playerCount = Math.max(minimum, Math.min(MAX_PLAYERS, playerCount));
	}

	public void adjustPlayerCount(int delta) {
		setPlayerCount(playerCount + delta);
	}

	public void setComputerOpponentEnabled(boolean enabled) {
		if (phase != Phase.SETUP) {
			return;
		}
		computerOpponentEnabled = enabled;
		if (enabled) {
			playerCount = Math.max(2, playerCount);
		}
	}

	public void toggleComputerOpponent() {
		setComputerOpponentEnabled(!computerOpponentEnabled);
	}

	public boolean isComputerOpponentEnabled() {
		return computerOpponentEnabled;
	}

	public void cycleComputerDifficulty() {
		if (phase != Phase.SETUP || !computerOpponentEnabled) {
			return;
		}
		ComputerDifficulty[] difficulties = ComputerDifficulty.values();
		computerDifficulty = difficulties[(computerDifficulty.ordinal() + 1) % difficulties.length];
	}

	public void setComputerDifficulty(ComputerDifficulty difficulty) {
		if (phase == Phase.SETUP && difficulty != null) {
			computerDifficulty = difficulty;
		}
	}

	public ComputerDifficulty getComputerDifficulty() {
		return computerDifficulty;
	}

	public void setScoreLimit(int scoreLimit) {
		if (phase != Phase.SETUP) {
			return;
		}
		this.scoreLimit = Math.max(MIN_SCORE_LIMIT, scoreLimit);
		openingScoreLimit = Math.min(openingScoreLimit, this.scoreLimit);
	}

	public void adjustScoreLimit(int steps) {
		setScoreLimit(scoreLimit + (steps * SCORE_LIMIT_STEP));
	}

	public void setOpeningScoreLimit(int openingScoreLimit) {
		if (phase != Phase.SETUP) {
			return;
		}
		this.openingScoreLimit = Math.max(
				MIN_OPENING_SCORE_LIMIT,
				Math.min(scoreLimit, openingScoreLimit)
		);
	}

	public void adjustOpeningScoreLimit(int steps) {
		setOpeningScoreLimit(openingScoreLimit + (steps * OPENING_SCORE_LIMIT_STEP));
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

	public int getOpeningScoreLimit() {
		return openingScoreLimit;
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

	public boolean isComputerTurn() {
		Player player = getCurrentPlayer();
		return player != null && player.isComputer();
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

	public boolean canScoreMore() {
		return phase == Phase.AWAITING_DECISION && !currentOptions.isEmpty();
	}

	/**
	 * Returns the next player in turn order without advancing or clearing the
	 * current roll.
	 */
	public Player getNextPlayer() {
		if (actionManager == null) {
			return null;
		}
		List<Player> players = actionManager.getPlayers();
		Player currentPlayer = getCurrentPlayer();
		int currentIndex = players.indexOf(currentPlayer);
		if (players.isEmpty() || currentIndex < 0) {
			return null;
		}
		return players.get((currentIndex + 1) % players.size());
	}

	public boolean isFinalRound() {
		return finalRound;
	}

	public Player getGameEndingPlayer() {
		return gameEndingPlayer;
	}

	/**
	 * Acknowledges a visible bust result and only then advances the turn.
	 */
	public void acknowledgeBust() {
		if (phase != Phase.AWAITING_BUST_ACKNOWLEDGEMENT) {
			return;
		}
		Player bustedPlayer = getCurrentPlayer();
		advanceTurn(bustedPlayer.name() + " busted. No points were banked.");
	}

	private void handleNoScoringOptions() {
		if (turnContext.isFirstRoll() && gameOptionManager.isRuleActive(RuleType.FIRST_ROLL_BUST)) {
			int pointsAwarded = getFirstRollBustPoints();
			getCurrentPlayer().score().increaseRoundScore(pointsAwarded);
			currentOptions = List.of();
			phase = Phase.AWAITING_ROLL;
			notice = "First-roll bust: " + PlayerText.withPresentVerb(
					getCurrentPlayer().name(),
					"get",
					"gets"
			) + " " + pointsAwarded + " points and " +
					(PlayerText.isSecondPerson(getCurrentPlayer().name()) ? "roll" : "rolls") + " again.";
			return;
		}

		Player bustedPlayer = getCurrentPlayer();
		bustedPlayer.score().setRoundScore(0);
		bustedPlayer.score().setScoreFromMultiples(0);
		turnContext.markBusted();
		stealingManager.clearContinuation();
		currentOptions = List.of();
		phase = Phase.AWAITING_BUST_ACKNOWLEDGEMENT;
		notice = "Bust! " + bustedPlayer.name() +
				" rolled no scoring dice. No points were banked. Review the roll, then continue.";
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
		computerStrategy.startTurn();
		Player player = actionManager.getCurrentPlayer();
		turnContext = new TurnContext(player);
		turnContext.resetForNewTurn();
		player.score().setRoundScore(0);
		player.score().setScoreFromMultiples(0);
		gameOptionManager.setSelectedGameOption(null);
		actionManager.replenishAllDice();
		currentOptions = List.of();

		String turnMessage = previousTurnMessage + " " +
				PlayerText.withPresentVerb(player.name(), "are", "is") + " up.";
		if (stealingManager.hasAvailableContinuation()) {
			TurnContinuation continuation = stealingManager.getAvailableContinuation().orElseThrow();
			if (stealingManager.canSteal(player)) {
				phase = Phase.AWAITING_STEAL_DECISION;
				notice = turnMessage + " Continue " + PlayerText.possessive(continuation.sourcePlayerName()) +
						" " + continuation.inheritedScore() + "-point turn with " +
						continuation.diceInPlay() + " dice, or start fresh.";
				return;
			}

			stealingManager.clearContinuation();
			if (player.score().getPermanentScore() < openingScoreLimit) {
				turnMessage += " Stealing is unavailable until this player has already banked " +
						openingScoreLimit + " points; this turn starts fresh.";
			} else {
				turnMessage += " A player cannot steal their own prior turn; this turn starts fresh.";
			}
		}

		phase = Phase.AWAITING_ROLL;
		notice = turnMessage;
	}

	private void concludeGame() {
		List<Player> tiedPlayers = actionManager.findHighestScoringPlayers();
		currentOptions = List.of();
		phase = Phase.GAME_OVER;
		if (tiedPlayers.isEmpty()) {
			notice = "Game over. No winner.";
			return;
		}
		int winningScore = tiedPlayers.get(0).score().getPermanentScore();

		if (allowTies && tiedPlayers.size() > 1) {
			notice = "Game over. " + tiedPlayers.stream().map(Player::name).reduce((a, b) -> a + ", " + b).orElse("")
					+ " tied with " + winningScore + " points.";
		} else {
			Player winner = resolveWinner(tiedPlayers);
			notice = "Game over. " + PlayerText.withPresentVerb(winner.name(), "win", "wins") +
					" with " + winningScore + " points.";
		}
	}

	private void recordHighScore(Player player) {
		int score = player.score().getPermanentScore();
		if (incumbentHighScorer == null || score > incumbentHighScore) {
			incumbentHighScorer = player;
			incumbentHighScore = score;
		}
	}

	private Player resolveWinner(List<Player> highestScoringPlayers) {
		if (highestScoringPlayers.contains(incumbentHighScorer)) {
			return incumbentHighScorer;
		}
		return highestScoringPlayers.isEmpty() ? null : highestScoringPlayers.get(0);
	}

	private int getFirstRollBustPoints() {
		if (gameOptionManager.getRule(RuleType.FIRST_ROLL_BUST) instanceof FirstRollBustRule firstRollBustRule) {
			return firstRollBustRule.getPointsAwarded();
		}
		return FALLBACK_FIRST_ROLL_BUST_POINTS;
	}

	private void performComputerAction() {
		Player player = getCurrentPlayer();
		if (player == null || !player.isComputer() || computerStrategy == null) {
			return;
		}

		switch (phase) {
			case AWAITING_STEAL_DECISION -> {
				TurnContinuation continuation = stealingManager.getAvailableContinuation().orElse(null);
				if (continuation != null && computerStrategy.shouldSteal(player, continuation)) {
					steal();
				} else {
					freshRoll();
				}
			}
			case AWAITING_ROLL -> roll();
			case AWAITING_OPTION -> chooseOption(computerStrategy.chooseGameOption(player, currentOptions));
			case AWAITING_BUST_ACKNOWLEDGEMENT -> acknowledgeBust();
			case AWAITING_DECISION -> {
				if (canScoreMore() && computerStrategy.shouldScoreMore(player, currentOptions)) {
					scoreMore();
				} else if (computerStrategy.shouldRollAgain(player, canBankCurrentTurn())) {
					rollAgain();
				} else {
					bank();
				}
			}
			default -> {
				// Setup and terminal phases do not accept computer actions.
			}
		}
	}

	private List<PlayerConfiguration> buildPlayerConfigurations() {
		List<PlayerConfiguration> players = new ArrayList<>();
		for (int index = 1; index <= playerCount; index++) {
			if (computerOpponentEnabled && index == 2) {
				players.add(PlayerConfiguration.computer("Computer", computerDifficulty));
			} else {
				players.add(PlayerConfiguration.human("Player " + index));
			}
		}
		return players;
	}
}
