package me.halfquark.squadronsreloaded.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftTranslateEvent;

public class TranslationListener implements Listener {

	@EventHandler
	public void onCraftTranslate(CraftTranslateEvent e) {
		if(e.getCraft().getCruising())
			CraftRotateManager.getInstance().registerCruise(e.getCraft(), e.getCraft().getCruiseDirection());
		Player p = e.getCraft().getNotificationPlayer();
		Craft craft = e.getCraft();
		CraftProximityManager.getInstance().updateCraft(craft, e.getNewHitBox());
		Squadron sq = SquadronManager.getInstance().getSquadron(p, true);
		if(sq == null)
			return;
		if(!sq.hasCraft(e.getCraft()))
			return;
		if(!e.getNewHitBox().isEmpty()) {
			if(CraftProximityManager.getInstance().check(p, craft, e.getNewHitBox())) {
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
