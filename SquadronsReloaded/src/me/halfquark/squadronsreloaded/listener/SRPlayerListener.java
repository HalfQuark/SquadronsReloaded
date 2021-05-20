package me.halfquark.squadronsreloaded.listener;

import java.lang.reflect.Field;
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
import net.countercraft.movecraft.utils.MathUtils;

public class SRPlayerListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer());
		if(sq == null)
			return;
		if(sq.getCarrier() == null) {
			return;
		}
	    Craft onBoardCraft = null;
	    for(Craft craft : sq.getCrafts()) {
	    	if (MathUtils.locationNearHitBox(craft.getHitBox(),event.getPlayer().getLocation(),2)) {
	    		onBoardCraft = craft;
		        break;
		    }
	    }
	    if(onBoardCraft == null)
	    	return;
		
		Craft playerCraft = sq.getCarrier();
		CraftManager.getInstance().addOverboard(event.getPlayer());
        if(playerCraft != null) {
            HandlerList handlers = event.getHandlers();
            RegisteredListener[] listeners = handlers.getRegisteredListeners();
            for (RegisteredListener l : listeners) {
                if (!l.getPlugin().isEnabled()) {
                    continue;
                }
                if(l.getListener() instanceof PlayerListener) {
                    PlayerListener pl = (PlayerListener) l.getListener();
                    Class<PlayerListener> plclass = PlayerListener.class;
                    try {
                        Field field = plclass.getDeclaredField("timeToReleaseAfter");
                        field.setAccessible(true);
                        @SuppressWarnings("unchecked")
						final Map<Craft, Long> timeToReleaseAfter = (Map<Craft, Long>) field.get(pl);
                        if(timeToReleaseAfter.containsKey(playerCraft)) {
                            timeToReleaseAfter.put(playerCraft, System.currentTimeMillis() + SquadronsReloaded.MANOVERBOARDTIME);
                        }
                    }
                    catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
	}
	
}
