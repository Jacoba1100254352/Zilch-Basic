package support;


import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;
import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import model.entities.TurnContinuation;
import model.managers.IDiceManager;
import model.managers.IPlayerManager;
import rules.managers.RuleType;
import ui.IInputManager;
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


public final class TestDoubles
{
	private TestDoubles() {
	}

	public static Player player(String name) {
		return new Player(name, new Dice(new LinkedHashMap<>()), new Score());
	}

	public static Player playerWithDice(String name, Map<Integer, Integer> diceSetMap) {
		Player player = player(name);
		player.dice().setDiceSetMap(new LinkedHashMap<>(diceSetMap));
		player.dice().calculateNumDiceInPlay();
		return player;
	}

	public static final class QueueInputManager implements IInputManager
	{
		private final Deque<String> stringInputs = new ArrayDeque<>();
		private final Deque<Object> intInputs = new ArrayDeque<>();
		public int waitCalls;

		public QueueInputManager addString(String value) {
			stringInputs.addLast(value);
			return this;
		}

		public QueueInputManager addInt(int value) {
			intInputs.addLast(value);
			return this;
		}

		public QueueInputManager addIntException(RuntimeException exception) {
			intInputs.addLast(exception);
			return this;
		}

		@Override
		public String getInputString() {
			if (stringInputs.isEmpty()) {
				throw new IllegalStateException("No queued string input is available.");
			}
			return stringInputs.removeFirst();
		}

		@Override
		public int getInputInt() {
			if (intInputs.isEmpty()) {
				throw new IllegalStateException("No queued integer input is available.");
			}

			Object next = intInputs.removeFirst();
			if (next instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			return (Integer) next;
		}

		@Override
		public Runnable waitForEnterKey() {
			return () -> waitCalls++;
		}
	}

	public static final class RecordingMessage implements IMessage
	{
		public final List<String> messageCalls = new ArrayList<>();
		public final List<String> waitingMessages = new ArrayList<>();
		public final List<String> currentScoreCalls = new ArrayList<>();
		public final List<Map<Integer, Integer>> displayedDice = new ArrayList<>();
		public final List<Integer> displayedDiceCounts = new ArrayList<>();
		public final List<List<GameOption>> displayedGameOptions = new ArrayList<>();
		public final List<Integer> displayedRoundScores = new ArrayList<>();
		public final List<String> highScoreCalls = new ArrayList<>();
		public final List<Player> lastRoundPlayers = new ArrayList<>();
		public final List<Player> winningPlayers = new ArrayList<>();
		public final List<Integer> winningScores = new ArrayList<>();
		public final List<List<Player>> tiePlayers = new ArrayList<>();
		public final List<Integer> tieScores = new ArrayList<>();
		public int welcomeCalls;
		public int rulesMenuCalls;
		public int clearCalls;
		public int pauseCalls;

		@Override
		public void displayWelcomeMessage() {
			welcomeCalls++;
		}

		@Override
		public void displayGameOptions(Score score, List<GameOption> gameOptions) {
			displayedRoundScores.add(score.getRoundScore());
			displayedGameOptions.add(new ArrayList<>(gameOptions));
		}

		@Override
		public void displayCurrentScore(String playerName, int roundScore) {
			currentScoreCalls.add(playerName + ":" + roundScore);
		}

		@Override
		public void displayDice(Dice dice) {
			displayedDice.add(new LinkedHashMap<>(dice.getDiceSetMap()));
			displayedDiceCounts.add(dice.getNumDiceInPlay());
		}

		@Override
		public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
			highScoreCalls.add(currentPlayer.name() + ":" + highestScoringPlayerName);
		}

		@Override
		public void displayMessage(String message) {
			messageCalls.add(message);
		}

		@Override
		public void displayAndWait(String message) {
			waitingMessages.add(message);
		}

		@Override
		public void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction) {
			lastRoundPlayers.add(gameEndingPlayer);
			if (waitFunction != null) {
				waitFunction.run();
			}
		}

		@Override
		public void announceTie(List<Player> tiedPlayers, int score) {
			tiePlayers.add(new ArrayList<>(tiedPlayers));
			tieScores.add(score);
		}

		@Override
		public void announceWinner(Player winner, int score) {
			winningPlayers.add(winner);
			winningScores.add(score);
		}

		@Override
		public void displayRulesMenu() {
			rulesMenuCalls++;
		}

		@Override
		public void clear() {
			clearCalls++;
		}

