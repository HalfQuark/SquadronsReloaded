package me.halfquark.squadronsreloaded.formation;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftTranslateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import org.bukkit.Bukkit;

public class FormationFormer {

	public static void formUp(SquadronCraft craft) {
		Squadron sq = craft.getSquadron();
		Formation formation = sq.getFormation();
		CruiseDirection cd = sq.getDirection();
		if(cd == null)
			cd = CruiseDirection.NORTH;
		Double x = (double) sq.getLeadCraft().getHitBox().getMidPoint().getX();
		Double y = (double) sq.getLeadCraft().getHitBox().getMidPoint().getY();
		Double z = (double) sq.getLeadCraft().getHitBox().getMidPoint().getZ();
		x += formation.getXPosition(sq.getCraftRank(craft), sq.getSpacing(), cd);
		y += formation.getYPosition(sq.getCraftRank(craft), sq.getSpacing(), cd);
		z += formation.getZPosition(sq.getCraftRank(craft), sq.getSpacing(), cd);
		MovecraftLocation mLoc = craft.getHitBox().getMidPoint();
		if((x-mLoc.getX())*(x-mLoc.getX()) + (y-mLoc.getY())*(y-mLoc.getY()) + (z-mLoc.getZ())*(z-mLoc.getZ()) <= Math.pow(SquadronsReloaded.FORMATIONROUNDDISTANCE, 2))
			return;
		int dx = (int) Math.signum(x - mLoc.getX());
		int dy = (int) Math.signum(y - mLoc.getY());
		int dz = (int) Math.signum(z - mLoc.getZ());
		HitBox b = craft.getHitBox();
		if(CraftProximityManager.getInstance().check(craft, b, dx, 0, 0)) {
			dx = 0;
		}
		if(CraftProximityManager.getInstance().check(craft, b, 0, 0, dz)) {
			dz = 0;
		}
		if(CraftProximityManager.getInstance().check(craft, b, 0, dy, 0)) {
			dy = 0;
		}
		CraftTranslateManager.getInstance().scheduleMove(craft, mLoc.getX() + dx, mLoc.getY() + dy, mLoc.getZ() + dz);
	}
	
}
