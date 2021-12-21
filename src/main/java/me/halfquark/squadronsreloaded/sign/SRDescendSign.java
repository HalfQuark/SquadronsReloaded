package me.halfquark.squadronsreloaded.sign;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.CruiseDirection;

public class SRDescendSign implements Listener {

	@EventHandler
    public void onSignClickEvent(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!Tag.SIGNS.isTagged(block.getType())){
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        String line = ChatColor.stripColor(sign.getLine(0));
        if(!line.equalsIgnoreCase("Descend: OFF") && !line.equalsIgnoreCase("Descend: ON"))
        	return;
        boolean setDescend = line.equalsIgnoreCase("Descend: OFF");
        String setLine = (setDescend)?("Descend: ON"):("Descend: OFF");
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(player, true);
		if(sq == null)
			return;
		if(setDescend) {
			sq.setCruiseDirection(CruiseDirection.DOWN);
		}
		sq.setCruising(setDescend);
		for(SquadronCraft c : sq.getCrafts()) {
			if (c == null || !c.getType().getBoolProperty(CraftType.CAN_CRUISE)) {
	            continue;
	        }
			if(setDescend) {
	            c.setLastCruiseUpdate(System.currentTimeMillis());
			}
	        sign.setLine(0, setLine);
	        sign.update(true);

	        c.resetSigns(sign);
		}

    }
	
}
