package com.kiwhen.remap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class ChatListener {
	private Pattern wildcardRegex = Pattern.compile("^(%\\d+)$");
	private Main plugin;
	public ChatListener(Main plugin) {
		this.plugin = plugin;
	}
	
	public PacketAdapter get() {
		return new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.Server.CHAT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if(e.getPacketID() == Packets.Client.CHAT) {
					String msg = e.getPacket().getStrings().read(0);
					e.getPacket().getStrings().write(0, bleep(swap(msg)));
				}
			}
		};
	}
	
	private String swap(String message) {
		String[] messageWords = ChatColor.stripColor(message).split(" ");
		
		// Loop through all swaps
		Map<String, String> wildcards = new HashMap<String, String>();
		String asterisk = "";
		for(Entry<String, String> swap : plugin.getMapper().getSwaps().entrySet()) {
			String output = swap.getValue();
			wildcards.clear();
			
			String[] patternWords = swap.getKey().split(" ");
			boolean match = true;
			
			for(int i = 0; i < patternWords.length; i++) {
				if(messageWords.length < patternWords.length) {
					// Message is too short for pattern to match
					match = false;
					break;
				}
				
				if(patternWords[i].equalsIgnoreCase("*")) {
					// Asterisk, save data and accept as match
					for(int n = i; n < messageWords.length; n++) {
						asterisk += messageWords[n] + " ";
					}
					asterisk = asterisk.trim();
					break;
				}
				
				Matcher m = wildcardRegex.matcher(patternWords[i]);
				if(m.find()) {
					// Wildcard, save data
					wildcards.put(m.group(1), messageWords[i]);
				} else if(!patternWords[i].equalsIgnoreCase(messageWords[i])) {
					// Mismatch
					match = false;
					break;
				}
				
				// Are there more words left to match?
				if((messageWords.length - i - 1 > 0) && i >= patternWords.length - 1) {
					// Pattern is too short for message to match
					match = false;
					break;
				}
			}
			
			if(match) {
				// Replace wildcards
				for(Entry<String, String> wildcard : wildcards.entrySet()) {
					output = output.replaceAll(wildcard.getKey(), wildcard.getValue());
				}
				
				// Replace asterisks
				if(asterisk.length() > 0)
					output = output.replaceAll("\\*", asterisk);
				
				return output;
			}
		}
		return message;
	}
	
	private String bleep(String message) {
		for(Entry<String, String> bleep : plugin.getMapper().getBleeps().entrySet()) {
			message = message.replaceAll(bleep.getKey(), bleep.getValue());
		}
		return message;
	}
}