package me.halfquark.squadronsreloaded.sign;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.MovecraftRotation;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.MathUtils;

public class SRHelmSign implements Listener {

	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
        MovecraftRotation rotation;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            rotation = MovecraftRotation.CLOCKWISE;
        }else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            rotation = MovecraftRotation.ANTICLOCKWISE;
        }else{
            return;
        }
        Block block = event.getClickedBlock();
        if (!Tag.SIGNS.isTagged(block.getType())){
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!(ChatColor.stripColor(sign.getLine(0)).equals("\\  ||  /") &&
                ChatColor.stripColor(sign.getLine(1)).equals("==      ==") &&
                ChatColor.stripColor(sign.getLine(2)).equals("/  ||  \\"))) {
            return;
        }
        
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
		if(sq == null)
			return;
        
		boolean onBoardCraft = false;
	    for(SquadronCraft craft : sq.getCrafts()) {
	    	if (MathUtils.locationNearHitBox(craft.getHitBox(),event.getPlayer().getLocation(),2)) {
	    		onBoardCraft = true;
		        break;
		    }
	    }
	    if(!onBoardCraft)
	    	return;
		
	    for(SquadronCraft craft : sq.getCrafts()) {
	        if (!event.getPlayer().hasPermission("movecraft." + craft.getType().getStringProperty(CraftType.NAME) + ".rotate")) {
	            event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
	            continue;
	        }
	        MovecraftLocation mLoc;
	        if (craft.getType().getBoolProperty(CraftType.ROTATE_AT_MIDPOINT)) {
	            mLoc = craft.getHitBox().getMidPoint();
	        } else {
	        	mLoc = MathUtils.bukkit2MovecraftLoc(sign.getLocation());
	        }
	        craft.rotate(rotation, mLoc, true);
	        /*new BukkitRunnable() {
				@Override
				public void run() {
					craft.rotate(rotation, mLoc);
				}
	        }.runTaskLater(SquadronsReloaded.getInstance(), sq.getCraftRank(craft));*/
	    }
	    
        event.setCancelled(true);

    }
	
}
