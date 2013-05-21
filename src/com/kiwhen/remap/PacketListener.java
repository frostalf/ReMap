package com.kiwhen.remap;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener {
	private Main plugin;
	public PacketListener(Main plugin) {
		this.plugin = plugin;
	}
	
	public PacketAdapter get() {
		return new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.Server.CHAT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if(e.getPacketID() == Packets.Client.CHAT) {
					String msg = e.getPacket().getStrings().read(0);

					String send = getSwap(msg);
					e.getPacket().getStrings().write(0, send);
				}
			}
		};
	}
	
	private String getSwap(String original) {
		String[] originalWords = ChatColor.stripColor(original).split(" ");
		Pattern regex = Pattern.compile("^(%\\d+)$");
		for(Entry<String, String> e : plugin.getSwaps().entrySet()) {
			String[] swapWords = e.getKey().split(" ");
			String swapText = e.getValue();

			if(swapWords.length != originalWords.length)
				continue;

			boolean c = false;
			for(int i = 0; i < swapWords.length; i++) {
				Matcher match = regex.matcher(swapWords[i]);
				if(match.find()) {
					swapText = swapText.replaceAll(match.group(1), originalWords[i]);
				} else if(!swapWords[i].equalsIgnoreCase(originalWords[i])) {
					c = true;
					break;
				}
			}

			if(c)
				continue;

			swapText = swapText.replaceAll("\\\\n", "\n");
			
			return ChatColor.translateAlternateColorCodes('&', swapText);
		}

		return original;
	}
}