package me.junglesociety.tntwars2;

import java.awt.List;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;

public class Arena {
	
	public int id;
	public Location blueSpawn;
	public Location redSpawn;
	public Location specSpawn;
	public Location min;
	public Location max;
	public boolean ingame;
	
	ArrayList<String> players = new ArrayList<String>();
    ArrayList<String> redTeam = new ArrayList<String>();
    ArrayList<String> blueTeam = new ArrayList<String>();
    ArrayList<String> spectators = new ArrayList<String>();
	
    public Arena(int arenaID){
    	this.id = arenaID;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public Location getBlueSpawn(){
    	return this.blueSpawn;
    }
    
    public Location getRedSpawn(){
    	return this.redSpawn;
    }
    
    public Location getSpectateSpawn(){
    	return this.specSpawn;
    }
    
    public ArrayList<String> getPlayers(){
    	return this.players;
    }
    
    public ArrayList<String> getSpectators(){
    	return this.spectators;
    }
    
    public ArrayList<String> getRedPlayers(){
    	return this.redTeam;
    }
    
    public ArrayList<String> getBluePlayers(){
    	return this.blueTeam;
    }
    
    public Location getMin(){
    	return this.getMin();
    }
    
    public Location getMax(){
    	return this.getMax();
    }
    
    public boolean isInGame(){
    	return ingame;
    }
    
    public void setInGame(boolean isingame){
    	ingame = isingame;
    }
}
