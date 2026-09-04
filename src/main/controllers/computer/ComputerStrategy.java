package controllers.computer;


import model.entities.ComputerDifficulty;
import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.TurnContinuation;
import model.managers.ActionManager;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Deterministic computer decisions for both the console state machine and visual session.
 */
public class ComputerStrategy
{
	public static final ComputerPolicy EASY_POLICY = new ComputerPolicy(
			"Easy",
			Map.of(1, 600, 2, 600, 3, 600, 4, 600, 5, 600, 6, 600),
			1, 0, 0, 0, 0, 0, 0, 0
	);
	public static final ComputerPolicy MEDIUM_POLICY = new ComputerPolicy(
			"Medium",
			Map.of(1, 350, 2, 500, 3, 700, 4, 850, 5, 1000, 6, 1150),
			1, 55, 240, 95, 0.08, 0.10, 0.25, 15
	);
	/** Policy produced by the standard Computers vs Zilch simulation. */
	public static final ComputerPolicy HARD_STANDARD_POLICY = new ComputerPolicy(
			"Hard",
			Map.of(1, 200, 2, 1021, 3, 1128, 4, 1506, 5, 2130, 6, 2130),
			1.0045, 36.0805, 354.561, 91.9329, 0, 0.293194, 0.193316, 136.066
	);
	/** Policy produced by the Stealing-enabled Computers vs Zilch simulation. */
	public static final ComputerPolicy HARD_STEALING_POLICY = new ComputerPolicy(
			"Hard with Stealing",
			Map.of(1, 313, 2, 313, 3, 1106, 4, 1360, 5, 1360, 6, 1376),
			0.88553, 91.2663, 229.628, 94.2546, 0, 0.187935, 0.20764, -26.2974
	);

	private final ActionManager actionManager;
	private final boolean finalChaseEnabled;
	private final boolean allowTies;
	private final boolean stealingEnabled;

	public ComputerStrategy(
			ActionManager actionManager,
			boolean finalChaseEnabled,
			boolean allowTies,
			boolean stealingEnabled
	) {
		this.actionManager = Objects.requireNonNull(actionManager, "actionManager cannot be null.");
		this.finalChaseEnabled = finalChaseEnabled;
		this.allowTies = allowTies;
		this.stealingEnabled = stealingEnabled;
	}

	/**
	 * Chooses one scoring option. Easy takes the largest immediate score. Medium
	 * and Hard value the score, remaining dice, hot dice, and multiples with
	 * their respective policies.
	 */
	public GameOption chooseGameOption(Player player, List<GameOption> options) {
		if (options.isEmpty()) {
			throw new IllegalArgumentException("At least one game option is required.");
		}

		ComputerDifficulty difficulty = difficulty(player);
		ComputerPolicy policy = policyFor(player);
		GameOption best = options.get(0);
		for (int index = 1; index < options.size(); index++) {
			GameOption candidate = options.get(index);
			if (difficulty == ComputerDifficulty.EASY
					? isBetterSimpleOption(candidate, best)
					: optionUtility(player, candidate, policy) > optionUtility(player, best, policy)) {
				best = candidate;
			}
		}
		return best;
	}

	/**
	 * Easy and Medium take every compatible score from a roll. Hard can preserve
	 * more dice when another option is less valuable than rolling the pool.
	 */
	public boolean shouldScoreMore(Player player, List<GameOption> remainingOptions) {
		if (remainingOptions.isEmpty()) {
			return false;
		}
		if (difficulty(player) != ComputerDifficulty.HARD) {
			return true;
		}

		ComputerPolicy policy = policyFor(player);
		GameOption best = chooseGameOption(player, remainingOptions);
		double rollUtility = policy.rollBias() + policy.remainingDiceWeight() * normalizedDiceInPlay(player);
		return optionUtility(player, best, policy) >= rollUtility;
	}

	/** Returns true when the computer should risk another roll rather than bank. */
	public boolean shouldRollAgain(Player player, boolean canBankPoints) {
		if (!canBankPoints) {
			return true;
		}

		int roundScore = player.score().getRoundScore();
		int remainingDice = normalizedDiceInPlay(player);
		Boolean endgameDecision = shouldBankForEndgame(player, roundScore, remainingDice);
		if (endgameDecision != null) {
			return !endgameDecision;
		}

		return roundScore < policyBankThreshold(player, roundScore, remainingDice);
	}

	/** Applies the difficulty-specific continuation acceptance rule. */
	public boolean shouldSteal(Player player, TurnContinuation continuation) {
		return switch (difficulty(player)) {
			case EASY -> continuation.inheritedScore() >= 600;
			case MEDIUM -> continuation.inheritedScore() >=
					MEDIUM_POLICY.bankThreshold(continuation.diceInPlay()) * 0.7;
			case HARD -> {
				ComputerPolicy policy = policyFor(player);
				double continuationUtility = policy.scoreWeight() * continuation.inheritedScore()
						+ policy.remainingDiceWeight() * continuation.diceInPlay()
						+ policy.rollBias();
				double freshRollUtility = policy.remainingDiceWeight() * Dice.FULL_SET_OF_DICE;
				yield continuationUtility >= freshRollUtility;
			}
		};
	}

