package me.halfquark.squadronsreloaded.move;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;

public class CraftTranslateManager {
	
	private static CraftTranslateManager inst;
	private static final Map<Craft, Long> timeMap = new HashMap<>();
	private static final Map<Craft, Loc> pendingMoves = new HashMap<>();
	
	public class Loc {
		private int x;
		private int y;
		private int z;
		public Loc(int a, int b, int c) {
			x = a;
			y = b;
			z = c;
		}
		public int getX() {return x;}
		public int getY() {return y;}
		public int getZ() {return z;}
	}
	
	public static void initialize() {
		inst = new CraftTranslateManager();
	}
	
	public CraftTranslateManager() {
		
	}
	
	public static CraftTranslateManager getInstance() {return inst;}
	
	public boolean isInCooldown(Craft craft) {
		return getTicksCooldown(craft) > 0L;
	}
	
	public Long getTicksCooldown(Craft craft) {
		final CraftType type = craft.getType();
		int currentGear = craft.getCurrentGear();
	    Long time = timeMap.get(craft);
	    int tickCooldown = craft.getTickCooldown();
	    if (type.getBoolProperty(CraftType.GEAR_SHIFTS_AFFECT_DIRECT_MOVEMENT) && type.getBoolProperty(CraftType.GEAR_SHIFTS_AFFECT_TICK_COOLDOWN)) {
	        tickCooldown *= currentGear;
	    }
	    if (time != null) {
	        long ticksElapsed = (System.currentTimeMillis() - time) / 50;
	
	        // if the craft should go slower underwater, make time
	        // pass more slowly there
	        if (craft.getType().getBoolProperty(CraftType.HALF_SPEED_UNDERWATER) && craft.getHitBox().getMinY() < craft.getWorld().getSeaLevel())
	            ticksElapsed = ticksElapsed >> 1;
	        return Math.max(tickCooldown - Math.abs(ticksElapsed), 0L);
	    }
	    return 0L;
	}
	
	public boolean translateCraft(Craft craft, int dx, int dy, int dz) {
		if(isInCooldown(craft))
			return false;
		if(pendingMoves.containsKey(craft))
			return false;
		forceTranslateCraft(craft, dx, dy, dz);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void forceTranslateCraft(Craft craft, int dx, int dy, int dz) {
		timeMap.put(craft, 0L);
		craft.translate(dx, dy, dz);
	    timeMap.put(craft, System.currentTimeMillis());
	    craft.setLastCruiseUpdate(System.currentTimeMillis());
	}
	
	private void performPendingMove(Craft craft) {
		if(!pendingMoves.containsKey(craft))
			return;
		Loc loc = pendingMoves.get(craft);
		int dx = (int) Math.signum(loc.getX() - craft.getHitBox().getMidPoint().getX());
		int dy = (int) Math.signum(loc.getY() - craft.getHitBox().getMidPoint().getY());
		int dz = (int) Math.signum(loc.getZ() - craft.getHitBox().getMidPoint().getZ());
		forceTranslateCraft(craft, dx, dy, dz);
		pendingMoves.remove(craft);
		return;
	}
	
	public void scheduleMove(Craft craft, int x, int y, int z) {
		if(pendingMoves.containsKey(craft))
			return;
		pendingMoves.put(craft, new Loc(x, y, z));
		if(!isInCooldown(craft))
			performPendingMove(craft);
		new BukkitRunnable() {
            @Override
            public void run() {
                performPendingMove(craft);
            }  
        }.runTaskLater(SquadronsReloaded.getInstance(), (long) (getTicksCooldown(craft) * SquadronsReloaded.FORMATIONSPEEDMULTIPLIER));
	}
}
