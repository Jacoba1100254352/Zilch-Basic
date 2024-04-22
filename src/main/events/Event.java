package events;


import java.util.HashMap;
import java.util.Map;


public class Event
{
	private final String type;
	private final Map<String, Object> data;
	
	public Event(String type) {
		this.type = type;
		this.data = new HashMap<>();
	}
	
	public String getType() {
		return type;
	}
	
	public void setData(String key, Object value) {
		data.put(key, value);
	}
	
	public Object getData(String key) {
		return data.get(key);
	}
}
