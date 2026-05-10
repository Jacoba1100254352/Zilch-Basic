# Testing Outline for Zilch Game System

## Model Layer
- **DiceManager**: Test rolling, removing, replenishing of dice.
- **ScoreManager**: Test scoring logic for straights, sets, singles, and multiples.
- **PlayerManager**: Test player switching, finding highest scoring player, etc.
- **GameOptionManager**: Test the evaluation and application of game options.

## Controllers Layer
- **GameEngine**: Test the game turn process, including rolling dice and applying game options.
- **GameStateManager**: Test the state management during the game, such as handling busts and managing turn continuation.
- **GameServer**: Test the game flow, including starting and ending the game, and handling last turns.

## Event Handling Layer
- **SimpleEventDispatcher**: Test adding, removing listeners, and dispatching events.
- **GameOverListener**: Test the handling of game over scenarios and score updates.
- **RuleChangeListener**, **GameStateChangeListener**, **ScoreUpdateListener**: Test the specific events these listeners are supposed to handle.

## Rules Layer
- **RuleManager**: Test rule evaluation based on dice configurations.
- **AddMultipleRule**, **MultipleRule**, **SetRule**, **SingleRule**, **StraitRule**: Test the validity checks for each rule type.

## UI Layer
- **ConsoleGameplayUI**: Test the display methods, ensuring they show the correct output for various game states and inputs.
- **UserInteractionManager**: Test the integration of input handling and game setup functionalities.

## Utility and Pattern Implementations
- **GameBuilder**: Test the builder pattern implementation ensuring it correctly configures and creates a `GameServer`.
- **GameFactory**: Test the factory pattern implementation for creating configured game server instances.