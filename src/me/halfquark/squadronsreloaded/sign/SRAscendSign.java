package me.halfquark.squadronsreloaded.sign;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.craft.Craft;

public class SRAscendSign implements Listener {

	@EventHandler
    public void onSignClickEvent(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN){
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        String line = ChatColor.stripColor(sign.getLine(0));
        if(!line.equalsIgnoreCase("Ascend: OFF") && !line.equalsIgnoreCase("Ascend: ON"))
        	return;
        boolean setAscend = line.equalsIgnoreCase("Ascend: OFF");
        String setLine = (setAscend)?("Ascend: ON"):("Ascend: OFF");
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(player, true);
		if(sq == null)
			return;
        
		for(Craft c : sq.getCrafts()) {
			if (c == null || !c.getType().getCanCruise()) {
	            continue;
	        }
			if(setAscend) {
				c.setCruiseDirection(CruiseDirection.UP);
	            c.setLastCruiseUpdate(System.currentTimeMillis());
			}
	        sign.setLine(0, setLine);
	        sign.update(true);

	        c.setCruising(setAscend);
	        c.resetSigns(sign);
		}

    }
	
}
