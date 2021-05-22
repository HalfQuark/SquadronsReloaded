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

import me.halfquark.squadronsreloaded.formation.Formation;
import me.halfquark.squadronsreloaded.formation.FormationManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;

public class SRFormationSign implements Listener {

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
        if(!line.equalsIgnoreCase("Formation"))
        	return;
        
        Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer());
        if(sq == null) {
        	player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
        }
		Craft leadCraft = sq.getLeadCraft();
		if(leadCraft == null) {
			player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
		}
		
		Formation formation = FormationManager.getInstance().getFormation(ChatColor.stripColor(sign.getLine(1)));
		if(formation == null) {
			player.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid formation"));
            return;
		}
		int spacing;
		try {
			spacing = Integer.valueOf(ChatColor.stripColor(sign.getLine(2)));
		} catch(NumberFormatException e) {
			player.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid integer"));
            return;
		}
		if(spacing < formation.getMinSpacing() || formation.getMaxSpacing() < spacing) {
			player.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Spacing has to be in range")
			+ " [" + String.valueOf(formation.getMinSpacing()) + "," + String.valueOf(formation.getMaxSpacing()) + "]");
            return;
		}
		sq.formationOn(formation, spacing);
		player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Formation")
		+ " " + formation.getName() + " " + spacing);
        event.setCancelled(true);
        return;
    }
	
}