	int policyBankThreshold(Player player, int roundScore, int remainingDice) {
		ComputerPolicy policy = policyFor(player);
		int threshold = policy.bankThreshold(remainingDice);
		int lead = player.score().getPermanentScore() - maxOpponentScore(player);
		if (lead > 0) {
			threshold -= (int) (lead * policy.leadFactor());
		} else {
			threshold += (int) (-lead * policy.trailFactor());
		}

		int projectedTotal = player.score().getPermanentScore() + roundScore;
		int distanceToWin = actionManager.getScoreLimit() - projectedTotal;
		int closingWindow = Math.max(0, 1500 - Math.max(distanceToWin, 0));
		threshold -= (int) (closingWindow * policy.closingFactor());
		return Math.min(actionManager.getScoreLimit(), Math.max(200, threshold));
	}

	private Boolean shouldBankForEndgame(Player player, int roundScore, int remainingDice) {
		int projectedTotal = player.score().getPermanentScore() + roundScore;
		int opponentScore = maxOpponentScore(player);
		int winningScore = actionManager.getScoreLimit();

		if (actionManager.getGameEndingPlayer() != null) {
			return allowTies ? projectedTotal >= opponentScore : projectedTotal > opponentScore;
		}

		ComputerDifficulty difficulty = difficulty(player);
		if (projectedTotal >= winningScore) {
			if (!finalChaseEnabled || actionManager.getPlayers().size() == 1 || difficulty == ComputerDifficulty.EASY) {
				return true;
			}

			int opponentDistance = Math.max(0, winningScore - opponentScore);
			int desiredBuffer = opponentDistance <= 500 ? 1000 : opponentDistance <= 1000 ? 500 : 0;
			if (desiredBuffer == 0 || projectedTotal >= winningScore + desiredBuffer) {
				return true;
			}

			int minimumDiceToPress = difficulty == ComputerDifficulty.HARD ? 3 : 4;
			return remainingDice < minimumDiceToPress;
		}

		if (!finalChaseEnabled || difficulty == ComputerDifficulty.EASY) {
			return null;
		}

		int distanceToTarget = winningScore - projectedTotal;
		if (distanceToTarget <= 150) {
			if (projectedTotal - opponentScore >= 500) {
				return true;
			}
			if (remainingDice >= 3) {
				return false;
			}
		}
		return null;
	}

	private double optionUtility(Player player, GameOption option, ComputerPolicy policy) {
		int nextDiceCount = normalizedDiceInPlay(player) - consumedDice(option);
		boolean hotDice = nextDiceCount <= 0;
		if (hotDice) {
			nextDiceCount = Dice.FULL_SET_OF_DICE;
		}

		double utility = policy.scoreWeight() * option.pointsAwarded()
				+ policy.remainingDiceWeight() * nextDiceCount;
		if (hotDice) {
			utility += policy.hotDiceWeight();
		}
		if (option.type().equals(RuleType.MULTIPLE) || option.type().equals(RuleType.ADD_MULTIPLE)) {
			utility += policy.multipleWeight();
			if (option.type().equals(RuleType.ADD_MULTIPLE)) {
				utility += policy.multipleWeight() * 0.5;
			}
		}
		return utility;
	}

	private boolean isBetterSimpleOption(GameOption candidate, GameOption best) {
		if (candidate.pointsAwarded() != best.pointsAwarded()) {
			return candidate.pointsAwarded() > best.pointsAwarded();
		}
		return consumedDice(candidate) > consumedDice(best);
	}

	private int consumedDice(GameOption option) {
		return option.consumedDice().values().stream().mapToInt(Integer::intValue).sum();
	}

	private int normalizedDiceInPlay(Player player) {
		return player.dice().getNumDiceInPlay() == 0
				? Dice.FULL_SET_OF_DICE
				: player.dice().getNumDiceInPlay();
	}

	private int maxOpponentScore(Player player) {
		return actionManager.getPlayers().stream()
		                    .filter(candidate -> candidate != player)
		                    .mapToInt(candidate -> candidate.score().getPermanentScore())
		                    .max()
		                    .orElse(0);
	}

	private ComputerDifficulty difficulty(Player player) {
		return Objects.requireNonNullElse(player.difficulty(), ComputerDifficulty.MEDIUM);
	}

	private ComputerPolicy policyFor(Player player) {
		return switch (difficulty(player)) {
			case EASY -> EASY_POLICY;
			case MEDIUM -> MEDIUM_POLICY;
			case HARD -> stealingEnabled ? HARD_STEALING_POLICY : HARD_STANDARD_POLICY;
		};
	}
}
