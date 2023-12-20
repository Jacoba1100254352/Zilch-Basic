package client;

import managers.GameCoordinator;

public class ZilchClient {
    public static void main(String[] args) {
        // Create an instance of the GameCoordinator class
        GameCoordinator gameCoordinator = new GameCoordinator();

        // Set up the game
        gameCoordinator.setupGame();

        // Start the game
        gameCoordinator.playGame();
    }
}
