package me.junglesociety.tntwars2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;


public class ArenaManager {
	
    TNTWars tnt = new TNTWars();
    SettingsManager settings = SettingsManager.getInstance();
    
    private Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
    private Map<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();
    private Map<String, Integer> xp = new HashMap<String, Integer>();
    private ArrayList<Arena> arenas = new ArrayList<Arena>();
    boolean iscounting = false;
    
    public static ArenaManager am = new ArenaManager();
    int lobbyx = tnt.getConfig().getInt("lobbyspawn." + "x");
    int lobbyy = tnt.getConfig().getInt("lobbyspawn." + "y");
    int lobbyz = tnt.getConfig().getInt("lobbyspawn." + "z");
    World lobbyworld = Bukkit.getServer().getWorld(tnt.getConfig().getString("lobbyspawn." + "world"));
    Location lobbyloc = new Location(lobbyworld,lobbyx,lobbyy,lobbyz);
    
    int arenaSize = 0;
    
    
	private ArenaManager() { }
	
	static ArenaManager instance = new ArenaManager();
	
	public static ArenaManager getInstance() {
		return instance;
	}
	

    
    public Arena getArena(int i){
        for(Arena a : arenas){
            if(a.getID() == i){
                return a;
            }
        }
        return null;
    }
    
    
    /*
     * ADD PLAYERS
     * 
     */
    
    public void addPlayer(Player player, int i){
    	final Arena a = getArena(i);
    	
    	if(a == null){
    		return;
    	}
    	
    	if(a.getBluePlayers().size() == 0 && a.getRedPlayers().size() == 0){
    		a.setInGame(false);
    	}
    	
    	if(a.isInGame() == true){
    		player.sendMessage(ChatColor.RED + "Sorry that arena is full!");
    	}else{
    		if(a.getPlayers().contains(player.getName())){
    			player.sendMessage(ChatColor.RED + "You are already in game!");
    		}else{
    			
    			if(iscounting = false){
    				
    				//Add them
        			a.getPlayers().add(player.getName());
        			
        			//If there is enough to start the game up
    				if(a.getPlayers().size() >= tnt.getConfig().getInt("minplayers") && a.getPlayers().size() <= tnt.getConfig().getInt("maxplayers")){
    					
    					//Tell all the players of the time
    					for(String allplayers : a.getPlayers()){
    						@SuppressWarnings("deprecation")
							Player p = Bukkit.getPlayer(allplayers);
    						p.sendMessage(ChatColor.YELLOW + "TNT Wars will start in " + tnt.getConfig().getInt("countdownbeforegame") + " seconds!");
    					}
    					
    					//What to do after that countdown is over
    			         BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    			 		scheduler.scheduleSyncDelayedTask((Plugin) this, new Runnable() {
    			 			@SuppressWarnings("deprecation")
							@Override
    			 			public void run() {
    			 				
    			 				//put them on teams
    			 				for(String list : a.getPlayers()){
								Player player = Bukkit.getPlayer(list);
    			 				if (a.getRedPlayers().size() > a.getBluePlayers().size()){
    			 		             a.getBluePlayers().add(player.getName());
    			 		         }else if (a.getRedPlayers().size() < a.getBluePlayers().size()){
    			 		             a.getRedPlayers().add(player.getName());
    			 		         }else {
    			 		             Integer team = new Random().nextInt(2);
    			 		             if (team == 1){
    			 		                 a.getRedPlayers().add(player.getName());
    			 		             }else {
    			 		                 a.getBluePlayers().add(player.getName());
    			 		             }
    			 		         }
    			 				
    			 				
    			 				 //teleport to blue spawn
    			 		         for(String blueteam : a.getBluePlayers()){
    			 		         Player bluePlayers = Bukkit.getServer().getPlayer(blueteam);
    			 		         bluePlayers.teleport(a.getBlueSpawn());
    			 		         bluePlayers.sendMessage(ChatColor.BLUE + "You are on the Blue team!");
    			 		         }
    			 		         
    			 		         
    			 		         //teleport to red spawn
    			 		         for(String redteam : a.getRedPlayers()){
    			 		         Player redPlayers = Bukkit.getPlayer(redteam);
    			 		         redPlayers.teleport(a.getRedSpawn());
    			 		         redPlayers.sendMessage(ChatColor.RED + "You are on the Red team!");
    			 		         }
    			 		         
    			 		         
    			 		         for(String allplayers : a.getPlayers()){
    			 		        	 Player all = Bukkit.getPlayer(allplayers);
    			 		        	 saveInv(all);
    			 		        	 all.getInventory().clear();
    			 		        	 if(tnt.getConfig().getBoolean("items_gui_enabled") == true){
    			 		        		 ItemStack item = new ItemStack(tnt.getConfig().getInt("item_for_gui_opener"), 1);
    			 		        		 all.getInventory().addItem(item);
    			 		        	 }
    			 		        	 
    			 		        	 all.setLevel(0);
    			 		        	 
    			 		        	 all.setGameMode(GameMode.SURVIVAL);
    			 		        	 all.setHealth(20);
    			 		        	 all.setFoodLevel(20);
    			 		         }
    			 				}
    			 				
    			 				
    			 		         //make it so no one can join
    			 			a.setInGame(true);
    			 			}
    			 		}, tnt.getConfig().getInt("countdownbeforegame") * 20);
    			 		
    			 		//If there isnt enough to start
    				}else{
    		        	 int needed = tnt.getConfig().getInt("minplayers") - a.getPlayers().size();
    		        	 
    		        	 for(String all: a.getPlayers()){
    		        		 @SuppressWarnings("deprecation")
							Player everyone = Bukkit.getPlayer(all);
    		        		 everyone.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the game! " + needed + " more players until the game starts!");
    		        	 }
    				}
    			}else{
        			a.getPlayers().add(player.getName());
    			}
    		}
    	}
    }
    
    
    /*
     * REMOVE PLAYERS
     * 
     */
    