		@Override
		public void pauseAndContinue(Runnable waitFunction) {
			pauseCalls++;
			if (waitFunction != null) {
				waitFunction.run();
			}
		}
	}

	public static final class ScriptedUserInteraction implements IUserInteraction
	{
		private int numberOfPlayers = 2;
		private List<String> playerNames = List.of("Alice", "Bob");
		private int scoreLimit = 5000;
		private int openingScoreLimit = 1000;
		private Map<RuleType, Object> selectedRules = Map.of(RuleType.SINGLE, Set.of(1, 5));
		private Function<List<GameOption>, GameOption> optionChooser = options -> options.get(0);
		private final Deque<Boolean> scoreMoreDecisions = new ArrayDeque<>();
		private final Deque<Boolean> rollAgainDecisions = new ArrayDeque<>();
		private final Deque<Boolean> stealingDecisions = new ArrayDeque<>();

		public final List<Player> choosePlayers = new ArrayList<>();
		public final List<Player> rollAgainPlayers = new ArrayList<>();
		public final List<Boolean> canBankRequests = new ArrayList<>();
		public final List<Player> stealingPlayers = new ArrayList<>();
		public final List<TurnContinuation> stealingContinuations = new ArrayList<>();
		public int chooseCalls;
		public int scoreMoreCalls;
		public int rollAgainCalls;
		public int stealingCalls;

		public ScriptedUserInteraction withNumberOfPlayers(int numberOfPlayers) {
			this.numberOfPlayers = numberOfPlayers;
			return this;
		}

		public ScriptedUserInteraction withPlayerNames(List<String> playerNames) {
			this.playerNames = new ArrayList<>(playerNames);
			return this;
		}

		public ScriptedUserInteraction withScoreLimit(int scoreLimit) {
			this.scoreLimit = scoreLimit;
			return this;
		}

		public ScriptedUserInteraction withOpeningScoreLimit(int openingScoreLimit) {
			this.openingScoreLimit = openingScoreLimit;
			return this;
		}

		public ScriptedUserInteraction withSelectedRules(Map<RuleType, Object> selectedRules) {
			this.selectedRules = new LinkedHashMap<>(selectedRules);
			return this;
		}

		public ScriptedUserInteraction chooseWith(Function<List<GameOption>, GameOption> optionChooser) {
			this.optionChooser = optionChooser;
			return this;
		}

		public ScriptedUserInteraction addRollAgainDecision(boolean rollAgain) {
			rollAgainDecisions.addLast(rollAgain);
			return this;
		}

		public ScriptedUserInteraction addScoreMoreDecision(boolean scoreMore) {
			scoreMoreDecisions.addLast(scoreMore);
			return this;
		}

		public ScriptedUserInteraction addStealingDecision(boolean steal) {
			stealingDecisions.addLast(steal);
			return this;
		}

		@Override
		public int getNumberOfPlayers() {
			return numberOfPlayers;
		}

		@Override
		public List<String> getPlayerNames(int numPlayers) {
			return new ArrayList<>(playerNames);
		}

		@Override
		public int getValidScoreLimit() {
			return scoreLimit;
		}

		@Override
		public int getValidOpeningScoreLimit(int scoreLimit) {
			return openingScoreLimit;
		}

		@Override
		public Map<RuleType, Object> selectRules() {
			return new LinkedHashMap<>(selectedRules);
		}

		@Override
		public GameOption chooseGameOption(Player currentPlayer, List<GameOption> gameOptions) {
			chooseCalls++;
			choosePlayers.add(currentPlayer);
			return optionChooser.apply(gameOptions);
		}

		@Override
		public boolean shouldRollAgain(Player currentPlayer, boolean canBankPoints, int openingScoreLimit) {
			rollAgainCalls++;
			rollAgainPlayers.add(currentPlayer);
			canBankRequests.add(canBankPoints);
			if (rollAgainDecisions.isEmpty()) {
				return false;
			}
			return rollAgainDecisions.removeFirst();
		}

		@Override
		public boolean shouldScoreMore(Player currentPlayer, List<GameOption> remainingOptions) {
			scoreMoreCalls++;
			if (scoreMoreDecisions.isEmpty()) {
				return false;
			}
			return scoreMoreDecisions.removeFirst();
		}

		@Override
		public boolean shouldSteal(Player currentPlayer, TurnContinuation continuation) {
			stealingCalls++;
			stealingPlayers.add(currentPlayer);
			stealingContinuations.add(continuation);
			if (stealingDecisions.isEmpty()) {
				return false;
			}
			return stealingDecisions.removeFirst();
		}
	}

	public static final class StubPlayerManager implements IPlayerManager
	{
		private final List<Player> players;
		private int currentIndex;
		private Player gameEndingPlayer;
		public int switchCalls;

		public StubPlayerManager(List<Player> players) {
			this.players = new ArrayList<>(players);
			this.currentIndex = players.isEmpty() ? -1 : 0;
		}

		@Override
		public void switchToNextPlayer() {
			switchCalls++;
			if (players.isEmpty()) {
				return;
			}
			currentIndex = (currentIndex + 1) % players.size();
		}

		@Override
		public Player findHighestScoringPlayer() {
			return players.stream()
			              .max(Comparator.comparing(player -> player.score().getPermanentScore()))
			              .orElse(null);
		}

		@Override
		public List<Player> getPlayers() {
			return players;
		}

		@Override
		public void setPlayers(List<Player> players) {
			this.players.clear();
			this.players.addAll(players);
			currentIndex = players.isEmpty() ? -1 : 0;
		}

		@Override
		public Player getCurrentPlayer() {
			return currentIndex < 0 ? null : players.get(currentIndex);
		}

		@Override
		public void setCurrentPlayer(Player player) {
			if (player == null) {
				currentIndex = -1;
				return;
			}

			int existingIndex = players.indexOf(player);
			if (existingIndex >= 0) {
				currentIndex = existingIndex;
				return;
			}

			players.add(player);
			currentIndex = players.size() - 1;
		}

		@Override
		public Player getGameEndingPlayer() {
			return gameEndingPlayer;
		}

		@Override
		public void setGameEndingPlayer(Player player) {
			this.gameEndingPlayer = player;
		}
	}

	public static final class SequencedDiceManager implements IDiceManager
	{
		private final Deque<Map<Integer, Integer>> queuedRolls = new ArrayDeque<>();
		public int rollCalls;
		public int replenishCalls;
		public int removeAllCalls;
		public final List<String> removeCalls = new ArrayList<>();

		public SequencedDiceManager queueRoll(Map<Integer, Integer> diceSetMap) {
			queuedRolls.addLast(new LinkedHashMap<>(diceSetMap));
			return this;
		}

		@Override
		public void rollDice(Dice dice) {
			rollCalls++;
			if (queuedRolls.isEmpty()) {
				throw new IllegalStateException("No queued roll is available.");
			}
			dice.setDiceSetMap(queuedRolls.removeFirst());
			dice.calculateNumDiceInPlay();
		}

		@Override
		public void replenishAllDice(Dice dice) {
			replenishCalls++;
			dice.getDiceSetMap().clear();
			dice.setNumDiceInPlay(Dice.FULL_SET_OF_DICE);
		}

		@Override
		public void removeAllDice(Dice dice) {
			removeAllCalls++;
			dice.getDiceSetMap().clear();
			dice.setNumDiceInPlay(0);
		}

		@Override
		public void removeDice(Dice dice, int dieValue) {
			removeCalls.add("all:" + dieValue);
			dice.getDiceSetMap().remove(dieValue);
			dice.calculateNumDiceInPlay();
		}

		@Override
		public void removeDice(Dice dice, int dieValue, int numToRemove) {
			removeCalls.add(dieValue + ":" + numToRemove);
			int remaining = dice.getDiceSetMap().getOrDefault(dieValue, 0) - numToRemove;
			if (remaining > 0) {
				dice.getDiceSetMap().put(dieValue, remaining);
			} else {
				dice.getDiceSetMap().remove(dieValue);
			}
			dice.calculateNumDiceInPlay();
		}
	}

	public static final class RecordingEventDispatcher implements IEventDispatcher
	{
		public final Map<GameEventType, List<IEventListener>> listeners = new EnumMap<>(GameEventType.class);
		public final List<Event> dispatchedEvents = new ArrayList<>();

		@Override
		public void addListener(GameEventType eventType, IEventListener listener) {
			listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
		}

		@Override
		public void removeListener(GameEventType eventType, IEventListener listener) {
			listeners.getOrDefault(eventType, List.of()).remove(listener);
		}

		@Override
		public void dispatchEvent(Event event) throws IOException {
			dispatchedEvents.add(event);
			for (IEventListener listener : new ArrayList<>(listeners.getOrDefault(event.getType(), List.of()))) {
				listener.handleEvent(event);
			}
		}
	}
}
