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
					"/remap reload: " + ChatColor.WHITE + "Reload plugin data\n");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("reload")) {
			plugin.getLoader().remaps();
			sender.sendMessage(ChatColor.GOLD + plugin.status());
			return true;
		} else if(args[0].equalsIgnoreCase("block")) {
			if(plugin.toggleBlockCmds())
				sender.sendMessage(ChatColor.GOLD + "ReMap will now block non-remapped commands.");
			else
				sender.sendMessage(ChatColor.GOLD + "ReMap will no longer block non-remapped commands.");
			return true;
		} else {
			return false;
		}
	}
}