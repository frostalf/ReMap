package com.kiwhen.remap;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class Main extends JavaPlugin implements Listener {
	// Objects
	private ProtocolManager pm = null;
	private Loader loader = new Loader(this);
	private Mapper mapper = new Mapper();
	
	// Settings from config.yml
	private boolean blockCommands = false;
	private String blockMsg = "";

	// Initialize
	public void onEnable() {
		// Create data folder and config.yml if there isn't one
		saveDefaultConfig();
		
		// Start reading config and remap files
		getLoader().config();
		getLoader().readAll();

		// Load up ProtocolLib and add listener
		pm = ProtocolLibrary.getProtocolManager();
		pm.addPacketListener(new ChatListener(this).get());
		
		// Add command listeners
		getServer().getPluginManager().registerEvents(new CmdListener(this), this);
		getCommand("remap").setExecutor(new CmdExecutor(this));
		
		// Log status
		getLogger().log(Level.INFO, status());
	}
	
	// Current plugin status
	public String status() {
		String output = "";
		
		// Remaps
		int size = getMapper().getRemapCount();
		if(size == 1)
			output += "1 remapped command, ";
		else
			output += size + " remapped commands, ";
		
		// Swaps
		size = getMapper().getSwapCount();
		if(size == 1)
			output += "1 sentence swapped, ";
		else
			output += size + " sentences swapped, ";
		
		// Bleeps
		size = getMapper().getBleepCount();
		if(size == 1)
			output += "1 phrase bleeped and ";
		else
			output += size + " phrases bleeped and ";
		
		// Replies
		size = getMapper().getReplyCount();
		if(size == 1)
			output += "1 reply loaded. ";
		else
			output += size + " replies loaded. ";
		
		// Blocking
		if(isBlocking())
			output += "Blocking all non-remapped commands. ";
		else
			output += "Not blocking any commands. ";
		
		return output;
	}
	
	// Getters and setters
	public Loader getLoader() {
		return this.loader;
	}
	public Mapper getMapper() {
		return this.mapper;
	}
	public boolean isBlocking() {
		return this.blockCommands;
	}
	public boolean setBlocking(boolean block) {
		if(this.blockCommands != block) {
			this.blockCommands = block;
			return true; // Status changed
		}
		return false; // Status remains the same
	}
	public boolean toggleBlockCmds() {
		if(isBlocking())
			setBlocking(false);
		else
			setBlocking(true);
		
		getConfig().set("blockCommands", isBlocking());
		saveConfig();
		return isBlocking();
	}
	public String getBlockMsg() {
		return this.blockMsg;
	}
	public void setBlockMsg(String text) {
		this.blockMsg = ChatColor.translateAlternateColorCodes('&', text.replaceAll("\\\\n", "\n"));
		getConfig().set("blockMessage", this.blockMsg);
		saveConfig();
	}
}