package com.kiwhen.remap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class Loader {
	// Patterns for locating data in remap files
	private Pattern[] regex = {
			Pattern.compile("^([^:]+)\\s*:\\s*([^:]+)$"),
			Pattern.compile("^'{1}(.+)'{1}\\s*:\\s*'{1}(.+)'{1}$"),
			Pattern.compile("^\"{1}(.+)\"{1}\\s*:\\s*\"{1}(.+)\"{1}$"),
			Pattern.compile("^'{1}(.+)'{1}\\s*:\\s*([^:]+)$"),
			Pattern.compile("^\"{1}(.+)\"{1}\\s*:\\s*([^:]+)$"),
			Pattern.compile("^([^:]+)\\s*:\\s*'{1}(.+)'{1}$"),
			Pattern.compile("^([^:]+)\\s*:\\s*\"{1}(.+)\"{1}$")
	};

	// Memory field for interpreter
	private String lastPattern = "";
	
	private Main plugin;
	public Loader(Main plugin) {
		this.plugin = plugin;
	}

	// Config method
	public void config() {
		plugin.reloadConfig();
		plugin.setBlocking(plugin.getConfig().getBoolean("blockCommands"));
		if(plugin.getConfig().getString("blockMessage") != null) {
			plugin.setBlockMsg(plugin.getConfig().getString("blockMessage"));
		}
	}
	
	// Remap file loader
	public void readAll() {
		// Dump all cached data
		plugin.getMapper().clearAll();
		
		// Select default mode
		Mode mode = Mode.REMAP;
		
		// Check if data folder exists
		if(!plugin.getDataFolder().exists())
			return;

		// Iterate all files in data folder 
		for(String f : plugin.getDataFolder().list()) {
			if(f.equalsIgnoreCase("config.yml"))
				continue; // Don't read own config file
			
			// Open file
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(plugin.getDataFolder() + "/" + f);
			} catch (FileNotFoundException e) {
				continue;
			}
			DataInputStream in = new DataInputStream(fs);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			
			try {
				// Read line-by-line
				while ((line = br.readLine()) != null) {
					line = line.trim(); // Trim whitespace

					// Check for mode switches (JRE 1.6 compliant)
					if(line.equalsIgnoreCase("remap:")) {
						mode = Mode.REMAP;
						continue; // End of line
					} else if(line.equalsIgnoreCase("swap:")) {
						mode = Mode.SWAP;
						continue; // End of line
					} else if(line.equalsIgnoreCase("bleep:")) {
						mode = Mode.BLEEP;
						continue; // End of line
					} else if(line.equalsIgnoreCase("reply:")) {
						mode = Mode.REPLY;
						continue; // End of line
					}

					// Start matching arguments
					Matcher m = null;
					for(Pattern r : regex) {
						m = r.matcher(line);
						if(m.find()) {
							// Strip all forward slashes up front
							String a = m.group(1).trim();
							String b = m.group(2).trim();
							
							while(a.substring(0, 1).equalsIgnoreCase("/"))
								a = a.substring(1, a.length());
							
							while(b.substring(0, 1).equalsIgnoreCase("/"))
								b = b.substring(1, b.length());
							
							interpret(mode, a, b);
							break;
						}
					}
				}
				// Close resources
				br.close();
				in.close();
				fs.close();
			} catch (IOException e) {
				continue;
			}
		}
	}
	
	// Remap file interpreter
	public void interpret(Mode m, String a, String b) {
		// Add to pattern?
		if(a.equalsIgnoreCase("^")) {
			if(this.lastPattern.trim().equalsIgnoreCase("")) {
				// Invalid pattern
				return;
			}
			a = this.lastPattern;
		} else {
			this.lastPattern = a;
		}
		
		switch(m) {
		case REMAP:
			// Get command name
			String[] c = a.split(" ");
			plugin.getMapper().addRemapPattern(c[0].toLowerCase(), a);
			plugin.getMapper().addRemapCommand(a, b);
			break;
		case SWAP:
			plugin.getMapper().addSwap(a, ChatColor.translateAlternateColorCodes('&', b.replaceAll("\\\\n", "\n")));
			break;
		case BLEEP:
			plugin.getMapper().addBleep(a, ChatColor.translateAlternateColorCodes('&', b.replaceAll("\\\\n", "\n")));
			break;
		case REPLY:
			String[] d = a.split(" ");
			plugin.getMapper().addReplyPattern(d[0].toLowerCase(), a);
			plugin.getMapper().addReplyLine(a, ChatColor.translateAlternateColorCodes('&', b.replaceAll("\\\\n", "\n")));
			break;
		}
	}
}