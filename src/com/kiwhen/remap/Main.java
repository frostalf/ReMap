package com.kiwhen.remap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class Main extends JavaPlugin implements Listener {
	private Map<String, String> remaps = new HashMap<String, String>();
	private Map<String, String> swap = new HashMap<String, String>();
	private ProtocolManager pm = null;
	private FileConfiguration yaml = null;
	private File yamlFile = null;

	public void onLoad() {
		pm = ProtocolLibrary.getProtocolManager();
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getRemaps();

		pm.addPacketListener(new PacketAdapter(this, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.Server.CHAT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if(e.getPacketID() == Packets.Client.CHAT) {
					String msg = e.getPacket().getStrings().read(0);

					String send = getSwap(msg);
					e.getPacket().getStrings().write(0, send);
				}
			}
		});

		getLogger().log(Level.INFO, remaps.size() + " remaps loaded and " + swap.size() + " sentences swapped.");
	}

	@EventHandler
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");

		String remap = getRemap(args[0]);
		if(remap != null) {
			for(int i = 0; i < args.length; i++) {
				remap = remap.replaceFirst("%" + i, args[i]);
			}

			while(remap.substring(0, 1).equalsIgnoreCase("/"))
				remap = remap.substring(1, remap.length());

			e.getPlayer().performCommand(remap);
			e.setCancelled(true);
		}
	}

	private void getRemaps() {
		for(String f : getDataFolder().list()) {
			if(f.length() <= 3)
				continue;
			if(!f.substring(f.length() - 3).equalsIgnoreCase("yml"))
				continue;

			yamlFile = new File(getDataFolder(), f);
			yaml = YamlConfiguration.loadConfiguration(yamlFile);

			ConfigurationSection cs = yaml.getConfigurationSection("remap");
			if(cs == null)
				continue;
			Map<String, Object> raw = cs.getValues(false);

			for(Entry<String, Object> e : raw.entrySet()) {
				String v = e.getValue().toString();
				remaps.put(e.getKey().replaceAll("%P", "."), v);
			}

			cs = yaml.getConfigurationSection("swap");
			if(cs == null)
				continue;
			raw = cs.getValues(false);

			for(Entry<String, Object> e : raw.entrySet()) {
				String v = e.getValue().toString();
				swap.put(e.getKey().replaceAll("%P", "."), v);
			}
		}
	}

	private String getRemap(String cmd) {
		for(Entry<String, String> e : remaps.entrySet()) {
			String[] args = e.getKey().split(" ");
			if(cmd.equalsIgnoreCase(args[0]) || cmd.equalsIgnoreCase("/" + args[0])) {
				return e.getValue();
			}
		}
		return null;
	}

	private String getSwap(String original) {
		String[] originalWords = ChatColor.stripColor(original).split(" ");
		Pattern regex = Pattern.compile("^(%\\d+)$");
		for(Entry<String, String> e : swap.entrySet()) {
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

			return ChatColor.translateAlternateColorCodes('&', swapText);
		}

		return original;
	}
}