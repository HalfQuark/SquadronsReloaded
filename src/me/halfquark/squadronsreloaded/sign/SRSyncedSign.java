package me.halfquark.squadronsreloaded.sign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;

public class SRSyncedSign implements Listener {
	
	private static ConcurrentMap<Player, Boolean> signCooldown = new ConcurrentHashMap<>();
	
	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
		if(signCooldown.getOrDefault(event.getPlayer(), false))
			return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!Tag.SIGNS.isTagged(block.getType())){
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        String header = ChatColor.stripColor(sign.getLine(0));
        boolean isSynced = false;
        for(String s : SquadronsReloaded.SYNCEDSIGNS) {
        	if(s.equalsIgnoreCase(header))
        		isSynced = true;
        }
        if(!isSynced)
        	return;
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
		if(sq == null)
			return;
        Craft onCraft = null;
		for(Craft c : sq.getCrafts()) {
			if (MathUtils.locationInHitBox(c.getHitBox(), event.getClickedBlock().getLocation())) {
                onCraft = c;
                break;
            }
		}
		if(onCraft == null)
			return;
        
		World w = event.getClickedBlock().getWorld();
		List<String> otherLines = new ArrayList<String>(Arrays.asList(sign.getLines()));
		otherLines.remove(0);
		otherLines.replaceAll(s -> s = ChatColor.stripColor(s).toLowerCase());
		otherLines.removeIf(s -> s.isEmpty());
		signCooldown.put(event.getPlayer(), true);
		Bukkit.broadcastMessage(otherLines.toString());
		for(Craft c : sq.getCrafts()) {
			if(c.equals(onCraft))
				continue;
			LinkedList<MovecraftLocation> foundLocations = new LinkedList<MovecraftLocation>();
	        for (MovecraftLocation tloc : c.getHitBox()) {
	            Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
	            if (!Tag.SIGNS.isTagged(tb.getType())){
	                return;
	            }
	            Sign ts = (Sign) tb.getState();

	            if (isEqualSign(ts, header, otherLines)) {
	            	foundLocations.add(tloc);
	            }
	        }
            if (foundLocations.isEmpty())
                continue;

            for (MovecraftLocation foundLoc : foundLocations) {
                Block newBlock = w.getBlockAt(foundLoc.getX(), foundLoc.getY(), foundLoc.getZ());

                PlayerInteractEvent newEvent = new PlayerInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), newBlock, event.getBlockFace());

                //TODO: DON'T DO THIS
                Bukkit.getServer().getPluginManager().callEvent(newEvent);
            }
		}
		signCooldown.remove(event.getPlayer());
        event.setCancelled(true);
    }
    private boolean isEqualSign(Sign test, String header, List<String> lines) {
    	if(!ChatColor.stripColor(test.getLine(0)).equalsIgnoreCase(header)) {
    		return false;
    	}
    	for(String s : test.getLines()) {
    		if(lines.contains(ChatColor.stripColor(s).toLowerCase())) {
    			return true;
    		}
    	}
        return false;
    }
	
}
