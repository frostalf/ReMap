package com.kiwhen.remap;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CmdListener implements Listener {
	private Main plugin;
	public CmdListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();

		if(e.getMessage().equalsIgnoreCase("/remap block")) {
			if(p.hasPermission("remap") || p.isOp()) {
				if(plugin.getBlockCmds()) {
					plugin.toggleBlockCmds();
					p.sendMessage(ChatColor.GOLD + "ReMap will no longer block non-remapped commands.");
					e.setCancelled(true);
					return;
				}
			}
		}

		String[] words = e.getMessage().split(" ");

		if(words.length == 0) {
			if(plugin.getBlockCmds()) {
				if(!plugin.getBlockMsg().trim().equalsIgnoreCase("")) {
					p.sendMessage(plugin.getBlockMsg());
				}
				e.setCancelled(true);
			}
			return;
		}

		String remap = getRemap(words[0]);
		if(remap == null) {
			if(plugin.getBlockCmds()) {
				if(!plugin.getBlockMsg().trim().equalsIgnoreCase("")) {
					p.sendMessage(plugin.getBlockMsg());
				}
				e.setCancelled(true);
			}
			return;
		}

		for(int i = 0; i < words.length; i++) {
			remap = remap.replaceFirst("%" + i, words[i]);
		}

		while(remap.substring(0, 1).equalsIgnoreCase("/"))
			remap = remap.substring(1, remap.length());

		p.performCommand(remap);
		e.setCancelled(true);
	}

	private String getRemap(String cmd) {
		for(Entry<String, String> e : plugin.getRemaps().entrySet()) {
			String[] args = e.getKey().split(" ");
			if(cmd.equalsIgnoreCase(args[0]) || cmd.equalsIgnoreCase("/" + args[0])) {
				return e.getValue();
			}
		}
		return null;
	}
}