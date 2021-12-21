package me.halfquark.squadronsreloaded.sign;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;

public class SRCruiseSign implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
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
        if(!line.equalsIgnoreCase("Cruise: OFF") && !line.equalsIgnoreCase("Cruise: ON"))
        	return;
        boolean setCruise = line.equalsIgnoreCase("Cruise: OFF");
        String setLine = (setCruise)?("Cruise: ON"):("Cruise: OFF");
        Squadron sq = SquadronManager.getInstance().getPlayerSquadron(player, true);
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
	    sign.setLine(0, "");
	    sign.update(true);
	    sign.setLine(0, setLine);
		CruiseDirection cd = null;
		if(setCruise) {
			if(sign.getBlockData() instanceof WallSign) {
                cd = CruiseDirection.fromBlockFace(((WallSign) sign.getBlockData()).getFacing());
            } else {
                cd = CruiseDirection.NONE;
            }
		}
		if(setCruise) {
			sq.setCruiseDirection(cd);
			for(SquadronCraft c : sq.getCrafts())
				c.setLastCruiseUpdate(System.currentTimeMillis());
		}
        sq.setCruising(setCruise);
        for(SquadronCraft c : sq.getCrafts())
        	c.resetSigns(sign);
		new BukkitRunnable() {
            @Override
            public void run() {
                sign.update(true);
            }
        }.runTaskLater(SquadronsReloaded.getInstance(), 1);
    }
	
}
