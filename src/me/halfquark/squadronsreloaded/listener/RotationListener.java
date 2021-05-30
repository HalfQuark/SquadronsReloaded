package me.halfquark.squadronsreloaded.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftRotateEvent;

public class RotationListener implements Listener {
	
	@EventHandler
	public void onCraftRotate(CraftRotateEvent e) {
		Player p = e.getCraft().getNotificationPlayer();
		Craft craft = e.getCraft();
		CraftProximityManager.getInstance().updateCraft(craft, e.getNewHitBox());
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(p, true);
		if(sq == null)
			return;
		if(!sq.hasCraft(e.getCraft()))
			return;
		if(CraftProximityManager.getInstance().check(p, craft, e.getNewHitBox())) {
			e.setCancelled(true);
			e.setFailMessage("Squadron craft obstructed");
			return;
		}
		CraftRotateManager.getInstance().registerRotation(e.getCraft(), e.getRotation());
		if(craft.equals(sq.getLeadCraft()))
			sq.setDirection(CraftRotateManager.getInstance().getDirection(craft));
		//Bad idea, rotation calls this
		//CraftRotateManager.getInstance().adjustDirection(sq, craft);
	}
	
}
