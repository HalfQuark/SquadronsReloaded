package me.halfquark.squadronsreloaded.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.events.CraftRotateEvent;

public class RotationListener implements Listener {
	
	@EventHandler
	public void onCraftRotate(CraftRotateEvent e) {
		if(!(e.getCraft() instanceof SquadronCraft))
			return;
		SquadronCraft craft = (SquadronCraft) e.getCraft();
		if(CraftProximityManager.getInstance().check(craft, e.getNewHitBox())) {
			e.setCancelled(true);
			e.setFailMessage("Squadron craft obstructed");
			return;
		}
		CraftProximityManager.getInstance().updateCraft(craft, e.getNewHitBox());
		CraftRotateManager.getInstance().registerRotation(e.getCraft(), e.getRotation());
		if(craft.isLead())
			craft.getSquadron().setDirection(CraftRotateManager.getInstance().getDirection(craft));
		//Bad idea, rotation calls this
		//CraftRotateManager.getInstance().adjustDirection(sq, craft);
	}
	
}
