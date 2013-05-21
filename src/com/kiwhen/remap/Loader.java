package com.kiwhen.remap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Loader {
	private Pattern[] regex = {
			Pattern.compile("^'{1}(.+)'{1}\\s*:\\s*'{1}(.+)'{1}$"),
			Pattern.compile("^\"{1}(.+)\"{1}\\s*:\\s*\"{1}(.+)\"{1}$"),
			Pattern.compile("^'{1}(.+)'{1}\\s*:\\s*([^:]+)$"),
			Pattern.compile("^\"{1}(.+)\"{1}\\s*:\\s*([^:]+)$"),
			Pattern.compile("^([^:]+)\\s*:\\s*'{1}(.+)'{1}$"),
			Pattern.compile("^([^:]+)\\s*:\\s*\"{1}(.+)\"{1}$"),
			Pattern.compile("^([^:]+)\\s*:\\s*([^:]+)$")
	};

	private Main plugin;
	public Loader(Main plugin) {
		this.plugin = plugin;
	}

	public void config() {
		plugin.reloadConfig();
		plugin.setBlockCmds(plugin.getConfig().getBoolean("blockCommands"));
		if(plugin.getConfig().getString("blockMessage") != null) {
			plugin.setBlockMsg(plugin.getConfig().getString("blockMessage"));
		}
	}
	
	public void remaps() {
		plugin.getRemaps().clear();
		plugin.getSwaps().clear();
		
		if(!plugin.getDataFolder().exists())
			return;

		for(String f : plugin.getDataFolder().list()) {
			if(f.equalsIgnoreCase("config.yml"))
				continue;
			
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(plugin.getDataFolder() + "/" + f);
			} catch (FileNotFoundException e) {
				continue;
			}
			
			DataInputStream in = new DataInputStream(fs);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			boolean remapMode = true;
			try {
				while ((line = br.readLine()) != null) {
					line = line.trim();

					if(line.length() < 3)
						continue;

					if(line.equalsIgnoreCase("remap:")) {
						remapMode = true;
						continue;
					} else if(line.equalsIgnoreCase("swap:")) {
						remapMode = false;
						continue;
					}

					boolean match = false;
					Matcher m = null;
					for(int i = 0; i < regex.length; i++) {
						m = regex[i].matcher(line);
						if(m.find()) {
							match = true;
							break;
						}
					}
					
					if(match) {
						if(remapMode) {
							plugin.getRemaps().put(m.group(1).trim(), m.group(2).trim());
							continue;
						} else {
							plugin.getSwaps().put(m.group(1).trim(), m.group(2).trim());
							continue;
						}
					} else {
						continue;
					}
				}
				br.close();
				in.close();
				fs.close();
			} catch (IOException e) {
				continue;
			}
		}
	}
}