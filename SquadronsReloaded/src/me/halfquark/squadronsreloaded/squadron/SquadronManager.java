package me.halfquark.squadronsreloaded.squadron;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class SquadronManager {

	private static SquadronManager inst;
	private static ConcurrentMap<Player, Squadron> squads;
	
	public static void initialize() {
		inst = new SquadronManager();
	}
	
	private SquadronManager() {
		squads = new ConcurrentHashMap<>();
	}
	
	public static SquadronManager getInstance() {return inst;}
	
	public void putSquadron(Player p, Squadron s) {squads.put(p, s);}
	public void removeSquadron(Player p) {squads.remove(p);}
	public boolean hasSquadron(Player p) {return squads.containsKey(p);}
	@Nullable
	public Squadron getSquadron(Player p) {return squads.get(p);}
	public List<Squadron> getSquadronList() {
		ArrayList<Squadron> squadList = new ArrayList<>();
		for(Map.Entry<Player, Squadron> entry : squads.entrySet()) {
			squadList.add(entry.getValue());
		}
		return squadList;
	}
	
}
