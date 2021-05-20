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

public class SRCruiseSign implements Listener {

	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        String line = ChatColor.stripColor(sign.getLine(0));
        if(!line.equalsIgnoreCase("Cruise: OFF") && !line.equalsIgnoreCase("Cruise: ON"))
        	return;
        boolean setCruise = line.equalsIgnoreCase("Cruise: OFF");
        String setLine = (setCruise)?("Cruise: ON"):("Cruise: OFF");
        Squadron sq = SquadronManager.getInstance().getSquadron(player);
		if(sq == null)
			return;
		if(sq.getCrafts() == null)
			return;
		if(sq.getCrafts().size() == 0)
			return;
		CruiseDirection cd = null;
		if(setCruise) {
			org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) block.getState().getData();
            if(block.getType() == Material.SIGN_POST)
                cd = CruiseDirection.NONE;
            else
                cd = CruiseDirection.fromBlockFace(materialSign.getFacing());
		}
		for(Craft c : sq.getCrafts()) {
			if (!c.getType().getCanCruise())
                continue;
			if(setCruise) {
				c.setCruiseDirection(cd);
				c.setLastCruiseUpdate(System.currentTimeMillis());
			}
            sign.setLine(0, setLine);
            sign.update(true);
            c.setCruising(setCruise);
            c.resetSigns(sign);
		}
		
    }
	
}
