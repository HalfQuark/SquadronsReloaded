package me.halfquark.squadronsreloaded.sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.ManOverboardEvent;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.ChatUtils;

public class SRLeadSign implements Listener {

	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
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
        if(!line.equalsIgnoreCase("SquadronLead"))
        	return;
        
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
        if(sq == null) {
        	player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
        }
		Craft leadCraft = sq.getLeadCraft();
		Location telPoint = getCraftTeleportPoint(leadCraft);
        
        ManOverboardEvent moEvent = new ManOverboardEvent(leadCraft, telPoint);
        //For some reason this commits sepuko
        //Bukkit.getServer().getPluginManager().callEvent(event);

        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0);
        player.teleport(moEvent.getLocation());
        event.setCancelled(true);
        return;
    }
	
	private Location getCraftTeleportPoint(Craft craft) {
        double telX = (craft.getHitBox().getMinX() + craft.getHitBox().getMaxX())/2D + 0.5D;
        double telZ = (craft.getHitBox().getMinZ() + craft.getHitBox().getMaxZ())/2D + 0.5D;
        double telY = craft.getHitBox().getMaxY() + 1;
        return new Location(craft.getWorld(), telX, telY, telZ);
    }
	
}
