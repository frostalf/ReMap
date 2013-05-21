package com.kiwhen.remap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class Main extends JavaPlugin implements Listener {
	private ProtocolManager pm = null;
	private Loader loader = null;

	private Map<String, String> remaps = new HashMap<String, String>();
	private Map<String, String> swaps = new LinkedHashMap<String, String>();
	
	private boolean blockCmds = false;
	private String blockMsg = "";

	public void onLoad() {
		pm = ProtocolLibrary.getProtocolManager();
		loader = new Loader(this);
	}

	public void onEnable() {
		saveDefaultConfig();
		
		loader.remaps();
		loader.config();

		getServer().getPluginManager().registerEvents(new CmdListener(this), this);
		pm.addPacketListener(new PacketListener(this).get());
		getCommand("remap").setExecutor(new CmdExecutor(this));
		
		getLogger().log(Level.INFO, status());
	}
	
	public String status() {
		String output = "";
		int size = remaps.size();
		if(size == 0) {
			output += "No remaps loaded and ";
		} else if(size == 1) {
			output += "1 remap loaded and ";
		} else {
			output += size + " remaps loaded and ";
		}
		size = swaps.size();
		if(size == 0)
			output += "no sentences swapped. ";
		else if(size == 1)
			output += "1 sentence swapped. ";
		else
			output += size + " sentences swapped. ";
		if(getBlockCmds())
			output += "Blocking non-remapped commands.";
		else
			output += "Not blocking non-remapped commands.";
		return output;
	}
	
	public Loader getLoader() {
		return loader;
	}
	public Map<String, String> getRemaps() {
		return remaps;
	}
	public Map<String, String> getSwaps() {
		return swaps;
	}
	public boolean getBlockCmds() {
		return blockCmds;
	}
	public void setBlockCmds(boolean block) {
		blockCmds = block;
	}
	public boolean toggleBlockCmds() {
		if(blockCmds)
			blockCmds = false;
		else
			blockCmds = true;
		getConfig().set("blockCommands", blockCmds);
		saveConfig();
		return blockCmds;
	}
	public String getBlockMsg() {
		return blockMsg;
	}
	public void setBlockMsg(String text) {
		blockMsg = text;
	}
}