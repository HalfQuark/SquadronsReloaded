package me.halfquark.squadronsreloaded.listener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredListener;

import me.halfquark.squadronsreloaded.move.CraftTranslateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.config.Settings;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.listener.InteractListener;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.MathUtils;

public class SRInteractListener implements Listener {
	
	private Map<Player, Long> timeMap;
	
	@SuppressWarnings("unchecked")
	public SRInteractListener() {
		HandlerList handlers = PlayerInteractEvent.getHandlerList();
		for(RegisteredListener l : handlers.getRegisteredListeners()) {
            if (!l.getPlugin().isEnabled()) {
                continue;
            }
            if(l.getListener() instanceof InteractListener) {
                InteractListener pl = (InteractListener) l.getListener();
                Class<InteractListener> plclass = InteractListener.class;
                try {
                    Field field = plclass.getDeclaredField("timeMap");
                    field.setAccessible(true);
                    timeMap = (Map<Player, Long>) field.get(pl);
                }
                catch(Exception exception) {
                    exception.printStackTrace();
                    timeMap = new HashMap<>();
                }
                return;
            }
		}
		timeMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractStick(PlayerInteractEvent event) {
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
		if(sq == null)
			return;
		if (event.getItem() == null || event.getItem().getType() != SquadronsReloaded.SQUADRONPILOTTOOL)
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
			
		    //Prevent carrier from moving
		    if(timeMap.containsKey(event.getPlayer())) {
                timeMap.put(event.getPlayer(), System.currentTimeMillis());
            }
		    
		    for(SquadronCraft craft : sq.getCrafts()) {
			    if(CraftTranslateManager.getInstance().isInCooldown(craft))
			    	continue;
			
			    if (!event.getPlayer().hasPermission("movecraft." + craft.getType().getStringProperty(CraftType.NAME) + ".move")) {
			        event.getPlayer().sendMessage(
			                I18nSupport.getInternationalisedString("Insufficient Permissions"));
			        continue;
			    }
			    int dx = 0;
			    int dz = 0;
			    int dy = 0;
			    if (sq.getPilotLocked()) {
			        // right click moves up or down if using direct
			        // control
			        dy = 1;
			        if (event.getPlayer().isSneaking())
			        	dy = -1;
			        if (craft.getType().getBoolProperty(CraftType.GEAR_SHIFTS_AFFECT_DIRECT_MOVEMENT))
			            dy *= craft.getCurrentGear();
			    } else {
				    // Player is onboard craft and right clicking
				
				    float rotation = (float) Math.PI * event.getPlayer().getLocation().getYaw() / 180f;
				
				    float nx = -(float) Math.sin(rotation);
				    float nz = (float) Math.cos(rotation);
				
				    dx = (Math.abs(nx) >= 0.5 ? 1 : 0) * (int) Math.signum(nx);
				    dz = (Math.abs(nz) > 0.5 ? 1 : 0) * (int) Math.signum(nz);
				
				    float p = event.getPlayer().getLocation().getPitch();
				
				    dy = -(Math.abs(p) >= 25 ? 1 : 0) * (int) Math.signum(p);
				
				    if (Math.abs(event.getPlayer().getLocation().getPitch()) >= 75) {
				        dx = 0;
				        dz = 0;
				    }
			    }
			    final int fx = dx, fy = dy, fz = dz;
			    CraftTranslateManager.getInstance().translateCraft(craft, fx, fy, fz);
			    /*new BukkitRunnable() {
					@Override
					public void run() {
						CraftTranslateManager.getInstance().translateCraft(craft, fx, fy, fz);
					}
		        }.runTaskLater(SquadronsReloaded.getInstance(), sq.getCraftRank(craft));*/
		    }
		    return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			sq.setPilotLocked(!sq.getPilotLocked());
			if(sq.getPilotLocked())
				event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Entering Direct Control Mode"));
			else
				event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Leaving Direct Control Mode"));
        }
		
	}	
	
}
