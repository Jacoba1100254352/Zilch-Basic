package controllers.state;


import java.io.IOException;


public interface GameTurnState
{
	GamePhase handle(TurnContext turnContext) throws IOException;
}
