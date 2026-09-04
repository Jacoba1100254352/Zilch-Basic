package ui;


import controllers.computer.ComputerStrategy;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.PlayerConfiguration;
import model.entities.TurnContinuation;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Routes human decisions to the selected interface and computer decisions to a strategy.
 */
public class PlayerAwareUserInteraction implements IUserInteraction
{
	private final IUserInteraction humanInteraction;
	private final ComputerStrategy computerStrategy;
	private final IMessage decisionMessages;

	public PlayerAwareUserInteraction(IUserInteraction humanInteraction, ComputerStrategy computerStrategy) {
		this(humanInteraction, computerStrategy, null);
	}

	public PlayerAwareUserInteraction(
			IUserInteraction humanInteraction,
			ComputerStrategy computerStrategy,
			IMessage decisionMessages
	) {
		this.humanInteraction = Objects.requireNonNull(humanInteraction, "humanInteraction cannot be null.");
		this.computerStrategy = Objects.requireNonNull(computerStrategy, "computerStrategy cannot be null.");
		this.decisionMessages = decisionMessages;
	}

	@Override
	public int getNumberOfPlayers() {
		return humanInteraction.getNumberOfPlayers();
	}

	@Override
	public List<String> getPlayerNames(int numPlayers) {
		return humanInteraction.getPlayerNames(numPlayers);
	}

	@Override
	public List<PlayerConfiguration> getPlayerConfigurations(int numPlayers) {
		return humanInteraction.getPlayerConfigurations(numPlayers);
	}

	@Override
	public int getValidScoreLimit() {
		return humanInteraction.getValidScoreLimit();
	}

	@Override
	public int getValidOpeningScoreLimit(int scoreLimit) {
		return humanInteraction.getValidOpeningScoreLimit(scoreLimit);
	}

	@Override
	public Map<RuleType, Object> selectRules() {
		return humanInteraction.selectRules();
	}

	@Override
	public GameOption chooseGameOption(Player currentPlayer, List<GameOption> gameOptions) {
		if (!currentPlayer.isComputer()) {
			return humanInteraction.chooseGameOption(currentPlayer, gameOptions);
		}
		GameOption option = computerStrategy.chooseGameOption(currentPlayer, gameOptions);
		String selectedValue = option.selectedValue() == null ? "" : " [" + option.selectedValue() + "]";
		announce(
				PlayerText.withPresentVerb(currentPlayer.name(), "select", "selects") + " " +
						option.displayName() + selectedValue +
						" for " + option.pointsAwarded() + " points.\n"
		);
		return option;
	}

	@Override
	public boolean shouldScoreMore(Player currentPlayer, List<GameOption> remainingOptions) {
		if (!currentPlayer.isComputer()) {
			return humanInteraction.shouldScoreMore(currentPlayer, remainingOptions);
		}
		boolean scoreMore = computerStrategy.shouldScoreMore(currentPlayer, remainingOptions);
		if (scoreMore) {
			announce(PlayerText.withPresentVerb(currentPlayer.name(), "score", "scores") +
					" another option from this roll.\n");
		}
		return scoreMore;
	}

	@Override
	public boolean shouldRollAgain(Player currentPlayer, boolean canBankPoints, int openingScoreLimit) {
		if (!currentPlayer.isComputer()) {
			return humanInteraction.shouldRollAgain(currentPlayer, canBankPoints, openingScoreLimit);
		}
		boolean rollAgain = computerStrategy.shouldRollAgain(currentPlayer, canBankPoints);
		announce(rollAgain
				? PlayerText.withPresentVerb(currentPlayer.name(), "roll", "rolls") + " again.\n"
				: PlayerText.withPresentVerb(currentPlayer.name(), "bank", "banks") + " " +
						currentPlayer.score().getRoundScore() + " points.\n");
		return rollAgain;
	}

	@Override
	public boolean shouldSteal(Player currentPlayer, TurnContinuation continuation) {
		if (!currentPlayer.isComputer()) {
			return humanInteraction.shouldSteal(currentPlayer, continuation);
		}
		boolean steal = computerStrategy.shouldSteal(currentPlayer, continuation);
		announce(
				steal
						? PlayerText.withPresentVerb(currentPlayer.name(), "accept", "accepts") + " " +
								PlayerText.possessive(continuation.sourcePlayerName()) + " " +
								continuation.inheritedScore() + "-point continuation with " +
								continuation.diceInPlay() + " dice.\n"
						: PlayerText.withPresentVerb(currentPlayer.name(), "decline", "declines") +
								" the continuation and " +
								(PlayerText.isSecondPerson(currentPlayer.name()) ? "start" : "starts") +
								" fresh.\n"
		);
		return steal;
	}

	private void announce(String message) {
		if (decisionMessages != null) {
			decisionMessages.displayMessage(message);
		}
	}
}
