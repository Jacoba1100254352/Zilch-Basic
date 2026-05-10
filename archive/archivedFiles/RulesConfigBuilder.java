package rules.config;


import java.util.Set;


public class RulesConfigBuilder
{
	private final String gameId;
	private Integer addMultipleMin;
	private Integer multipleMin;
	private Set<Integer> singleValues;
	private Integer setMin;
	private Integer numStraitValues;
	
	public RulesConfigBuilder(String gameId) {
		this.gameId = gameId;
	}
	
	public RulesConfigBuilder setAddMultipleMin(Integer addMultipleMin) {
		this.addMultipleMin = addMultipleMin;
		return this;
	}
	
	public RulesConfigBuilder setMultipleMin(Integer multipleMin) {
		this.multipleMin = multipleMin;
		return this;
	}
	
	public RulesConfigBuilder setSingleValues(Set<Integer> singleValues) {
		this.singleValues = singleValues;
		return this;
	}
	
	public RulesConfigBuilder setSetMin(Integer setMin) {
		this.setMin = setMin;
		return this;
	}
	
	public RulesConfigBuilder setNumStraitValues(Integer numStraitValues) {
		this.numStraitValues = numStraitValues;
		return this;
	}
	
	public RulesConfig build() {
		RulesConfig config = new RulesConfig(gameId);
		config.setAddMultipleMin(addMultipleMin);
		config.setMultipleMin(multipleMin);
		config.setSingleValues(singleValues);
		config.setSetMin(setMin);
		config.setNumStraitValues(numStraitValues);
		return config;
	}
}
