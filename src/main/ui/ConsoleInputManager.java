package ui;


import model.managers.AbstractManager;

import java.util.Scanner;


public class ConsoleInputManager extends AbstractManager implements IInputManager
{
	private Scanner scanner;
	
	public ConsoleInputManager() {
		this.scanner = new Scanner(System.in);
	}
	
	@Override
	public String getInputString() {
		return scanner.nextLine();
	}
	
	@Override
	public int getInputInt() {
		while (!scanner.hasNextInt()) {
			System.out.println("Please enter a valid integer:");
			scanner.next(); // Consume the invalid input
		}
		return scanner.nextInt();
	}
	
	@Override
	public Runnable waitForEnterKey() {
		return () -> {
			System.out.print("\nPress enter to continue... ");
			scanner.nextLine(); // Assuming this follows a call to `getInputInt()` which leaves a newline character
		};
	}
	
	@Override
	protected void doInitialize() {
		this.scanner = new Scanner(System.in);
	}
}
