package me.halfquark.squadronsreloaded.move;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import org.jetbrains.annotations.NotNull;

public class CraftProximityManager {

	/*
	public static class Box {
		public int minX;
		public int minY;
		public int minZ;
		public int maxX;
		public int maxY;
		public int maxZ;
		
		public Box(int a, int b, int c, int d, int e, int f) {
			minX = a;
			minY = b;
			minZ = c;
			maxX = d;
			maxY = e;
			maxZ = f;
		}
		
		public void expand(int n) {
			minX -= n;
			minY -= n;
			minZ -= n;
			maxX += n;
			maxY += n;
			maxZ += n;
		}
		
		public void translate(int dx, int dy, int dz) {
			minX += dx;
			minY += dy;
			minZ += dz;
			maxX += dx;
			maxY += dy;
			maxZ += dz;
		}
		
	}
	
	private static final ConcurrentMap<Craft, HitBox> preHitboxMap = new ConcurrentHashMap<>();
	private static CraftProximityManager inst;
	
	public static void initialize() {
		inst = new CraftProximityManager();
	}
	
	public static CraftProximityManager getInstance() {return inst;}

	public void updateCraft(Craft craft, HitBox hb) {
		if(hb.isEmpty())
			return;
		preHitboxMap.put(craft, hb);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(preHitboxMap.get(craft).equals(hb))
					preHitboxMap.remove(craft);
			}
        }.runTaskLater(SquadronsReloaded.getInstance(), 2);
	}
	
	public boolean check(SquadronCraft craft, HitBox newHb) {
		if(newHb.isEmpty())
			return false;
		Box craftBox = hitBoxToBox(newHb);
		return check(craft, craftBox);
	}
	
	public boolean check(SquadronCraft craft, Box craftBox) {
		Squadron sq = craft.getSquadron();
		if(sq == null)
			return false;
		if(!sq.hasCraft(craft))
			return false;
		//craftBox.expand(1);
		for(Craft c : sq.getCrafts()) {
			if(c.equals(craft))
				continue;
			if(c.getHitBox().isEmpty())
				continue;
			Box b = hitBoxToBox(c.getHitBox());
			if(boxIntersect(craftBox, b)) {
				return true;
			}
			if(preHitboxMap.containsKey(c)) {
				if(preHitboxMap.get(c).isEmpty())
					continue;
				if(boxIntersect(craftBox, hitBoxToBox(preHitboxMap.get(c))))
					return true;
			}
		}
		return false;
	}
	
	public static Box hitBoxToBox(HitBox hb) {
		return new Box(
			hb.getMinX(),
			hb.getMinY(),
			hb.getMinZ(),
			hb.getMaxX(),
			hb.getMaxY(),
			hb.getMaxZ()
		);
	}
	
	public static boolean boxIntersect(Box b1, Box b2) {
		return !(
			b1.minX > b2.maxX || b1.maxX < b2.minX ||
			b1.minY > b2.maxY || b1.maxY < b2.minY ||
			b1.minZ > b2.maxZ || b1.maxZ < b2.minZ
		);
	}
	*/

	private static final ConcurrentMap<Craft, HitBox> preHitboxMap = new ConcurrentHashMap<>();
	private static CraftProximityManager inst;

	public static void initialize() {
		inst = new CraftProximityManager();
	}

	public static CraftProximityManager getInstance() {return inst;}

	public void updateCraft(Craft craft, HitBox hb) {
		if(hb.isEmpty())
			return;
		preHitboxMap.put(craft, hb);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(preHitboxMap.get(craft).equals(hb))
					preHitboxMap.remove(craft);
			}
		}.runTaskLater(SquadronsReloaded.getInstance(), 2);
	}

	public boolean check(SquadronCraft craft, HitBox newHb){
		return check(craft, newHb, 0, 0, 0);
	}
	public boolean check(SquadronCraft craft, HitBox newHb, int dx, int dy, int dz) {
		Squadron sq = craft.getSquadron();
		if(sq == null)
			return false;
		if(!sq.hasCraft(craft))
			return false;
		//craftBox.expand(1);
		for(Craft c : sq.getCrafts()) {
			if(c.equals(craft))
				continue;
			if(c.getHitBox().isEmpty())
				continue;
			if(!newHb.intersection(c.getHitBox()).isEmpty())
				return true;
			if(preHitboxMap.containsKey(c)) {
				if(preHitboxMap.get(c).isEmpty())
					continue;
				for (MovecraftLocation ml : newHb) {
					ml.translate(dx, dy, dz);
					if (preHitboxMap.get(c).contains(ml))
						return true;
				}
			}
		}
		return false;
	}
	
}
