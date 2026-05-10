package controllers.state;


import model.entities.GameOption;
import model.managers.GameOptionManager;
import ui.IUserInteraction;


/**
 * Collects the player's choice from the currently available scoring options.
 */
public class SelectOptionState implements GameTurnState
{
	private final GameOptionManager gameOptionManager;
	private final IUserInteraction userInteraction;

	/**
	 * Creates the state that prompts the user to choose a scoring option.
	 */
	public SelectOptionState(GameOptionManager gameOptionManager, IUserInteraction userInteraction) {
		this.gameOptionManager = gameOptionManager;
		this.userInteraction = userInteraction;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		GameOption selectedOption = userInteraction.chooseGameOption(
				turnContext.getPlayer(),
				gameOptionManager.getGameOptions()
		);
		turnContext.setSelectedOption(selectedOption);
		gameOptionManager.setSelectedGameOption(selectedOption);
		return GamePhase.APPLY_OPTION;
	}
}
