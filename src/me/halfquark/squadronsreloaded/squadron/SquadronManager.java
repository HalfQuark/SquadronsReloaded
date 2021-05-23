package me.halfquark.squadronsreloaded.squadron;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import net.countercraft.movecraft.craft.Craft;

public class SquadronManager {

	private static SquadronManager inst;
	private static ConcurrentMap<Player, Squadron> squads;
	
	public static void initialize() {
		inst = new SquadronManager();
	}
	
	private SquadronManager() {
		squads = new ConcurrentHashMap<>();
		new BukkitRunnable() {
            @Override
            public void run() {
                updateSquadrons();
            }
        }.runTaskTimerAsynchronously(SquadronsReloaded.getInstance(), 20*30, 20*30);
	}
	
	public static SquadronManager getInstance() {return inst;}
	
	public void putSquadron(Player p, Squadron s) {squads.put(p, s);}
	public void removeSquadron(Player p) {squads.remove(p);}
	public boolean hasSquadron(Player p) {
		if(!squads.containsKey(p))
			return false;
		Squadron sq = squads.get(p);
		if(sq == null) {
			return false;
		}
		if(sq.getCarrier() == null) {
			sq.sinkAll();
			return false;
		}
		if(sq.getCrafts() == null) {
			return false;
		}
		if(sq.getCrafts().size() == 0) {
			return false;
		}
		return true;
	}
	@Nullable
	public Squadron getSquadron(Player p, boolean check) {
		if(!check)
			return squads.get(p);
		if(hasSquadron(p))
			return squads.get(p);
		return null;
	}
	public List<Squadron> getSquadronList() {
		ArrayList<Squadron> squadList = new ArrayList<>();
		for(Map.Entry<Player, Squadron> entry : squads.entrySet()) {
			squadList.add(entry.getValue());
		}
		return squadList;
	}
	
	private void updateSquadrons() {
		for(Entry<Player, Squadron> entry : squads.entrySet()) {
			Squadron sq = entry.getValue();
			if(sq == null) {
				squads.remove(entry.getKey());
			}
			if(sq.getCrafts() == null) {
				squads.remove(entry.getKey());
			}
			for(Craft craft : sq.getCrafts()) {
				if(!craft.getCruising())
					craft.setLastCruiseUpdate(System.currentTimeMillis());
			}
		}
	}
	
}
