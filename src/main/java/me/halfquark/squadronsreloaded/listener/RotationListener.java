package me.halfquark.squadronsreloaded.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.events.CraftRotateEvent;

public class RotationListener implements Listener {
	
	@EventHandler
	public void onCraftRotate(CraftRotateEvent e) {
		if(!(e.getCraft() instanceof SquadronCraft))
			return;
		if(e.isCancelled())
			return;
		SquadronCraft craft = (SquadronCraft) e.getCraft();
		if(CraftProximityManager.getInstance().check(craft, e.getNewHitBox())) {
			e.setCancelled(true);
			e.setFailMessage("Squadron craft obstructed");
			return;
		}
		CraftProximityManager.getInstance().updateCraft(craft, e.getNewHitBox());
		CraftRotateManager.getInstance().registerRotation(e.getCraft(), e.getRotation());
		CruiseDirection cd = CraftRotateManager.getInstance().getDirection(craft);
		if(craft.isLead()) {
			craft.getSquadron().setDirection(cd);
			if(isHorizontal(cd))
				craft.getSquadron().setCruiseDirection(cd);
		}
		//Bad idea, rotation calls this
		//CraftRotateManager.getInstance().adjustDirection(sq, craft);
	}
	
	private boolean isHorizontal(CruiseDirection cd) {
		return cd.equals(CruiseDirection.EAST)
			|| cd.equals(CruiseDirection.WEST)
			|| cd.equals(CruiseDirection.NORTH)
			|| cd.equals(CruiseDirection.SOUTH);
	}
	
}
