package managers;


import modelManagers.PlayerManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DeterministicPlayerManager extends PlayerManager
{
	private final Deque<Map<Integer, Integer>> scriptedRolls;
	
	@SafeVarargs
	DeterministicPlayerManager(List<String> playerNames, int scoreLimit, Map<Integer, Integer>... scriptedRolls) {
		super(playerNames, scoreLimit);
		this.scriptedRolls = new ArrayDeque<>();
		for (Map<Integer, Integer> scriptedRoll : scriptedRolls) {
			this.scriptedRolls.addLast(new HashMap<>(scriptedRoll));
		}
	}
	
	@Override
	public void rollDice() {
		if (scriptedRolls.isEmpty()) {
			throw new IllegalStateException("No scripted rolls remain for this test.");
		}
		
		getCurrentPlayer().dice().setDiceSetMap(scriptedRolls.removeFirst());
		getCurrentPlayer().dice().calculateNumDiceInPlay();
	}
}
