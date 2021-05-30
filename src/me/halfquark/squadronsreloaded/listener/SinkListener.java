package me.halfquark.squadronsreloaded.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftSinkEvent;
import net.countercraft.movecraft.events.ManOverboardEvent;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;
import net.countercraft.movecraft.utils.MathUtils;

public class SinkListener implements Listener {

	@EventHandler
	public void onCraftSink(CraftSinkEvent e) {
		List<Squadron> sqList = SquadronManager.getInstance().getCarrierSquadrons(e.getCraft());
		if(sqList.size() > 0) {
			for(Squadron sq : sqList) {
				sq.sinkAll();
				SquadronManager.getInstance().removeSquadron(sq);
			}
			return;
		}
		Player p = e.getCraft().getNotificationPlayer();
		if(p == null)
			return;
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(p, true);
		if(sq == null)
			return;
		if(sq.hasCraft(e.getCraft())) {
			boolean tpToLead = false;
			if(sq.getLeadCraft().equals(e.getCraft())) {
				if(MathUtils.locationNearHitBox(e.getCraft().getHitBox(), p.getLocation(),2))
					tpToLead = true;
			}
			sq.removeCraft(e.getCraft());
			if(sq.getSize() == 0) {
				SquadronManager.getInstance().removeSquadron(p);
				p.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Squadron has been released"));
			}
			if(tpToLead && SquadronsReloaded.TPTONEWLEAD) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if(!SquadronManager.getInstance().hasSquadron(p))
							return;
						ManOverboardEvent event = new ManOverboardEvent(sq.getLeadCraft(), getCraftTeleportPoint(sq.getLeadCraft()));
				        Bukkit.getServer().getPluginManager().callEvent(event);

				        p.setVelocity(new Vector(0, 0, 0));
				        p.setFallDistance(0);
				        p.teleport(event.getLocation());
					}		
				}.runTaskLater(SquadronsReloaded.getInstance(), 1);
			}
			return;
		}
		if(sq.getCarrier().equals(e.getCraft())) {
			sq.sinkAll();
			SquadronManager.getInstance().removeSquadron(p);
			return;
		}
	}
	
	private Location getCraftTeleportPoint(Craft craft) {
        double telX = (craft.getHitBox().getMinX() + craft.getHitBox().getMaxX())/2D + 0.5D;
        double telZ = (craft.getHitBox().getMinZ() + craft.getHitBox().getMaxZ())/2D + 0.5D;
        double telY = craft.getHitBox().getMaxY() + 1;
        return new Location(craft.getW(), telX, telY, telZ);
    }
	
}
