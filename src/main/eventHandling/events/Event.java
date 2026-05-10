package eventHandling.events;


import java.util.EnumMap;
import java.util.Map;


/**
 * Generic event payload used by the simple in-process event dispatcher.
 */
public class Event
{
	private final GameEventType type;
	private final Map<EventDataKey, Object> data = new EnumMap<>(EventDataKey.class);
	
	/**
	 * Creates an event of the supplied type.
	 */
	public Event(GameEventType type) {
		this.type = type;
	}
	
	/**
	 * Stores a typed data entry on the event.
	 */
	public void setData(EventDataKey key, Object value) {
		data.put(key, value);
	}
	
	/**
	 * Retrieves a typed data entry from the event.
	 */
	public Object getData(EventDataKey key) {
		return data.get(key);
	}
	
	/**
	 * Returns the event's type identifier.
	 */
	public GameEventType getType() {
		return type;
	}
}
