package me.halfquark.squadronsreloaded.formation;

import org.bukkit.Location;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftTranslateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;

public class FormationFormer {

	public static void formUp(Craft craft, Squadron sq) {
		Formation formation = sq.getFormation();
		CruiseDirection cd = sq.getDirection();
		if(cd == null)
			cd = CruiseDirection.NORTH;
		Double x = (double) sq.getLeadCraft().getHitBox().getMidPoint().getX();
		Double y = (double) sq.getLeadCraft().getHitBox().getMidPoint().getY();
		Double z = (double) sq.getLeadCraft().getHitBox().getMidPoint().getZ();
		x += formation.getXPosition(sq.getCraftRank(craft) - sq.getLeadId(), sq.getSpacing(), cd);
		y += formation.getYPosition(sq.getCraftRank(craft) - sq.getLeadId(), sq.getSpacing(), cd);
		z += formation.getZPosition(sq.getCraftRank(craft) - sq.getLeadId(), sq.getSpacing(), cd);
		Location targetLoc = new Location(craft.getW(), x, y, z);
		MovecraftLocation mLoc = craft.getHitBox().getMidPoint();
		Location craftLoc = new Location(craft.getW(), mLoc.getX(), mLoc.getY(), mLoc.getZ());
		if(targetLoc.distanceSquared(craftLoc) <= Math.pow(SquadronsReloaded.FORMATIONROUNDDISTANCE, 2))
			return;
		int dx = (int) Math.signum(x - craftLoc.getX());
		int dy = (int) Math.signum(y - craftLoc.getY());
		int dz = (int) Math.signum(z - craftLoc.getZ());
		CraftProximityManager.Box b = CraftProximityManager.hitBoxToBox(craft.getHitBox());
		b.translate(dx, 0, 0);
		if(CraftProximityManager.getInstance().check(sq.getPilot(), craft, b)) {
			b.translate(-dx, 0, 0);
			dx = 0;
		}
		b.translate(0, dy, 0);
		if(CraftProximityManager.getInstance().check(sq.getPilot(), craft, b)) {
			b.translate(0, -dy, 0);
			dy = 0;
		}
		b.translate(0, 0, dz);
		if(CraftProximityManager.getInstance().check(sq.getPilot(), craft, b)) {
			b.translate(0, 0, -dz);
			dz = 0;
		}
		Location moveLoc = new Location(craft.getW(), mLoc.getX() + dx, mLoc.getY() + dy, mLoc.getZ() + dz);
		CraftTranslateManager.getInstance().scheduleMove(craft, moveLoc);
	}
	
}
