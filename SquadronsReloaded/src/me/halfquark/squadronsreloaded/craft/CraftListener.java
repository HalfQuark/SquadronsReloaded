package me.halfquark.squadronsreloaded.craft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.events.CraftSinkEvent;

public class CraftListener implements Listener {

	@EventHandler
	public void onCraftRelease(CraftReleaseEvent e) {
		Player p = e.getCraft().getNotificationPlayer();
		if(p == null)
			return;
		if(!SquadronManager.getInstance().hasSquadron(p))
			return;
		Squadron sq = SquadronManager.getInstance().getSquadron(p);
		if(sq.getCarrier() != null) {
			if(sq.getCarrier().equals(e.getCraft())) {
				SquadronManager.getInstance().getSquadron(p).releaseAll(e.getReason());
				SquadronManager.getInstance().removeSquadron(p);
				return;
			}
		}
		if(SquadronManager.getInstance().getSquadron(p).hasCraft(e.getCraft()))
			SquadronManager.getInstance().getSquadron(p).removeCraft(e.getCraft());
	}
	
	@EventHandler
	public void onCraftSink(CraftSinkEvent e) {
		Player p = e.getCraft().getNotificationPlayer();
		if(!SquadronManager.getInstance().hasSquadron(p))
			return;
		Squadron sq = SquadronManager.getInstance().getSquadron(p);
		if(sq.hasCraft(e.getCraft())) {
			Bukkit.broadcastMessage("Squad craft sunk");
			sq.removeCraft(e.getCraft());
			return;
		}
		Bukkit.broadcastMessage("Carrier sunk");
		sq.sinkAll();
		SquadronManager.getInstance().removeSquadron(p);
	}
	
}
