package me.kan.lost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Main
implements Listener
{
	public static BukkitPluginLost plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	public Main(BukkitPluginLost instance) {
		plugin = instance;
	}	

	static int goldX;
	static int goldY;
	static int goldZ;
	Map<Player, Location> pMap = new HashMap<Player, Location>();

	@EventHandler
	public void WhenThePlayerWalksOverAPressurePlate(PlayerInteractEvent event) {
		if ((event.getAction().equals(Action.PHYSICAL)&& event.getClickedBlock().getType().equals(Material.STONE_PLATE))){
			Block underPlate = event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getX(), event.getClickedBlock().getY() - 4, event.getClickedBlock().getZ());
			if (inMaze(underPlate)){
				pMap.put(event.getPlayer(), this.getHolePressureplateIsIn(underPlate.getLocation()));
				Location l = getRoomToFromCoords(underPlate.getX(), underPlate.getZ());
				if(l != null){
					if(l.getBlockX() == goldX && l.getBlockZ()== goldZ){
						Set<Location> dne = new HashSet<Location>();
						for(Player p: pMap.keySet()){
							if (this.inMaze(p.getLocation().getBlock())){
								if(goldBlockIsNextTo(pMap.get(p))){
									Location use = pMap.get(p);
									use.setY(0);
									use.setPitch(0);
									use.setYaw(0);
									if(!dne.contains(use)){
										dne.add(use);
									}
								}
							}
						}
						Location[] places = new Location[4];
						places[0] = new Location(plugin.w, goldX+13, 0, goldZ,0,0);
						places[1] = new Location(plugin.w, goldX-13, 0, goldZ,0,0);
						places[2] = new Location(plugin.w, goldX, 0, goldZ+13,0,0);
						places[3] = new Location(plugin.w, goldX, 0, goldZ-13,0,0);
						for(int i = 0; i<4; i++){
							if((dne.contains(places[i]))||(!(this.inMaze(places[i].getBlock())))){
								this.logger.info("Cannot flee to " + places[i].getBlockX() + "," + places[i].getBlockZ());
								places[i]= null;
							}else{
								this.logger.info("Can flee to " + places[i].getBlockX() + "," + places[i].getBlockZ());

							}
						}
						boolean allcovered = true;
						for(int i = 0; i<4; i++){
							if(places[i]!= null){
								allcovered = false;
							}
						}
						if(allcovered){
							Location hhh = new Location(plugin.w, goldX, goldY, goldZ);
							hhh.getBlock().setType(Material.GOLD_BLOCK);
						}
						while(!allcovered){
							Random r = new Random();
							int choose = r.nextInt(4);
							if(places[choose] != null){
								this.placeGoldAtCoords(places[choose].getBlockX(), places[choose].getBlockZ());
								allcovered = true;
							}
						}
					}
				}
			}

		}
		if ((event.getClickedBlock().getType().equals(Material.GOLD_BLOCK))){
			Block underPlate = event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getX(), event.getClickedBlock().getY() - 4, event.getClickedBlock().getZ());
			if (inMaze(underPlate)){
				if(!(event.getPlayer().isOp())){
					BukkitPluginLost.cfg.addDefault(event.getPlayer().getUniqueId().toString(),true);
				}
				//Additional rewards go here!
			}
		}

	}
	private boolean goldBlockIsNextTo(Location l) {
		if ((Math.abs(l.getBlockX()- goldX)== 13)&&(Math.abs(l.getBlockZ()- goldZ)== 0)){
			return true;
		}else{
			if ((Math.abs(l.getBlockX()- goldX)== 0)&&(Math.abs(l.getBlockZ()- goldZ)== 13)){
				return true;
			}
		}
		return false;
	}
	@EventHandler
	public void ooops(PlayerQuitEvent e) {
		if (this.inMaze(e.getPlayer().getLocation().getBlock())){;
		this.pMap.remove(e.getPlayer());
		e.setQuitMessage(e.getPlayer().getName() + " went missing in the woods");
		}
	}

	@EventHandler
	public void loginss(PlayerJoinEvent e) {
		if (inMaze(e.getPlayer().getLocation().getBlock())){
			e.setJoinMessage(ChatColor.YELLOW +e.getPlayer().getName() + " emerged from the woods");
			e.getPlayer().teleport(plugin.STARTROOM);
		}
	}
	@EventHandler
	public void respawnevent (PlayerRespawnEvent e) {
		if (this.inMaze(e.getPlayer().getLocation().getBlock())){
			e.setRespawnLocation(plugin.STARTROOM);
		}
	}

	@EventHandler
	public void drown(PlayerDeathEvent e) {
		if (this.inMaze(e.getEntity().getLocation().getBlock())){
			this.pMap.remove(e.getEntity());
			e.setDeathMessage(e.getEntity().getName() + " went missing in the woods");
		}
	}
	@EventHandler
	public void onMobSacrifice(EntityDeathEvent e) {
		if(inLumberjack(e.getEntity().getLocation().getBlock())){
			if(e.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)){
				Location l = e.getEntity().getKiller().getLocation();
				if(!(BukkitPluginLost.cfg.contains(e.getEntity().getKiller().getUniqueId().toString()))){
					e.getEntity().getKiller().teleport(new Location(plugin.w, l.getX()-12,l.getY()+72,l.getZ()-6136,l.getYaw(), l.getPitch()));
					//e.getEntity().getKiller().playSound(Sound., arg1, 1, 0)
				}
			}
		}
	}

	private boolean inMaze(Block underPlate) {
		int X = underPlate.getX();
		int lowX = -159;
		int highX = -84;
		int Z = underPlate.getZ();
		int lowZ = -6444;
		int highZ = -6369;
		if(lowX<=X && X <=highX && lowZ<=Z && Z<=highZ && underPlate.getWorld().getName().equals("world")){
			return true;
		}else{
			return false;
		}
	}
	private boolean inLumberjack(Block underPlate) {
		int X = underPlate.getX();
		int lowX = -146;
		int highX = -73;
		int Y = underPlate.getY();
		int lowY = 66;
		int highY = 71;
		int Z = underPlate.getZ();
		int lowZ = -307;
		int highZ = -234;
		if(lowX<=X && X <=highX && lowZ<=Z && Z<=highZ && lowY<=Y && Y<=highY&& underPlate.getWorld().getName().equals("world")){
			return true;
		}else{
			return false;
		}
	}
	private Location getHolePressureplateIsIn(Location l){
		int x = l.getBlockX();
		int y = l.getBlockZ();
		x = x + 154;
		y = y + 6374;
		if(x%13 == 0){
			if((y+5)%13 == 0){
				y = y + 5;
			}else{
				y = y - 5;
			}
		}else{
			if((x+5)%13 == 0){
				x = x + 5;
			}else{
				x = x - 5;
			}
		}
		return Main.plugin.holes[x/13][y/-13];
	}
	public void placeGoldAtHole(int x, int z) {
		Random r = new Random();
		int heightr = r.nextInt(9);
		int height = 80 + (heightr*7);
		Location l = plugin.holes[x][z];
		l.setY(height);
		l.getBlock().setType(Material.LOG);
		Location oldgold = new Location(plugin.w, Main.goldX, Main.goldY, Main.goldZ);
		oldgold.getBlock().setType(Material.AIR);
		Main.goldX = l.getBlockX();
		Main.goldY = l.getBlockY();
		Main.goldZ = l.getBlockZ();
	}
	public void placeGoldAtCoords(int x, int z) {
		Random r = new Random();
		int heightr = r.nextInt(9);
		int height = 80 + (heightr*7);
		Location l = new Location(plugin.w,x, height, z);
		l.setY(height);
		l.getBlock().setType(Material.LOG);
		Location oldgold = new Location(plugin.w, Main.goldX, Main.goldY, Main.goldZ);
		oldgold.getBlock().setType(Material.AIR);
		Main.goldX = l.getBlockX();
		Main.goldY = l.getBlockY();
		Main.goldZ = l.getBlockZ();
	}
	private Location getRoomToFromCoords(int x, int y){
		x = x + 154;
		y = y + 6374;
		if(x%13 == 0){
			if((y+5)%13 == 0){
				y = y - 8;
			}else{
				y = y + 8;
			}
		}else{
			if((x+5)%13 == 0){
				x = x - 8;
			}else{
				x = x + 8;
			}
		}
		if(((x/13) == -1) ||((y/-13) == -1) || ((x/13) == 6) ||((y/-13) == 6)){
			return null;
		}
		return Main.plugin.holes[x/13][y/-13];
	}

}
