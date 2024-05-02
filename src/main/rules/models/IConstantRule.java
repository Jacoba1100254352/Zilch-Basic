package rules.models;


public interface IConstantRule
{
	String getDescription();
	
	boolean isValid(Integer value1, Integer value2);
}
