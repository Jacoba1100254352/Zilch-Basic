package eventHandling.events;


import java.util.EnumMap;
import java.util.Map;


public class Event
{
	private final GameEventType type;
	private final Map<GameEventType, Object> data;
	
	public Event(GameEventType type) {
		this.type = type;
		this.data = new EnumMap<>(GameEventType.class);
	}
	
	public GameEventType getType() {
		return type;
	}
	
	public void setData(GameEventType key, Object value) {
		data.put(key, value);
	}
	
	public Object getData(GameEventType key) {
		return data.get(key);
	}
}
