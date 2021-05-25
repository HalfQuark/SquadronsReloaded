package me.halfquark.squadronsreloaded.sign;

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
import net.countercraft.movecraft.Rotation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.MathUtils;

public class SRHelmSign implements Listener {

	@EventHandler
    public final void onSignClick(PlayerInteractEvent event) {
        Rotation rotation;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            rotation = Rotation.CLOCKWISE;
        }else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            rotation = Rotation.ANTICLOCKWISE;
        }else{
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!(ChatColor.stripColor(sign.getLine(0)).equals("\\  ||  /") &&
                ChatColor.stripColor(sign.getLine(1)).equals("==      ==") &&
                ChatColor.stripColor(sign.getLine(2)).equals("/  ||  \\"))) {
            return;
        }
        
        Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer(), true);
		if(sq == null)
			return;
        
		boolean onBoardCraft = false;
	    for(Craft craft : sq.getCrafts()) {
	    	if (MathUtils.locationNearHitBox(craft.getHitBox(),event.getPlayer().getLocation(),2)) {
	    		onBoardCraft = true;
		        break;
		    }
	    }
	    if(!onBoardCraft)
	    	return;
		
	    for(Craft craft : sq.getCrafts()) {
	        if (!event.getPlayer().hasPermission("movecraft." + craft.getType().getCraftName() + ".rotate")) {
	            event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
	            continue;
	        }
	        MovecraftLocation mLoc;
	        if (craft.getType().rotateAtMidpoint()) {
	            mLoc = craft.getHitBox().getMidPoint();
	        } else {
	        	mLoc = MathUtils.bukkit2MovecraftLoc(sign.getLocation());
	        }
	        craft.rotate(rotation, mLoc);
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
