package me.halfquark.squadronsreloaded.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Comparator;
import org.bukkit.material.Diode;
import org.bukkit.material.MaterialData;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.MathUtils;

public class RedstoneComponentListener implements Listener {
	
	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(event.getClickedBlock().getType() != Material.DIODE_BLOCK_OFF
        && event.getClickedBlock().getType() != Material.DIODE_BLOCK_ON
        && event.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_ON
        && event.getClickedBlock().getType() != Material.REDSTONE_COMPARATOR_OFF) {
        	return;
        }
		Location compLoc = event.getClickedBlock().getLocation();
		Craft pressedCraft = null;
        Squadron squad = null;
        for(Squadron sq : SquadronManager.getInstance().getSquadronList()) {
			if(sq == null)
				continue;
			if(sq.getCrafts() == null)
				continue;
			if(sq.getCrafts().size() == 0)
				continue;
			for(Craft craft : sq.getCrafts()) {
				if(MathUtils.locationInHitBox(craft.getHitBox(), compLoc)) {
					pressedCraft = craft;
					break;
				}
			}
			if(pressedCraft != null) {
				squad = sq;
				break;
			}
		}
        if(squad == null)
        	return;
        World w = event.getClickedBlock().getWorld();
		Material compMaterial = event.getClickedBlock().getType();
		MaterialData compBlockData = getCompBlock(event.getClickedBlock()).getState().getData();
		int compState = getCompState(event.getClickedBlock());
		for(Craft craft : squad.getCrafts()) {
			for (MovecraftLocation tloc : craft.getHitBox()) {
				Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
				if(tb.equals(event.getClickedBlock()))
					continue;
				if(!tb.getType().equals(compMaterial))
					continue;
				if(!getCompBlock(tb).getState().getData().equals(compBlockData))
					continue;
				setCompState(tb, compState + 1);
			}
		}
        
	}
	
	
	private Block getCompBlock(Block block) {
		return block.getRelative(BlockFace.DOWN);
	}
	
	private int getCompState(Block block) {
		MaterialData md = block.getState().getData();
		if(md instanceof Diode) {
			return ((Diode) md).getDelay();
		}
		if(md instanceof Comparator) {
			if(((Comparator) md).isSubtractionMode())
				return 1;
			return 0;
		}
		return -1;
	}
	
	private void setCompState(Block block, int state) {
		BlockState bs = block.getState();
		MaterialData md = block.getState().getData();
		if(md instanceof Diode) {
			Diode diode = (Diode) md;
			diode.setDelay(1 + (state-1)%4);
			bs.setData(diode);
			bs.update();
			return;
		}
		if(md instanceof Comparator) {
			Comparator comparator = (Comparator) md;
			comparator.setSubtractionMode(state % 2 == 1);
			bs.setData(comparator);
			bs.update();
			return;
		}
	}
	
}
