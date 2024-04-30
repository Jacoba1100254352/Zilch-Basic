package ui;


import java.util.Scanner;


public class ConsoleInputManager implements IInputManager
{
	private final Scanner scanner;
	
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
		int result = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character
		return result;
	}
	
	@Override
	public Runnable waitForEnterKey() {
		return () -> {
			System.out.print("\nPress enter to continue... ");
			scanner.nextLine(); // Assuming this follows a call to `getInputInt()` which leaves a newline character
		};
	}
}
