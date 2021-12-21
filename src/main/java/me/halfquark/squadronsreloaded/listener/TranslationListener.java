package me.halfquark.squadronsreloaded.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.events.CraftTranslateEvent;

public class TranslationListener implements Listener {

	@EventHandler
	public void onCraftTranslate(CraftTranslateEvent e) {
		if(e.getCraft().getCruising())
			CraftRotateManager.getInstance().registerCruise(e.getCraft(), e.getCraft().getCruiseDirection());
		if(!(e.getCraft() instanceof SquadronCraft))
			return;
		if(e.isCancelled())
			return;
		SquadronCraft craft = (SquadronCraft) e.getCraft();
		CraftProximityManager.getInstance().updateCraft(craft, e.getNewHitBox());
		Squadron sq = craft.getSquadron();
		if(!e.getNewHitBox().isEmpty()) {
			if(CraftProximityManager.getInstance().check(craft, e.getNewHitBox())) {
				e.setCancelled(true);
				e.setFailMessage("Squadron craft obstructed");
				return;
			}
		}
		if(craft.equals(sq.getLeadCraft())) {
			sq.setDirection(CraftRotateManager.getInstance().getDirection(craft));
			return;
		}
		if(e.getCraft().getCruising())
			return;
	}
	
}
