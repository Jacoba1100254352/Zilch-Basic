// Player.java
public class Player {
	private final Dice dice;
	private final Score score;
	private final String name;
	
	public Player(String playerName) {
		this.name = playerName;
		this.dice = new Dice();
		this.score = new Score();
	}
	
	public Dice getDice() {
		return dice;
	}
	
	public Score getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
}
