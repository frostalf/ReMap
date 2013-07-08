package com.kiwhen.remap;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CmdListener implements Listener {
	private Pattern wildcardRegex = Pattern.compile("^(%\\d+)$");
	private Main plugin;
	public CmdListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		
		String command = e.getMessage();
		
		// Disable command blocking (hard-coded safety feature to prevent ReMap from blocking itself)
		if(command.equalsIgnoreCase("/remap b") || command.equalsIgnoreCase("/remap block")) {
			if(p.hasPermission("remap") || p.isOp()) {
				if(plugin.isBlocking()) {
					plugin.toggleBlockCmds();
					p.sendMessage(ChatColor.GOLD + "ReMap will no longer block non-remapped commands.");
					e.setCancelled(true);
					return;
				}
			}
		}

		
		
		// Strip all forward slashes up front
		command = command.replaceAll("^/+", "");
		
		if(command.trim().equalsIgnoreCase("")) {
			// Empty command
			if(plugin.isBlocking()) {
				if(!plugin.getBlockMsg().trim().equalsIgnoreCase("")) {
					p.sendMessage(plugin.getBlockMsg());
				}
				e.setCancelled(true);
			}
			return;
		}
		
		boolean remapping = false;
		boolean replying = false;
		
		// Check for remaps
		MatchResult mr = matchCommand(command, Mode.REMAP);
		Set<String> commands = new LinkedHashSet<>();
		if(mr != null) {
			// Remapping
			remapping = true;
			
			// Execute remaps
			
			for(String remap : plugin.getMapper().getRemapSet(mr.getPattern())) {
				// Replace wildcards
				for(Entry<String, String> wildcard : mr.getWildcards().entrySet()) {
					remap = remap.replaceAll(wildcard.getKey(), wildcard.getValue());
				}
				
				// Replace asterisks
				remap = remap.replaceAll("\\*", mr.getAsterisk());
				
				commands.add(remap);
			}
		}
		
		// Check for replies
		mr = matchCommand(command, Mode.REPLY);
		String reply = "";
		if(mr != null) {
			// Replying
			replying = true;
			
			// Send reply
			reply = plugin.getMapper().getReplySet(mr.getPattern());
			
			// Replace wildcards
			for(Entry<String, String> wildcard : mr.getWildcards().entrySet()) {
				reply = reply.replaceAll(wildcard.getKey(), wildcard.getValue());
			}
			
			// Replace asterisks
			reply = reply.replaceAll("\\*", mr.getAsterisk());
		}
		
		if(!remapping && !replying) {
			// No mapping. Block command anyway?
			if(plugin.isBlocking()) {
				if(!plugin.getBlockMsg().trim().equalsIgnoreCase("")) {
					p.sendMessage(plugin.getBlockMsg());
				}
				e.setCancelled(true);
			}
		} else if(remapping && replying) {
			// Remapping AND replying. Execute commands silently (TODO, work in progress)
			for(String c : commands) {
				p.performCommand(c);
			}
			p.sendMessage(reply);
			e.setCancelled(true);
		} else if(remapping && !replying) {
			// Remapping only
			for(String c : commands) {
				p.performCommand(c);
			}
			e.setCancelled(true);
		} else if(!remapping && replying) {
			// Replying only
			p.sendMessage(reply);
			e.setCancelled(true);
		}
	}
	
	private MatchResult matchCommand(String command, Mode mode) {
		MatchResult result = new MatchResult();
		
		String[] commandWords = command.split(" ");
		String commandName = commandWords[0];
		
		// Get patterns for this command
		Set<String> patterns = null;
		switch(mode) {
		case REMAP:
			patterns = plugin.getMapper().getRemapPatterns(commandName);
			break;
		case REPLY:
			patterns = plugin.getMapper().getReplyPatterns(commandName);
			break;
		default:
			return null;
		}
		
		if(patterns == null) {
			// Command is not remapped
			return null;
		}
		
		for(String pattern : patterns) {
			result.setPattern(pattern);
			result.clearWildcards();
			
			String[] patternWords = pattern.split(" ");
			
			boolean match = true;
			for(int i = 0; i < patternWords.length; i++) {
				if(commandWords.length < patternWords.length) {
					// Command is too short for pattern to match
					match = false;
					break;
				}
				
				if(patternWords[i].equalsIgnoreCase("*")) {
					// Asterisk, save data and accept pattern as match
					String asterisk = "";
					for(int n = i; n < commandWords.length; n++) {
						asterisk += commandWords[n] + " ";
					}
					result.setAsterisk(asterisk.trim());
					break;
				}
				
				Matcher m = wildcardRegex.matcher(patternWords[i]);
				if(m.find()) {
					// Wildcard, save data
					result.setWildcard(m.group(1), commandWords[i]);
				} else if(!patternWords[i].equalsIgnoreCase(commandWords[i])) {
					// This word does not match pattern
					match = false;
					break;
				}
				
				// Are there more words left to match?
				if((commandWords.length - i - 1 > 0) && i >= patternWords.length - 1) {
					// Pattern is too short for message to match
					match = false;
					break;
				}
			}
			
			if(match) {
                            return result;
                        }
		}
		
		return null;
	}
}