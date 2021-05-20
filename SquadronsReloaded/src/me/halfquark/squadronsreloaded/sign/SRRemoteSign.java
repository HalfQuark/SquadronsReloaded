package me.halfquark.squadronsreloaded.sign;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.config.Settings;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;
import net.countercraft.movecraft.utils.MathUtils;

public class SRRemoteSign implements Listener {

	private static final String HEADER = "Remote Sign";
	
	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(HEADER)) {
            return;
        }
        
        Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer());
		if(sq == null)
			return;
		if(sq.getCrafts() == null)
			return;
		if(sq.getCrafts().size() == 0)
			return;
        boolean locationInCraft = false;
		for(Craft c : sq.getCrafts()) {
			if (MathUtils.locationInHitBox(c.getHitBox(), event.getClickedBlock().getLocation())) {
                locationInCraft = true;
                break;
            }
		}
		if(!locationInCraft)
			return;
        
		String targetText = ChatColor.stripColor(sign.getLine(1));
		
		if(targetText.equalsIgnoreCase(HEADER))
            return;

        if(targetText.equalsIgnoreCase(""))
            return;
		
		for(Craft c : sq.getCrafts()) {
			if (!c.getType().allowRemoteSign()) {
	            event.getPlayer().sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Remote Sign - Not allowed on this craft"));
	            continue;
	        }
			LinkedList<MovecraftLocation> foundLocations = new LinkedList<MovecraftLocation>();
	        boolean firstError = true;
	        for (MovecraftLocation tloc : c.getHitBox()) {
	            Block tb = event.getClickedBlock().getWorld().getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
	            if (!tb.getType().equals(Material.SIGN_POST) && !tb.getType().equals(Material.WALL_SIGN)) {
	                continue;
	            }
	            Sign ts = (Sign) tb.getState();

	            if (isEqualSign(ts, targetText)) {
	                if (isForbidden(ts)) {
	                    if (firstError) {
	                        event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Remote Sign - Forbidden string found"));
	                        firstError = false;
	                    }
	                    event.getPlayer().sendMessage(" - ".concat(tloc.toString()).concat(" : ").concat(ts.getLine(0)));
	                } else {
	                    foundLocations.add(tloc);
	                }
	            }
	        }
            if (!firstError) {
                continue;
            }
            else if (foundLocations.isEmpty()) {
                event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Remote Sign - Could not find target sign"));
              	continue;
            }

            if (Settings.MaxRemoteSigns > -1) {
                int foundLocCount = foundLocations.size();
                if(foundLocCount > Settings.MaxRemoteSigns) {
                    event.getPlayer().sendMessage(String.format(I18nSupport.getInternationalisedString("Remote Sign - Exceeding maximum allowed"), foundLocCount, Settings.MaxRemoteSigns));
                    continue;
                }
            }

            for (MovecraftLocation foundLoc : foundLocations) {
                Block newBlock = event.getClickedBlock().getWorld().getBlockAt(foundLoc.getX(), foundLoc.getY(), foundLoc.getZ());

                PlayerInteractEvent newEvent = new PlayerInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), newBlock, event.getBlockFace());

                //TODO: DON'T DO THIS
                Bukkit.getServer().getPluginManager().callEvent(newEvent);
            }
		}
        
        event.setCancelled(true);
    }
    private boolean isEqualSign(Sign test, String target) {
        return !ChatColor.stripColor(test.getLine(0)).equalsIgnoreCase(HEADER) && ( ChatColor.stripColor(test.getLine(0)).equalsIgnoreCase(target)
                || ChatColor.stripColor(test.getLine(1)).equalsIgnoreCase(target)
                || ChatColor.stripColor(test.getLine(2)).equalsIgnoreCase(target)
                || ChatColor.stripColor(test.getLine(3)).equalsIgnoreCase(target) );
    }
    private boolean isForbidden(Sign test) {
        for (int i = 0; i < 4; i++) {
            String t = test.getLine(i).toLowerCase();
            if(Settings.ForbiddenRemoteSigns.contains(t))
                return true;
        }
        return false;
    }
	
}
