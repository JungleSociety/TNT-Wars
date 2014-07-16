package me.junglesociety.tntwars2;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TNTWars extends JavaPlugin implements Listener{
	
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		
		settings.setup(this);
		settings.getData().createSection("ArenaNumbers");
		settings.getData().createSection("Arenas");
		
		settings.saveData();
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		settings.saveData();
	}
	
	SettingsManager settings = SettingsManager.getInstance();
	ArenaManager am = ArenaManager.getInstance();
	
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("You must be a player to do this!");
        }else{
            final Player player = (Player) sender;
            
            //TnTWars Command
            if(label.equalsIgnoreCase("tntwars")){
                if(args.length == 0){
                    player.sendMessage("Please use /tntwars help for help!");
                }else{
               	 //Join command
                    if(args[0].equalsIgnoreCase("join")){
                   	 if(player.hasPermission("tntwars.join")){
                   		 if(args.length > 1){
                   			 int num = Integer.parseInt(args[1]);
                   			 
                   			 am.addPlayer(player, num);
                   			 player.sendMessage(ChatColor.YELLOW + "You have been added to the TNT Wars list!");
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an arena!");
                   		 }
                   	 }
                    }
                    
                    
                    
                    //Spectate
                    if(args[0].equalsIgnoreCase("spectate")){
                   	 if(player.hasPermission("tntwars.spectate")){
                   		 if(args.length > 1){
                   			 Integer i = Integer.parseInt(args[1]);
                   			 
                   			 if(i == 0 || i == null){
                   				 player.sendMessage(ChatColor.RED + "Invalid arena!");
                   			 }else{
                   				 am.spectate(player, i);
                   			 }
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an arena!");
                   		 }
                    }
                    }
                    
                    
                    //UnSpectate
                    if(args[0].equalsIgnoreCase("unspectate")){
                    	if(player.hasPermission("tntwars.spectate")){
                    		int arena = 0;
                    	    for(Arena a : am.getArenaList()){
                    	    	if(a.getSpectators().contains(player.getName())){
                    	    		arena = a.getID();
                    	    	}
                    	    }
                    	    if(arena == 0){
                    	    	player.sendMessage(ChatColor.RED + "You are not spectating!");
                    	    }else{
                    	    am.unSpectate(player, arena);
                    	    player.sendMessage(ChatColor.YELLOW + "You are no longer spectating!");
                    	    }
                    	}
                    }
                    
                    
                    //Set Position 1
                    if(args[0].equalsIgnoreCase("min")){
                   	 if(player.hasPermission("tntwars.position")){
                   		 if(args.length > 1){
                   			 int i = Integer.parseInt(args[1]);
                   			 
                   			 Arena a = am.getArena(i);
                   			 
                   			 if(a == null){
                   				 player.sendMessage(ChatColor.RED + "Invalid Arena!");
                   			 }else{
                   				 am.setMinLoc(player, i);
                   				 player.sendMessage(ChatColor.YELLOW + "Minimum position set!");
                   			 }
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an Arena!");
                   		 }
                    }
                    }
                    
                    
                    //Set Position 2
                    if(args[0].equalsIgnoreCase("max")){
                   	 if(player.hasPermission("tntwars.position")){
                   	 
                   		 if(args.length > 1){
                   			 int i = Integer.parseInt(args[0]);
                   			 Arena a = am.getArena(i);
                   			 
                   			 if(a == null){
                   				 player.sendMessage(ChatColor.RED + "Invalid Arena!");
                   			 }else{
                   				 am.setMaxLoc(player, i);
                   				 player.sendMessage(ChatColor.YELLOW + "Maximum position set!");
                   			 }
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an arena!");
                   		 }
                   	 
                    }
                    }
                    
                    
                    //Save the arenas schematic file
                    if(args[0].equalsIgnoreCase("savearena")){
                   	 if(player.hasPermission("tntwars.save")){
                   		 if(args.length > 1){
                   			 int i = Integer.parseInt(args[1]);
                   			 Arena a = am.getArena(i);
                   			 
                   			 if(a == null){
                   				 player.sendMessage(ChatColor.RED + "Invalid Arena!");
                   			 }else{
                   				 double minx = settings.getData().getDouble("Arenas." + i +  ".minloc.x");
                   				 double miny = settings.getData().getDouble("Arenas." + i +  ".minloc.y");
                   				 double minz = settings.getData().getDouble("Arenas." + i +  ".minloc.z");
                   				 String minworld = settings.getData().getString("Arenas." + i +  ".minloc.world");
                   				 
                   				 double maxx = settings.getData().getDouble("Arenas." + i +  ".maxloc.x");
                   				 double maxy = settings.getData().getDouble("Arenas." + i +  ".maxloc.y");
                   				 double maxz = settings.getData().getDouble("Arenas." + i +  ".maxloc.z");
                   				 String maxworld = settings.getData().getString("Arenas." + i +  ".maxloc.world");
                   			     
                   				 Location minloc = new Location(Bukkit.getWorld(minworld), minx, miny, minz);
                   				 Location maxloc = new Location(Bukkit.getWorld(maxworld), maxx, maxy, maxz);
                   				 
                   				Double min = Double.valueOf(minx);
                   				Double max = Double.valueOf(maxx);
                   				 if(min == null || max == null){
                   					 player.sendMessage(ChatColor.RED + "There is no value for a maximum and minimum in this arena, please set one with /TNTWars max and /TNTWars min!");
                   				 }else{
                   					 am.saveRegion(minloc, maxloc, player, i);
                   					 player.sendMessage(ChatColor.YELLOW + "Arena saved!");
                   				 }
                   				 
                   			 }
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an arena!");
                   		 }
                    }
                    }
                    
                    
                    //redspawn command
                    if(args[0].equalsIgnoreCase("setredspawn")){
                   	 if(player.hasPermission("tntwars.setredspawn")){
                        if(args.length > 1){
                        	int i = Integer.parseInt(args[1]);
                        	Arena a = am.getArena(i);
                        	
                        	if(a == null){
                        		player.sendMessage(ChatColor.RED + "Invalid Arena!");
                        	}else{
                        		am.setRedSpawn(player, i);
                           		player.sendMessage(ChatColor.YELLOW + "Red spawn set for Arena " + i );
                        	}
                        }else{
                        	player.sendMessage(ChatColor.RED + "Please specify an arena!");
                        }
                   	 }
                    }
                    
                    
                    //bluespawn command
                    if(args[0].equalsIgnoreCase("setbluespawn")){
                      	 if(player.hasPermission("tntwars.setbluespawn")){
                           if(args.length > 1){
                           	int i = Integer.parseInt(args[1]);
                           	Arena a = am.getArena(i);
                           	
                           	if(a == null){
                           		player.sendMessage(ChatColor.RED + "Invalid Arena!");
                           	}else{
                           		am.setBlueSpawn(player, i);
                           		player.sendMessage(ChatColor.YELLOW + "Blue spawn set for Arena " + i );
                           	}
                           }else{
                           	player.sendMessage(ChatColor.RED + "Please specify an arena!");
                           }
                      	 }
                       }
                    
                    
                    //leave the game
                    if(args[0].equalsIgnoreCase("leave")){
                   	 //declare lobby locations
                   	 if(player.hasPermission("tntwars.leave")){
                   		 int i = 0;
                   		 for(Arena arena : am.getArenaList()){
                   			 if(arena.getPlayers().contains(player.getName())){
                   				 i = arena.getID();
                   			 }
                   		 }
                   		 if(i == 0){
                   			 player.sendMessage(ChatColor.RED + "You are not currently in game!");
                   		 }else{
                   		 am.removePlayer(player, i);
                   		 }
                   	 }
                    }
                    
                    
                    //Set spectator spawn
                    if(args[0].equalsIgnoreCase("setspectatorspawn")){
                   	 if(player.hasPermission("tntwars.setspectatorspawn")){
                   		 if(args.length > 1){
                   			 int i = Integer.parseInt(args[1]);
                   			 Arena a = am.getArena(i);
                   			 
                   			 if(a == null){
                   				 player.sendMessage(ChatColor.RED + "Invalid Arena!");
                   			 }else{
                   				 am.setSpectatorSpawn(player, i);
                   			 }
                   			 
                   		 }else{
                   			 player.sendMessage(ChatColor.RED + "Please specify an arena!");
                   		 }
                   	 }
                    }
                    
                    //setlobbyspawn command
                    if(args[0].equalsIgnoreCase("setlobbyspawn")){
                   	 if(player.hasPermission("tntwars.setlobbyspawn")){
                   		 am.setLobbySpawn(player);
                   	 }
                    }
                    
                    
                    //List arenas
                    if(args[0].equalsIgnoreCase("list")){
                    	if(player.hasPermission("tntwars.list")){                    		
                    		for(int i : settings.getData().getIntegerList("ArenaNumbers")){
                    			player.sendMessage(ChatColor.GREEN + "Arena #" + i);
                    		}
                    	}
                    }
                    
                    
                    /*
                     * 
                     * Create arena
                     * 
                     */
                    
                    if(args[0].equalsIgnoreCase("create")){
                    	if(player.hasPermission("tntwars.create")){
                    	am.createArena(player);
                    	player.sendMessage(ChatColor.GREEN + "Arena created!");
                    	}
                    }
                    
                    /*
                     * 
                     * Delete arena
                     */
                    
                    if(args[0].equalsIgnoreCase("remove")){
                    	if(!(player.hasPermission("tntwars.remove"))){
                    		return false;
                    	}
                    	if(args.length > 1){
                    		Integer i = Integer.parseInt(args[1]);
                    		am.deleteArena(i, player);
                    	}else{
                    		player.sendMessage(ChatColor.RED + "Please specify an arena to remove!");
                    	}
                    }
                    
                    
                    if(args[0].equalsIgnoreCase("todo")){
                    	if(!(player.hasPermission("tntwars.todo"))){
                    		return false;
                    	}
                    	if(args.length > 1){
                    		Integer i = Integer.parseInt(args[1]);
                    		player.sendMessage(ChatColor.YELLOW + "These need to be done: ");
                    		if(settings.getData().get("Arenas." + i + ".bluespawn.x").equals(null)){
                    			player.sendMessage(ChatColor.BLUE + "Blue Spawn needs to be set");
                    		}
                    		
                    		if(settings.getData().get("Arenas." + i + ".redspawn.x").equals(null)){
                    			player.sendMessage(ChatColor.BLUE + "Red Spawn needs to be set");
                    		}
                    		
                    		if(settings.getData().get("Arenas." + i + ".specspawn.x").equals(null)){
                    			player.sendMessage(ChatColor.BLUE + "Spectator Spawn needs to be set");
                    		}
                    		
                    		if(settings.getData().get("Arenas." + i + ".minloc.x").equals(null)){
                    			player.sendMessage(ChatColor.BLUE + "Minimum Location needs to be set");
                    		}
                    		
                    		if(settings.getData().get("Arenas." + i + ".maxloc.x").equals(null)){
                    			player.sendMessage(ChatColor.BLUE + "Maximum Location needs to be set");
                    		}
                    	}else{
                    		player.sendMessage(ChatColor.RED + "Please specify an arena to check what needs to be done!");
                    	}
                    }
                    
                    //Help command
                    if(args[0].equalsIgnoreCase("help")){
                   	 if(player.hasPermission("tntwars.help")){
                   	 if(player.hasPermission("tntwars.join")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Join" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.leave")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Leave");
                   	 }
                   	 if(player.hasPermission("tntwars.spectate")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Spectate" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.min")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Min" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.max")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Max" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.setlobbyspawn")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars SetLobbySpawn");
                   	 }
                   	 if(player.hasPermission("tntwars.setredspawn")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars SetRedSpawn" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.setbluespawn")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars SetBlueSpawn" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.save")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars SaveArena" + ChatColor.BLUE + " {Arena ID}");
                   	 }
                   	 if(player.hasPermission("tntwars.list")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars list");
                   	 }
                   	 if(player.hasPermission("tntwars.help")){
                   		 player.sendMessage(ChatColor.GREEN + "/TNTWars Help");
                   	 }
                   	 }
                    }
                    
                    //If their command doesnt exist
                    if(!(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("todo") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("spectate") || args[0].equalsIgnoreCase("min") || args[0].equalsIgnoreCase("setlobbyspawn") || args[0].equalsIgnoreCase("setbluespawn") || args[0].equalsIgnoreCase("setredspawn") || args[0].equalsIgnoreCase("setspectatorspawn") || args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("max") || args[0].equalsIgnoreCase("list"))){
                  
                   	 player.sendMessage(ChatColor.RED + "Please use /tntwars help for help!");
                    }
                }
            }
        }
        
        
        return false;
    }

}
