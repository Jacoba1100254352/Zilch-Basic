package models;


/**
 * @param value Used for options like MULTIPLE or SINGLE
 */
public record GameOption(GameOption.Type type, Integer value)
{
	public enum Type
	{
		STRAIT, SET, MULTIPLE, SINGLE
	}
}
