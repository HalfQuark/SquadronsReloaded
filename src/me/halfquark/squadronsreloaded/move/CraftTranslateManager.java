package me.halfquark.squadronsreloaded.move;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftType;

public class CraftTranslateManager {
	
	private static CraftTranslateManager inst;
	private static final Map<Craft, Long> timeMap = new HashMap<>();
	private static final Map<Craft, Location> pendingMoves = new HashMap<>();
	
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
	    World w = craft.getW();
	    int tickCooldown = craft.getType().getTickCooldown(w);
	    if (type.getGearShiftsAffectDirectMovement() && type.getGearShiftsAffectTickCooldown()) {
	        tickCooldown *= currentGear;
	    }
	    if (time != null) {
	        long ticksElapsed = (System.currentTimeMillis() - time) / 50;
	
	        // if the craft should go slower underwater, make time
	        // pass more slowly there
	        if (craft.getType().getHalfSpeedUnderwater() && craft.getHitBox().getMinY() < craft.getW().getSeaLevel())
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
		Location loc = pendingMoves.get(craft);
		int dx = (int) Math.signum(loc.getX() - craft.getHitBox().getMidPoint().getX());
		int dy = (int) Math.signum(loc.getY() - craft.getHitBox().getMidPoint().getY());
		int dz = (int) Math.signum(loc.getZ() - craft.getHitBox().getMidPoint().getZ());
		forceTranslateCraft(craft, dx, dy, dz);
		pendingMoves.remove(craft);
		return;
	}
	
	public void scheduleMove(Craft craft, Location loc) {
		if(pendingMoves.containsKey(craft))
			return;
		pendingMoves.put(craft, loc);
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
