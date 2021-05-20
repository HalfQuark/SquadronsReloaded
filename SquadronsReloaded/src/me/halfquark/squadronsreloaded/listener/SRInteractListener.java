package me.halfquark.squadronsreloaded.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.config.Settings;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftType;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.MathUtils;

public class SRInteractListener implements Listener {

	private static final Map<Craft, Long> timeMap = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractStick(PlayerInteractEvent event) {
		Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer());
		if(sq == null)
			return;
		if(sq.getCrafts() == null)
			return;
		if(sq.getCrafts().size() == 0)
			return;
		Craft carrier = sq.getCarrier();
		if(carrier == null)
			return;
		
		if (event.getItem() == null || event.getItem().getTypeId() != Settings.PilotTool)
	        return;
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.setCancelled(true);
		    
			Craft onBoardCraft = null;
		    for(Craft craft : sq.getCrafts()) {
		    	if (MathUtils.locationNearHitBox(craft.getHitBox(),event.getPlayer().getLocation(),2)) {
		    		onBoardCraft = craft;
			        break;
			    }
		    }
		    if(onBoardCraft == null)
		    	return;
			
		    for(Craft craft : sq.getCrafts()) {
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
			
			
			        if (Math.abs(ticksElapsed) < tickCooldown) {
			            continue;
			        }
			    }
			
			    if (!event.getPlayer().hasPermission("movecraft." + craft.getType().getCraftName() + ".move")) {
			        event.getPlayer().sendMessage(
			                I18nSupport.getInternationalisedString("Insufficient Permissions"));
			        continue;
			    }
			    if (craft.getPilotLocked()) {
			        // right click moves up or down if using direct
			        // control
			        int DY = 1;
			        if (event.getPlayer().isSneaking())
			            DY = -1;
			        if (craft.getType().getGearShiftsAffectDirectMovement())
			            DY *= currentGear;
			        craft.translate(0, DY, 0);
			        timeMap.put(craft, System.currentTimeMillis());
			        craft.setLastCruiseUpdate(System.currentTimeMillis());
			        continue;
			    }
			    // Player is onboard craft and right clicking
			
			    float rotation = (float) Math.PI * event.getPlayer().getLocation().getYaw() / 180f;
			
			    float nx = -(float) Math.sin(rotation);
			    float nz = (float) Math.cos(rotation);
			
			    int dx = (Math.abs(nx) >= 0.5 ? 1 : 0) * (int) Math.signum(nx);
			    int dz = (Math.abs(nz) > 0.5 ? 1 : 0) * (int) Math.signum(nz);
			    int dy;
			
			    float p = event.getPlayer().getLocation().getPitch();
			
			    dy = -(Math.abs(p) >= 25 ? 1 : 0) * (int) Math.signum(p);
			
			    if (Math.abs(event.getPlayer().getLocation().getPitch()) >= 75) {
			        dx = 0;
			        dz = 0;
			    }
			
			    craft.translate(dx, dy, dz);
			    timeMap.put(craft, System.currentTimeMillis());
			    craft.setLastCruiseUpdate(System.currentTimeMillis());
		    }
		    return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() == null || event.getItem().getTypeId() != Settings.PilotTool) {
                return;
            }
            for(Craft craft : sq.getCrafts()) {
	            if (craft == null) {
	                return;
	            }
	            if (!event.getPlayer().hasPermission("movecraft." + craft.getType().getCraftName() + ".move")
	                    || !craft.getType().getCanDirectControl()) {
	                        event.getPlayer().sendMessage(
	                                I18nSupport.getInternationalisedString("Insufficient Permissions"));
	                        return;
	            }
	            craft.setPilotLocked(carrier.getPilotLocked());
            }
            return;
        }
		
	}	
	
}
