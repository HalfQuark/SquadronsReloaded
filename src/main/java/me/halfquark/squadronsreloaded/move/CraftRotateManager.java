package me.halfquark.squadronsreloaded.move;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftRotation;
import net.countercraft.movecraft.craft.Craft;

public class CraftRotateManager {
	
	private static CraftRotateManager inst;
	private static final Map<Craft, CruiseDirection> directionMap = new HashMap<>();
	private static final Map<Craft, Boolean> turningMap = new HashMap<>();
	
	public static void initialize() {
		inst = new CraftRotateManager();
	}
	
	public CraftRotateManager() {
		
	}
	
	public static CraftRotateManager getInstance() {return inst;}
	
	public void registerCruise(Craft craft, CruiseDirection cd) {
		if(!isHorizontal(cd))
			return;
		directionMap.putIfAbsent(craft, cd);
	}
	
	public void registerRotation(Craft craft, MovecraftRotation r) {
		CruiseDirection cd = getDirection(craft);
		if(cd == null)
			return;
		directionMap.put(craft, rotateDirection(cd, r));
	}
	
	@Nullable
	public CruiseDirection getDirection(Craft craft) {return directionMap.get(craft);}
	public void setDirection(Craft craft, CruiseDirection cd) {directionMap.put(craft, cd);}
	
	public void adjustDirection(Squadron sq, SquadronCraft craft) {
		CruiseDirection cd = CraftRotateManager.getInstance().getDirection(craft);
		if(turningMap.getOrDefault(craft, false))
			return;
		if(cd != null && sq.getDirection() != null) {
			if(!cd.equals(sq.getDirection())) {
				MovecraftRotation rotation = subtractDirections(sq.getDirection(), cd);
				turningMap.put(craft, true);
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	craft.setLastRotateTime(0L);
		            	craft.rotate(rotation, craft.getHitBox().getMidPoint(), true);
		            	turningMap.remove(craft);
		            }
		        }.runTaskLater(SquadronsReloaded.getInstance(), SquadronsReloaded.TURNTICKS);
			}
		}
	}
	
	private boolean isHorizontal(CruiseDirection cd) {
		return CruiseDirection.NORTH.equals(cd) ||
				CruiseDirection.EAST.equals(cd) ||
				CruiseDirection.SOUTH.equals(cd) ||
				CruiseDirection.WEST.equals(cd);
	}
	
	private final List<CruiseDirection> directionIds = Arrays.asList(
			CruiseDirection.NORTH,
			CruiseDirection.EAST,
			CruiseDirection.SOUTH,
			CruiseDirection.WEST);
	
	private CruiseDirection rotateDirection(CruiseDirection cd, MovecraftRotation r) {
		int id = directionIds.indexOf(cd);
		if(MovecraftRotation.ANTICLOCKWISE.equals(r))
			id--;
		if(MovecraftRotation.CLOCKWISE.equals(r))
			id++;
		return directionIds.get((id + 4) % 4);
	}
	
	private MovecraftRotation subtractDirections(CruiseDirection a, CruiseDirection b) {
		int aId = directionIds.indexOf(a);
		int bId = directionIds.indexOf(b);
		int subId = (aId - bId + 4) % 4;
		if(subId == 0)
			return MovecraftRotation.NONE;
		if(subId == 1)
			return MovecraftRotation.CLOCKWISE;
		if(subId == 3)
			return MovecraftRotation.ANTICLOCKWISE;
		return (Math.random() < 0.5D)?MovecraftRotation.CLOCKWISE:MovecraftRotation.ANTICLOCKWISE;
	}
	
}
