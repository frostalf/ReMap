package com.kiwhen.remap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Mapper {
	private Map<String, Set<String>> remapPatterns = new LinkedHashMap<String, Set<String>>();
	private Map<String, Set<String>> remapSet = new LinkedHashMap<String, Set<String>>();
	private Map<String, String> swaps = new LinkedHashMap<String, String>();
	private Map<String, String> bleeps = new LinkedHashMap<String, String>();
	private Map<String, Set<String>> replyPatterns = new LinkedHashMap<String, Set<String>>();
	private Map<String, String> replySet = new LinkedHashMap<String, String>();
	
	// Getters
	
	// Remaps
	public Map<String, Set<String>> getAllRemapPatterns() {
		return this.remapPatterns;
	}
	public Set<String> getRemapPatterns(String commandname) {
		return this.remapPatterns.get(commandname);
	}
	public Set<String> getRemapSet(String pattern) {
		return this.remapSet.get(pattern);
	}
	public int getRemapCount() {
		int size = 0;
		for(Set<String> patterns : this.remapPatterns.values()) {
			size += patterns.size();
		}
		return size;
	}
	
	// Swaps
	public Map<String, String> getSwaps() {
		return this.swaps;
	}
	public String getSwap(String pattern) {
		return this.swaps.get(pattern);
	}
	public int getSwapCount() {
		return this.swaps.size();
	}
	
	// Bleeps
	public Map<String, String> getBleeps() {
		return this.bleeps;
	}
	public String getBleep(String word) {
		return this.bleeps.get(word);
	}
	public int getBleepCount() {
		return this.bleeps.size();
	}
	

	// Replies
	public Set<String> getReplyPatterns(String commandname) {
		return this.replyPatterns.get(commandname);
	}
	public String getReplySet(String pattern) {
		return this.replySet.get(pattern);
	}
	public int getReplyCount() {
		int size = 0;
		for(Set<String> patterns : this.replyPatterns.values()) {
			size += patterns.size();
		}
		return size;
	}
	
	
	// Setters
	public void addRemapPattern(String commandname, String pattern) {
		if(getRemapPatterns(commandname) == null) {
			// Command doesn't exist, create new set
			this.remapPatterns.put(commandname, new LinkedHashSet<String>());
		}
		getRemapPatterns(commandname).add(pattern);
	}
	public void addRemapCommand(String pattern, String remap) {
		if(getRemapSet(pattern) == null) {
			// Pattern doesn't exist, create new set
			this.remapSet.put(pattern, new LinkedHashSet<String>());
		}
		getRemapSet(pattern).add(remap);
	}
	
	public void addSwap(String pattern, String swap) {
		if(getSwap(pattern) == null) {
			this.swaps.put(pattern, swap);
		} else {
			String existing = getSwap(pattern);
			existing += "\n" + swap;
			this.swaps.put(pattern, existing);
		}
	}
	
	public void addBleep(String word, String bleep) {
		if(getBleep(word) == null) {
			this.bleeps.put(word, bleep);
		} else {
			String existing = getBleep(word);
			existing += "\n" + bleep;
			this.bleeps.put(word, existing);
		}
	}
	
	public void addReplyPattern(String commandname, String pattern) {
		if(getReplyPatterns(commandname) == null) {
			// Command doesn't exist, create new set
			this.replyPatterns.put(commandname, new LinkedHashSet<String>());
		}
		getReplyPatterns(commandname).add(pattern);
	}
	public void addReplyLine(String pattern, String line) {
		if(getReplySet(pattern) == null) {
			this.replySet.put(pattern, line);
		} else {
			String existing = getReplySet(pattern);
			existing += "\n" + line;
			this.replySet.put(pattern, existing);
		}
	}
	
	// Dump cache
	public void clearAll() {
		this.remapPatterns.clear();
		this.remapSet.clear();
		this.swaps.clear();
		this.bleeps.clear();
		this.replyPatterns.clear();
		this.replySet.clear();
	}
}