    @SuppressWarnings("deprecation")
	public void removePlayer(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid arena!");
    	}else{
    		
    	   	 World world = player.getWorld();
        	 //check if they are already in game
             if(a.getPlayers().contains(player.getName())){
             a.getPlayers().remove(player.getName());
             //check if they are on blue team
             if(a.getBluePlayers().contains(player.getName())){
                 a.getBluePlayers().remove(player.getName());
                 player.getInventory().clear();
                 restoreInv(player);
             }
             //check if they are on red team
             if(a.getRedPlayers().contains(player.getName())){
                 a.getRedPlayers().remove(player.getName());
                 player.getInventory().clear();
                 restoreInv(player);
             }
             
             
             //teleport to lobby spawn
             player.teleport(lobbyloc);
             player.sendMessage(ChatColor.YELLOW + "You left TnT Wars!");
             for(String id : a.getPlayers()){
            	 Player p = Bukkit.getServer().getPlayer(id);
            	 p.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + " has left the game!");
             }
             
             
             //Check for the last team remaining
             if((a.getRedPlayers().size() == 0 && a.getBluePlayers().size() >= 1) || (a.getBluePlayers().size() == 0 && a.getRedPlayers().size() >= 1)){
            	 loadRegion(world, i);
            	 for(String spectator : a.getSpectators()){
            		 Player p = Bukkit.getPlayer(spectator);
            		 unSpectate(player, i);
            	 }
                 
            	 for(String last : a.getPlayers()){
            		 Player lastplayer = Bukkit.getPlayer(last);
            		 lastplayer.sendMessage(ChatColor.YELLOW + "Congratulations!  Your team won!");
            		 lastplayer.teleport(lobbyloc);
            		 lastplayer.getInventory().clear();
            		 restoreInv(lastplayer);
            		 //remove them
            		 if(a.getRedPlayers().contains(last)){
            			 a.getRedPlayers().remove(last);
            		 }
            		 if(a.getBluePlayers().contains(last)){
            			 a.getBluePlayers().remove(last);
            		 }
            		 a.getPlayers().remove(last);
            	 }
             } 
             //if they aren't already in tntwars
             }else{
                 player.sendMessage(ChatColor.RED + "You are not in TnT Wars!");
             }
    	}
    }
    
    
    /*
     * 
     * CREATE ARENA
     * 
     */
    
    public Arena createArena(Player player){
    	if(settings.getData().getIntegerList("ArenaNumbers").isEmpty() || settings.getData().getIntegerList("ArenaNumbers") == null){
    		int num = 1;
    		settings.getData().getIntegerList("ArenaNumbers").add(num);
    		
    		settings.getData().createSection("Arenas." + num + ".bluespawn.x");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.y");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.z");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".redspawn.x");
    		settings.getData().createSection("Arenas." + num + ".redspawn.y");
    		settings.getData().createSection("Arenas." + num + ".redspawn.z");
    		settings.getData().createSection("Arenas." + num + ".redspawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".specspawn.x");
    		settings.getData().createSection("Arenas." + num + ".specspawn.y");
    		settings.getData().createSection("Arenas." + num + ".specspawn.z");
    		settings.getData().createSection("Arenas." + num + ".specspawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".minloc.x");
    		settings.getData().createSection("Arenas." + num + ".minloc.y");
    		settings.getData().createSection("Arenas." + num + ".minloc.z");
    		settings.getData().createSection("Arenas." + num + ".minloc.world");
    		
    		settings.getData().createSection("Arenas." + num + ".maxloc.x");
    		settings.getData().createSection("Arenas." + num + ".maxloc.y");
    		settings.getData().createSection("Arenas." + num + ".maxloc.z");
    		settings.getData().createSection("Arenas." + num + ".maxloc.world");
    		
    		settings.saveData();
    		
    		Arena a = new Arena(num);
    		arenas.add(a);
    		
    		return a;
    		
    	}else{
    		List<Integer> ints = settings.getData().getIntegerList("ArenaNumbers");
    		
    		int num = 0;
    		int n = 0;
    		for(int i : ints){
    			n++;
    			if(i != n){
    				num = n;
    				break;
    			}
    		}
    		
    		settings.getData().getIntegerList("ArenaNumbers").add(num);
    		
    		settings.getData().createSection("Arenas." + num + ".bluespawn.x");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.y");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.z");
    		settings.getData().createSection("Arenas." + num + ".bluespawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".redspawn.x");
    		settings.getData().createSection("Arenas." + num + ".redspawn.y");
    		settings.getData().createSection("Arenas." + num + ".redspawn.z");
    		settings.getData().createSection("Arenas." + num + ".redspawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".specspawn.x");
    		settings.getData().createSection("Arenas." + num + ".specspawn.y");
    		settings.getData().createSection("Arenas." + num + ".specspawn.z");
    		settings.getData().createSection("Arenas." + num + ".specspawn.world");
    		
    		settings.getData().createSection("Arenas." + num + ".minloc.x");
    		settings.getData().createSection("Arenas." + num + ".minloc.y");
    		settings.getData().createSection("Arenas." + num + ".minloc.z");
    		settings.getData().createSection("Arenas." + num + ".minloc.world");
    		
    		settings.getData().createSection("Arenas." + num + ".maxloc.x");
    		settings.getData().createSection("Arenas." + num + ".maxloc.y");
    		settings.getData().createSection("Arenas." + num + ".maxloc.z");
    		settings.getData().createSection("Arenas." + num + ".maxloc.world");
    		
    		settings.saveData();
    		
    		Arena a = new Arena(num);
    		arenas.add(a);
    		
    		return a;
    		
    	}
    }
    
    
    /*
     * 
     * DELETE ARENA
     * 
     */
    
    public void deleteArena(int i, Player player){
    	
    	if(getArena(i) == null){
    		player.sendMessage(ChatColor.RED + "Invalid arena!");
    		return;
    	}
    	
		settings.getData().getIntegerList("ArenaNumbers").remove(i);
		settings.getData().getConfigurationSection("Arenas").set(String.valueOf(i), null);
		settings.saveData();
    }
    
    /*
     * 
     * SAVE LOCATIONS
     * 
     */
    
    public void setBlueSpawn(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		return;
    	}else{
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		settings.getData().set("Arenas." +  i + "bluespawn.x", x);
    		settings.getData().set("Arenas." +  i + "bluespawn.y", y);
    		settings.getData().set("Arenas." +  i + "bluespawn.z", z);
    		settings.getData().set("Arenas." +  i + "bluespawn.world", world);
    	}
    }
    
    
    public void setRedSpawn(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		return;
    	}else{
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		settings.getData().set("Arenas." +  i + "redspawn.x", x);
    		settings.getData().set("Arenas." +  i + "redspawn.y", y);
    		settings.getData().set("Arenas." +  i + "redspawn.z", z);
    		settings.getData().set("Arenas." +  i + "redspawn.world", world);
    	}
    }
    
    
    public void setSpectatorSpawn(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		return;
    	}else{
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		settings.getData().set("Arenas." +  i + "specspawn.x", x);
    		settings.getData().set("Arenas." +  i + "specspawn.y", y);
    		settings.getData().set("Arenas." +  i + "specspawn.z", z);
    		settings.getData().set("Arenas." +  i + "specspawn.world", world);
    	}
    }
    
    
    public void setMinLoc(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		return;
    	}else{
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		settings.getData().set("Arenas." +  i + "minloc.x", x);
    		settings.getData().set("Arenas." +  i + "minloc.y", y);
    		settings.getData().set("Arenas." +  i + "minloc.z", z);
    		settings.getData().set("Arenas." +  i + "minloc.world", world);
    	}
    }


    public void setMaxLoc(Player player, int i){
    	Arena a = getArena(i);
    	
    	if(a == null){
    		player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		return;
    	}else{
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		settings.getData().set("Arenas." +  i + "maxloc.x", x);
    		settings.getData().set("Arenas." +  i + "maxloc.y", y);
    		settings.getData().set("Arenas." +  i + "maxloc.z", z);
    		settings.getData().set("Arenas." +  i + "maxloc.world", world);
    	}
    }
    
    
    public void setLobbySpawn(Player player){
    		double x = player.getLocation().getX();
    		double y = player.getLocation().getY();
    		double z = player.getLocation().getZ();
    		String world = player.getWorld().toString();
    		
    		tnt.getConfig().set("lobbyspawn.x", x);
    		tnt.getConfig().set("lobbyspawn.y", y);
    		tnt.getConfig().set("lobbyspawn.z", z);
    		tnt.getConfig().set("lobbyspawn.world", world);
    	
    }
    
    
    public void saveInv(Player p){
   	 inv.put(p.getName(), p.getInventory().getContents());
   	 armor.put(p.getName(), p.getInventory().getArmorContents());
   	 xp.put(p.getName(), p.getExpToLevel());
   	 }
    
        //restore player inventory
   	 public void restoreInv(Player p){
   	 p.getInventory().setContents(inv.get(p.getName()));
   	 p.getInventory().setArmorContents(armor.get(p.getName()));
   	 p.setLevel(0);
   	 p.setLevel(xp.get(p.getName()));
   	 inv.remove(p.getName());
   	 armor.remove(p.getName());
   	 xp.remove(p.getName());
   	 } 
   	 
     
     public void saveRegion(Location l1, Location l2, Player player, int i){
    	 
    	 Arena a = getArena(i);
    	 if(a == null){
    		 player.sendMessage(ChatColor.RED + "Invalid Arena!");
    		 return;
    	 }
    	 
    			 // ensure WorldEdit is available
    			 WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    			 if (wep == null) {
    			   // then don't try to use TerrainManager!
    			 }
    			 
    			 Plugin plugin = (Plugin) this;
    			 // OR - without needing an associated Player
    			 TerrainManager tm = new TerrainManager(wep, player.getWorld());
    			  
    			 // don't include an extension - TerrainManager will auto-add ".schematic"
    			 File saveFile = new File(plugin.getDataFolder(), "Arena" + i);
    			  
    			 // save the terrain to a schematic file
    			 try {
    			   tm.saveTerrain(saveFile, l1, l2);
    			 } catch (FilenameException e) {
    			   // thrown by WorldEdit - it doesn't like the file name/location etc.
    			 } catch (DataException e) {
    			   // thrown by WorldEdit - problem with the data
    			 } catch (IOException e) {
    			   // problem with creating/writing to the file
    			 }
     }
     
     public void loadRegion(World world, int i){
    	 Arena a = getArena(i);
    	 if(a == null){
    		 return;
    	 }
    	 
    	 Plugin p = (Plugin) this;
		 WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		 if (wep == null) {
		   // then don't try to use TerrainManager!
		 }
		 
		 TerrainManager tm = new TerrainManager(wep, world);
		 File saveFile = new File(p.getDataFolder(), "Arena" + i);
    	 
    try{
    	  tm.loadSchematic(saveFile);
     } catch (FilenameException e) {
       // thrown by WorldEdit - it doesn't like the file name/location etc.
     } catch (DataException e) {
       // thrown by WorldEdit - problem with the data
     } catch (IOException e) {
       // problem with opening/reading the file
     } catch (MaxChangedBlocksException e) {
       // thrown by WorldEdit - the schematic is larger than the configured block limit for the player
     } catch (EmptyClipboardException e) {
     // thrown by WorldEdit - should be self-explanatory
     }
     }
     
     @SuppressWarnings("deprecation")
	public void spectate(Player p, int i){
    	 Arena a = getArena(i);
    	 
    	 if(a == null){
    		 p.sendMessage(ChatColor.RED + "Invalid Arena!");
    		 return;
    	 }
    	 
         p.teleport(a.getSpectateSpawn());
    	 a.getSpectators().add(p.getName());
         p.setAllowFlight(true);
         p.setFlying(true);
         p.setHealth(20);
         p.setFoodLevel(20);
         p.sendMessage(ChatColor.GREEN + "You are now spectating TNT Wars!");
         
         for(String play : a.getPlayers()){
        	 Player player = Bukkit.getPlayer(play);
        	 player.hidePlayer(p);
         }
     }
     
     @SuppressWarnings("deprecation")
	public void unSpectate(Player p, int i){
    	 Arena a = getArena(i);
    	 if(a == null){
    		 p.sendMessage(ChatColor.RED + "Could not leave spectate mode!");
    		 return;
    	 }
    	 
         p.teleport(lobbyloc);
         p.setGameMode(GameMode.SURVIVAL);
         p.setAllowFlight(false);
         p.setFlying(false);
         p.setHealth(20);
         p.sendMessage(ChatColor.GREEN + "You are no longer spectating TNT Wars!");
    	 a.getSpectators().remove(p.getUniqueId());
         
         for(Player player : Bukkit.getServer().getOnlinePlayers()){
        	 player.showPlayer(p);
         }
     }
     
     
     //Get arena list
     public ArrayList<Arena> getArenaList(){
    	 return arenas;
     }

}
