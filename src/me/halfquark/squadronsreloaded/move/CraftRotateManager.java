package me.halfquark.squadronsreloaded.move;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.Rotation;
import net.countercraft.movecraft.craft.Craft;

public class CraftRotateManager {
	
	private static CraftRotateManager inst;
	private static final Map<Craft, CruiseDirection> directionMap = new HashMap<>();
	
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
	
	public void registerRotation(Craft craft, Rotation r) {
		CruiseDirection cd = getDirection(craft);
		if(cd == null)
			return;
		directionMap.put(craft, rotateDirection(cd, r));
	}
	
	@Nullable
	public CruiseDirection getDirection(Craft craft) {return directionMap.get(craft);}
	public void setDirection(Craft craft, CruiseDirection cd) {directionMap.put(craft, cd);}
	
	public void adjustDirection(Squadron sq, Craft craft) {
		CruiseDirection cd = CraftRotateManager.getInstance().getDirection(craft);
		if(cd != null && sq.getDirection() != null) {
			if(!cd.equals(sq.getDirection())) {
				Rotation rotation = subtractDirections(sq.getDirection(), cd);
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	craft.setLastRotateTime(0L);
		            	craft.rotate(rotation, craft.getHitBox().getMidPoint());
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
	
	private CruiseDirection rotateDirection(CruiseDirection cd, Rotation r) {
		int id = directionIds.indexOf(cd);
		if(Rotation.ANTICLOCKWISE.equals(r))
			id--;
		if(Rotation.CLOCKWISE.equals(r))
			id++;
		return directionIds.get((id + 4) % 4);
	}
	
	private Rotation subtractDirections(CruiseDirection a, CruiseDirection b) {
		int aId = directionIds.indexOf(a);
		int bId = directionIds.indexOf(b);
		int subId = (aId - bId + 4) % 4;
		if(subId == 0)
			return Rotation.NONE;
		if(subId == 1)
			return Rotation.CLOCKWISE;
		if(subId == 3)
			return Rotation.ANTICLOCKWISE;
		return (Math.random() < 0.5D)?Rotation.CLOCKWISE:Rotation.ANTICLOCKWISE;
	}
	
}
