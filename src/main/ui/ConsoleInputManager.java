package ui;


import java.util.Scanner;


/**
 * Console-backed input reader used by the text UI.
 */
public class ConsoleInputManager implements IInputManager
{
	private final Scanner scanner;
	
	/**
	 * Creates a new scanner that reads from standard input.
	 */
	public ConsoleInputManager() {
		this.scanner = new Scanner(System.in);
	}
	
	/** {@inheritDoc} */
	@Override
	public String getInputString() {
		return scanner.nextLine();
	}
	
	/** {@inheritDoc} */
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
	
	/** {@inheritDoc} */
	@Override
	public Runnable waitForEnterKey() {
		return () -> {
			System.out.print("\nPress enter to continue... ");
			scanner.nextLine(); // Assuming this follows a call to `getInputInt()` which leaves a newline character
		};
	}
}
