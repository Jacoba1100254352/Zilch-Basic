package client;

import managers.GameManager;

public class ZilchClient {
    public static void main(String[] args) {
        // Create an instance of the GameManager class
        GameManager game = new GameManager();

        // Start the game
        game.playGame();
    }
}
