package me.halfquark.squadronsreloaded.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.formation.Formation;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.move.CraftTranslateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftTranslateEvent;

public class TranslationListener implements Listener {

	@EventHandler
	public void onCraftTranslate(CraftTranslateEvent e) {
		if(e.getCraft().getCruising())
			CraftRotateManager.getInstance().registerCruise(e.getCraft(), e.getCraft().getCruiseDirection());
		Player p = e.getCraft().getNotificationPlayer();
		Craft craft = e.getCraft();
		Squadron sq = SquadronManager.getInstance().getSquadron(p, true);
		if(sq == null)
			return;
		if(!sq.hasCraft(e.getCraft()))
			return;
		if(craft.equals(sq.getLeadCraft())) {
			sq.setDirection(CraftRotateManager.getInstance().getDirection(craft));
			return;
		}
		if(e.getCraft().getCruising())
			return;
		CraftRotateManager.getInstance().adjustDirection(sq, craft);
		
		if(!sq.isFormingUp())
			return;
		Formation formation = sq.getFormation();
		CruiseDirection cd = sq.getDirection();
		if(cd == null)
			cd = CruiseDirection.NORTH;
		Double x = (double) sq.getLeadCraft().getHitBox().getMidPoint().getX();
		Double y = (double) sq.getLeadCraft().getHitBox().getMidPoint().getY();
		Double z = (double) sq.getLeadCraft().getHitBox().getMidPoint().getZ();
		x += formation.getXPosition(sq.getCraftRank(e.getCraft()) - sq.getLeadId(), sq.getSpacing(), cd);
		y += formation.getYPosition(sq.getCraftRank(e.getCraft()) - sq.getLeadId(), sq.getSpacing(), cd);
		z += formation.getZPosition(sq.getCraftRank(e.getCraft()) - sq.getLeadId(), sq.getSpacing(), cd);
		Location targetLoc = new Location(e.getWorld(), x, y, z);
		MovecraftLocation mLoc = craft.getHitBox().getMidPoint();
		Location craftLoc = new Location(e.getWorld(), mLoc.getX(), mLoc.getY(), mLoc.getZ());
		if(targetLoc.distanceSquared(craftLoc) <= Math.pow(SquadronsReloaded.FORMATIONROUNDDISTANCE, 2))
			return;
		CraftTranslateManager.getInstance().scheduleMove(e.getCraft(), targetLoc);
	}
	
}
