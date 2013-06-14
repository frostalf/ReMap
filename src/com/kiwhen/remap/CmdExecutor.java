package com.kiwhen.remap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdExecutor implements CommandExecutor {
	private Main plugin;
	public CmdExecutor(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if(!sender.hasPermission("remap") && !sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to operate ReMap.");
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.GOLD + "ReMap commands:\n" +
					"/remap block: " + ChatColor.WHITE + "Toggle block of non-remapped commands\n" + ChatColor.GOLD +
					"/remap message: " + ChatColor.WHITE + "Set command block message (can be empty)\n" + ChatColor.GOLD +
					"/remap reload: " + ChatColor.WHITE + "Reload plugin data\n" + ChatColor.GOLD +
					"/remap status: " + ChatColor.WHITE + "Displays status information\n" + ChatColor.GOLD +
					"Tip: " + ChatColor.WHITE + "Short-type commands by using the first letter.\n" + "Example: " + ChatColor.GOLD + "/remap r " + ChatColor.WHITE + "instead of " + ChatColor.GOLD + "/remap reload" + ChatColor.WHITE + ".");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("reload")) {
			plugin.getLoader().config();
			plugin.getLoader().readAll();
			sender.sendMessage(ChatColor.GOLD + plugin.status());
			return true;
			
		} else if(args[0].equalsIgnoreCase("b") || args[0].equalsIgnoreCase("block")) {
			if(plugin.toggleBlockCmds())
				sender.sendMessage(ChatColor.GOLD + "ReMap will now block non-remapped commands.");
			else
				
				sender.sendMessage(ChatColor.GOLD + "ReMap will no longer block non-remapped commands.");
			return true;
			
		} else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("status")) {
			sender.sendMessage(ChatColor.GOLD + plugin.status());
			return true;
			
		} else if(args[0].equalsIgnoreCase("m") || args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("blockmessage") || args[0].equalsIgnoreCase("blockmsg")) {
			if(args.length == 1) {
				plugin.setBlockMsg("");
				sender.sendMessage(ChatColor.GOLD + "Block message is now disabled.");
			} else {
				String text = "";
				for(int i = 1; i < args.length; i++) {
					text += args[i] + " ";
				}
				plugin.setBlockMsg(text.trim());
				sender.sendMessage(ChatColor.GOLD + "Block message changed to: " + plugin.getBlockMsg());
			}
			return true;
		} else {
			return false;
		}
	}
}