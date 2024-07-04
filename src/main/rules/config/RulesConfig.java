package rules.config;


import java.util.Set;


public class RulesConfig
{
	private final String gameId;
	private Integer addMultipleMin;
	private Integer multipleMin;
	private Set<Integer> singleValues;
	private Integer setMin;
	private Integer numStraitValues;
	
	public RulesConfig(String gameId) {
		this.gameId = gameId;
	}
	
	// Getters and Setters
	public String getGameId() {
		return gameId;
	}
	
	public Integer getAddMultipleMin() {
		return addMultipleMin;
	}
	
	public void setAddMultipleMin(Integer addMultipleMin) {
		this.addMultipleMin = addMultipleMin;
	}
	
	public Integer getMultipleMin() {
		return multipleMin;
	}
	
	public void setMultipleMin(Integer multipleMin) {
		this.multipleMin = multipleMin;
	}
	
	public Set<Integer> getSingleValues() {
		return singleValues;
	}
	
	public void setSingleValues(Set<Integer> singleValues) {
		this.singleValues = singleValues;
	}
	
	public Integer getSetMin() {
		return setMin;
	}
	
	public void setSetMin(Integer setMin) {
		this.setMin = setMin;
	}
	
	public Integer getNumStraitValues() {
		return numStraitValues;
	}
	
	public void setNumStraitValues(Integer numStraitValues) {
		this.numStraitValues = numStraitValues;
	}
}
