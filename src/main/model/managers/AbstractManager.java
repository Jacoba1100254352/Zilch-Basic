package model.managers;


/**
 * Abstract base class for all managers to provide common functionalities.
 */
public abstract class AbstractManager
{
	private boolean wasInitialized = false;
	
	// This is the "template method". It's final to prevent subclasses from overriding it.
	public final void initialize() {
		// Call the superclass's method
		baseInitialize();
		
		// Call the subclass's method
		doInitialize();
	}
	
	public boolean wasInitialized() {
		return wasInitialized;
	}
	
	// This method contains the code that was previously in initialize()
	private void baseInitialize() {
		wasInitialized = true;
	}
	
	// Subclasses will override this method to provide their own initialization logic
	protected abstract void doInitialize();
}

