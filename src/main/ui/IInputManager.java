package ui;


public interface IInputManager
{
	String getInputString();
	
	int getInputInt();
	
	Runnable waitForEnterKey();
}
