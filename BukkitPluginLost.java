package me.kan.lost;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class BukkitPluginLost extends JavaPlugin
{
	public static BukkitPluginLost plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final Main playerListener = new Main(this);
	Location[][] holes = null;
	World w;
	Location STARTROOM;
	static String mainDirectory = "plugins/LostWoods";
	public static File configFile = new File(mainDirectory + File.separator + "config.yml");
	public static FileConfiguration cfg;

    
	public void onDisable()
	{
		new Location(w, Main.goldX, Main.goldY, Main.goldZ).getBlock().setType(Material.AIR);
		PluginDescriptionFile pdfFile = getDescription();
		saveConfig();
		this.logger.info(pdfFile.getName() + " is now disabled.");
		
	}
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		w = this.getServer().getWorld("world");
		PluginDescriptionFile pdfFile = getDescription();
		holes = new Location[6][6];
		STARTROOM = new Location(w,-115.5,72,-296.5);
		pm.registerEvents(this.playerListener, this);
		
		
		for(int y = 0; y < 6; y++){
			for(int x = 0; x < 6; x++){
				holes[x][y] = new Location(w, -154 + (13*x), 80, -6374 - (13*y));
			}
		}
		
		placeGoldRandom();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			try {
				getDataFolder().mkdir();
				new File(getDataFolder(), "config.yml").createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
				this.logger.severe("[LOSTWOODS] Error making data file?! Please report this error!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		cfg = getConfig();
		if (cfg.getKeys(true).isEmpty()) {
			this.logger.info("[LOSTWOODS] Data file not found, creating.");
			saveConfig();
		}
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
	}
	private void placeGoldRandom() {
		Random r = new Random();
		int x = r.nextInt(6);
		int z = r.nextInt(6);
		int heightr = r.nextInt(9);
		int height = 80 + (heightr*7);
		Location l = holes[x][z];
		l.setY(height);
		l.getBlock().setType(Material.LOG);
		this.logger.info("[LOSTWOODS] The block is at " +x+" "+ z);
		Main.goldX = l.getBlockX();
		Main.goldY = l.getBlockY();
		Main.goldZ = l.getBlockZ();
	}
	
	
}