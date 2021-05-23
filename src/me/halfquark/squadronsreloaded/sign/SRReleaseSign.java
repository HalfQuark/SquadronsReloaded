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
import net.countercraft.movecraft.events.CraftReleaseEvent.Reason;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;

public class SRReleaseSign implements Listener {

	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
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
        if(!line.equalsIgnoreCase("SquadronRelease"))
        	return;
        
        Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer(), true);
        if(sq == null) {
        	player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
        }
        sq.releaseAll(Reason.PLAYER);
		SquadronManager.getInstance().removeSquadron(event.getPlayer());
		event.getPlayer().sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Squadron has been released"));
        event.setCancelled(true);
        return;
    }
	
}
