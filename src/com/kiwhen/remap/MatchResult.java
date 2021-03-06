package com.kiwhen.remap;

import java.util.HashMap;
import java.util.Map;

public class MatchResult {
	private String pattern = "";
	private Map<String, String> wildcards = new HashMap<String, String>();
	private String asterisk = "*";
	
	// Getters
	public String getPattern() {
		return this.pattern;
	}
	public Map<String, String> getWildcards() {
		return this.wildcards;
	}
	public String getAsterisk() {
		return this.asterisk;
	}
	
	// Setters
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public void setWildcard(String wildcard, String word) {
		this.wildcards.put(wildcard, word);
	}
	public void clearWildcards() {
		this.wildcards.clear();
	}
	public void setAsterisk(String asterisk) {
		this.asterisk = asterisk;
	}
}