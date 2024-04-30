package eventHandling.events;


import java.util.EnumMap;
import java.util.Map;


public class Event
{
	private final GameEventType type;
	private final Map<EventDataKey, Object> data = new EnumMap<>(EventDataKey.class);
	
	public Event(GameEventType type) {
		this.type = type;
	}
	
	public void setData(EventDataKey key, Object value) {
		data.put(key, value);
	}
	
	public Object getData(EventDataKey key) {
		return data.get(key);
	}
	
	public GameEventType getType() {
		return type;
	}
}
