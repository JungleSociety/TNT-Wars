package me.junglesociety.tntwars2;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class Events {
	
	
	
	/*
	
    @EventHandler
    public void onChestClick(PlayerInteractEvent e){
   	 if(tnt.getConfig().getBoolean("items_gui_enabled") == true){
   	 Player player = e.getPlayer();
   	 if(players.contains(player.getUniqueId())){
   	 if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
   		 if(player.getItemInHand().getType().equals(Material.getMaterial(getConfig().getInt("item_for_gui_opener")))){
   			 openGUI(player);
   		 }
   	 }
   	 }
     }
    }
    
    @EventHandler
    public void chestInventoryClick(InventoryClickEvent e){
   	 if(getConfig().getBoolean("items_gui_enabled") == true){
   	 Player player = (Player) e.getWhoClicked();
   	 if(players.contains(player.getUniqueId())){
   	 if(e.getView().getTopInventory().getTitle().equals("TNT Wars")){
   		 e.setCancelled(true);
   		 player.getInventory().addItem(e.getCurrentItem());
   	 }
   	 
   	 if(e.getCurrentItem().getType().equals(Material.getMaterial(getConfig().getInt("item_for_gui_opener")))){
   		 e.setCancelled(true);
   	 }
   	}
   	 
     }
   }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
   	 Player player = e.getPlayer();
   	 if(spectators.contains(player.getUniqueId())){
   		 e.setCancelled(true);
   	 }
   	 
   	 if(getConfig().getBoolean("items_gui_enabled") == true){
   	 if(players.contains(player.getUniqueId())){
   		 if(e.getBlock().getType().equals(Material.getMaterial(getConfig().getInt("item_for_gui_opener")))){
   			 e.setCancelled(true);
   		 }
   	 }
   	 }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
   	 Player player = e.getPlayer();
   	 if(spectators.contains(player.getUniqueId())){
   		 e.setCancelled(true);
   	 }
    }
    
    @EventHandler
    public void playerHitEvent(EntityDamageByEntityEvent e){
   	 if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
   		 Player damager = (Player) e.getDamager();
   		 Player player = (Player) e.getEntity();
   		 if(spectators.contains(player.getUniqueId()) || spectators.contains(damager.getUniqueId())){
   			 e.setCancelled(true);
   		 }
   	 }
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent e){
   	 if(spectators.contains(e.getPlayer().getUniqueId())){
   		 e.setCancelled(true);
   	 }
    }
    
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e){
   	 Player player = e.getPlayer();
   	 if(spectators.contains(player.getUniqueId())){
   		 e.setCancelled(true);
   	 }
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e){
   	 Player player = e.getPlayer();
   	 if(spectators.contains(player.getUniqueId())){
   		 e.setCancelled(true);
   	 }
   	 if(getConfig().getBoolean("items_gui_enabled") == true){
   	 if(players.contains(player.getUniqueId())){
   		 if(e.getItemDrop().getItemStack().equals(new ItemStack(Material.getMaterial(getConfig().getInt("item_for_gui_opener"))))){
   			 e.setCancelled(true);
   		 }
   	 }
   	 }
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
   	 Player player = e.getPlayer();
   	 if(spectators.contains(player.getUniqueId())){
   		 e.setCancelled(true);
   		 player.sendMessage(ChatColor.RED + "You may not teleport while spectating!");
   	 }
    }
    
    //When they die
    @EventHandler
    public void onDeath(PlayerRespawnEvent e){
        final Player player = e.getPlayer();
   	 World world = player.getWorld();
        //Make sure they are in tnt wars
        if(players.contains(player.getUniqueId())){
       	 //get some locations
            int x = getConfig().getInt("lobbyspawn." + "x");
            int y = getConfig().getInt("lobbyspawn." + "y");
            int z = getConfig().getInt("lobbyspawn." + "z");
            World lobbyworld = getServer().getWorld(getConfig().getString("redspawn." + "world"));
            final Location lobbyloc = new Location(lobbyworld,x,y,z);
            
            
            //teleport them
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
		             player.teleport(lobbyloc);
				}
			}, 20L);
			
			//restore inventory
           player.getInventory().clear();
           restoreInv(player);
			
            players.remove(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Sorry you were eliminated!");
            //remove from red team
            if(redTeam.contains(player.getUniqueId())){
           	 redTeam.remove(player.getUniqueId());
            }
            //remove from blue team
            if(blueTeam.contains(player.getUniqueId())){
           	 blueTeam.remove(player.getUniqueId());
            }
            //if there is only one person left, make them the victor
            if((redTeam.size() == 0 && blueTeam.size() >= 1) || (blueTeam.size() == 0 && redTeam.size() >= 1)){
           	 
           	 loadRegion(world);
           	 
           	 for(UUID spectator : spectators){
           		 Player p = Bukkit.getPlayer(spectator);
           		 unSpectate(p);
           	 }
           	 
           	 for(UUID last : players){
           		 Player lastplayer = Bukkit.getPlayer(last);
           		 lastplayer.sendMessage(ChatColor.YELLOW + "Congratulations!  Your team won!");
           		 lastplayer.teleport(lobbyloc);
           		 lastplayer.getInventory().clear();
           		 restoreInv(lastplayer);
           		 //remove them
           		 if(redTeam.contains(last)){
           			 redTeam.remove(last);
           		 }
           		 if(blueTeam.contains(last)){
           			 blueTeam.remove(last);
           		 }
           		 players.remove(last);
           	 }
        }
    }
}
    
    //When they quit
    @EventHandler
    public void onRelog(PlayerJoinEvent e){
        final Player player = e.getPlayer();
   	 World world = player.getWorld();
   	 
   	 if(spectators.contains(player.getUniqueId())){
   		 unSpectate(player);
   	 }
        //Make sure they are in tnt wars
        if(players.contains(player.getUniqueId())){
       	 //get some locations
            int x = getConfig().getInt("lobbyspawn." + "x");
            int y = getConfig().getInt("lobbyspawn." + "y");
            int z = getConfig().getInt("lobbyspawn." + "z");
            World lobbyworld = getServer().getWorld(getConfig().getString("redspawn." + "world"));
            final Location lobbyloc = new Location(lobbyworld,x,y,z);
            
            //teleport them
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
		             player.teleport(lobbyloc);
				}
			}, 20L);
			
			//restore inventory
           player.getInventory().clear();
           restoreInv(player);
			
            players.remove(player.getUniqueId());
            //remove from red team
            if(redTeam.contains(player.getUniqueId())){
           	 redTeam.remove(player.getUniqueId());
            }
            //remove from blue team
            if(blueTeam.contains(player.getUniqueId())){
           	 blueTeam.remove(player.getUniqueId());
            }
            //if there is only one person left, make them the victor
            if((redTeam.size() == 0 && blueTeam.size() >= 1) || (blueTeam.size() == 0 && redTeam.size() >= 1)){
           	 loadRegion(world);
           	 for(UUID last : players){
           		 Player lastplayer = Bukkit.getPlayer(last);
           		 lastplayer.sendMessage(ChatColor.YELLOW + "Congratulations!  Your team won!");
           		 lastplayer.teleport(lobbyloc);
           		 lastplayer.getInventory().clear();
           		 restoreInv(lastplayer);
           		 //remove them
           		 if(redTeam.contains(last)){
           			 redTeam.remove(last);
           		 }
           		 if(blueTeam.contains(last)){
           			 blueTeam.remove(last);
           		 }
           		 players.remove(last);
           	 }
        }
    }
}
    
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
   	 Player player = e.getPlayer();
   	 World world = player.getWorld();
   	 
   	 if(spectators.contains(player.getUniqueId())){
   		 unSpectate(player);
   	 }
   	 //get some locations
        int x = getConfig().getInt("lobbyspawn." + "x");
        int y = getConfig().getInt("lobbyspawn." + "y");
        int z = getConfig().getInt("lobbyspawn." + "z");
        World lobbyworld = getServer().getWorld(getConfig().getString("redspawn." + "world"));
        final Location lobbyloc = new Location(lobbyworld,x,y,z);
        
   	 if(players.contains(player.getUniqueId())){
            if((redTeam.size() == 0 && blueTeam.size() >= 1) || (blueTeam.size() == 0 && redTeam.size() >= 1)){
           	 loadRegion(world);
           	 for(UUID last : players){
           		 Player lastplayer = Bukkit.getPlayer(last);
           		 lastplayer.sendMessage(ChatColor.YELLOW + "Congratulations!  Your team won!");
           		 lastplayer.teleport(lobbyloc);
           		 lastplayer.getInventory().clear();
           		 restoreInv(lastplayer);
           		 //remove them
           		 if(redTeam.contains(last)){
           			 redTeam.remove(last);
           		 }
           		 if(blueTeam.contains(last)){
           			 blueTeam.remove(last);
           		 }
           		 players.remove(last);
           	 
        }
   	 }
   	 }
    }
    
    */
}
