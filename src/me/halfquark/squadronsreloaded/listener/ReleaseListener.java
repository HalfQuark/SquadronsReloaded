package me.halfquark.squadronsreloaded.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;

public class ReleaseListener implements Listener {

	@EventHandler
	public void onCraftRelease(CraftReleaseEvent e) {
		Player p = e.getCraft().getNotificationPlayer();
		if(p == null)
			return;
		if(!SquadronManager.getInstance().hasSquadron(p))
			return;
		Squadron sq = SquadronManager.getInstance().getSquadron(p, true);
		if(sq.getCarrier() != null) {
			if(sq.getCarrier().equals(e.getCraft())) {
				SquadronManager.getInstance().getSquadron(p, false).releaseAll(e.getReason());
				SquadronManager.getInstance().removeSquadron(p);
				return;
			}
		}
		if(sq.hasCraft(e.getCraft())) {
			sq.removeCraft(e.getCraft());
			if(sq.getSize() == 0) {
				SquadronManager.getInstance().removeSquadron(p);
				p.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Squadron has been released"));
			}
		}
	}
	
}
