package me.halfquark.squadronsreloaded.listener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredListener;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.listener.PlayerListener;
import net.countercraft.movecraft.util.MathUtils;

public class SRPlayerListener implements Listener {
	
	private Map<Craft, Long> timeToReleaseAfter;
	
	@SuppressWarnings("unchecked")
	public SRPlayerListener() {
		HandlerList handlers = PlayerMoveEvent.getHandlerList();
		for(RegisteredListener l : handlers.getRegisteredListeners()) {
            if (!l.getPlugin().isEnabled()) {
                continue;
            }
            if(l.getListener() instanceof PlayerListener) {
                PlayerListener pl = (PlayerListener) l.getListener();
                Class<PlayerListener> plclass = PlayerListener.class;
                try {
                    Field field = plclass.getDeclaredField("timeToReleaseAfter");
                    field.setAccessible(true);
                    timeToReleaseAfter = (Map<Craft, Long>) field.get(pl);
                }
                catch(Exception exception) {
                    exception.printStackTrace();
                    timeToReleaseAfter = new HashMap<>();
                }
                return;
            }
		}
		timeToReleaseAfter = new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
		if(sq == null)
			return;
		if(sq.getCarrier() == null) {
			return;
		}
	    Craft onBoardCraft = null;
	    for(Craft craft : sq.getCrafts()) {
	    	if (MathUtils.locationNearHitBox(craft.getHitBox(), event.getPlayer().getLocation(),2)) {
	    		onBoardCraft = craft;
		        break;
		    }
	    }
	    if(onBoardCraft == null)
	    	return;
		
		Craft playerCraft = sq.getCarrier();
		CraftManager.getInstance().addOverboard(event.getPlayer());
        if(playerCraft != null) {
        	if(timeToReleaseAfter.containsKey(playerCraft)) {
                timeToReleaseAfter.put(playerCraft, System.currentTimeMillis() + SquadronsReloaded.MANOVERBOARDTIME);
            }
        }
	}
	
}
