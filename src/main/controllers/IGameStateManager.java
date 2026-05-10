package controllers;


import controllers.state.GamePhase;
import controllers.state.TurnContext;

import java.io.IOException;


public interface IGameStateManager
{
	GamePhase getCurrentPhase();

	void processTurn(TurnContext turnContext) throws IOException;
}
