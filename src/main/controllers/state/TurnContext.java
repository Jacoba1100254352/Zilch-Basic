package controllers.state;


import model.entities.GameOption;
import model.entities.Player;
import rules.context.RuleContext;

import java.util.HashMap;
import java.util.Map;


/**
 * Mutable per-turn state shared across the turn state machine. It carries the
 * current player, the selected option, and any bookkeeping that rules need
 * while the turn is in progress.
 */
public class TurnContext
{
	private final Player player;
	private final Map<Integer, Integer> scoredMultiples = new HashMap<>();
	private GameOption selectedOption;
	private boolean busted;

	/**
	 * Creates a new mutable context for the supplied player's turn.
	 */
	public TurnContext(Player player) {
		this.player = player;
	}

	/**
	 * Returns the player whose turn is being processed.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the map used to track previously scored multiples this turn.
	 */
	public Map<Integer, Integer> getScoredMultiples() {
		return scoredMultiples;
	}

	/**
	 * Builds the immutable rule-facing context used during evaluation and
	 * application of scoring options.
	 */
	public RuleContext toRuleContext() {
		return new RuleContext(player, player.dice().getDiceSetMap(), scoredMultiples);
	}

	/**
	 * Returns the option currently selected for application.
	 */
	public GameOption getSelectedOption() {
		return selectedOption;
	}

	/**
	 * Stores the option selected by the player for the current state cycle.
	 */
	public void setSelectedOption(GameOption selectedOption) {
		this.selectedOption = selectedOption;
	}

	/**
	 * Returns whether the turn has ended in a bust.
	 */
	public boolean isBusted() {
		return busted;
	}

	/**
	 * Marks the current turn as a bust.
	 */
	public void markBusted() {
		this.busted = true;
	}

	/**
	 * Clears turn-local state before a fresh turn begins.
	 */
	public void resetForNewTurn() {
		selectedOption = null;
		scoredMultiples.clear();
		busted = false;
	}
}
