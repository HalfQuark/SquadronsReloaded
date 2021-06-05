package me.halfquark.squadronsreloaded.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Comparator.Mode;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;

public class RedstoneComponentListener implements Listener {
	
	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(event.getClickedBlock().getType() != Material.REPEATER
        && event.getClickedBlock().getType() != Material.COMPARATOR) {
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
		BlockData compBlockData = getCompBlock(event.getClickedBlock()).getState().getBlockData();
		int compState = getCompState(event.getClickedBlock());
		for(SquadronCraft craft : squad.getCrafts()) {
			for (MovecraftLocation tloc : craft.getHitBox()) {
				Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
				if(tb.equals(event.getClickedBlock()))
					continue;
				if(!tb.getType().equals(compMaterial))
					continue;
				if(!getCompBlock(tb).getState().getBlockData().equals(compBlockData))
					continue;
				setCompState(tb, compState + 1);
			}
		}
        
	}
	
	
	private Block getCompBlock(Block block) {
		return block.getRelative(BlockFace.DOWN);
	}
	
	private int getCompState(Block block) {
		BlockData md = block.getState().getBlockData();
		if(md instanceof Repeater) {
			return ((Repeater) md).getDelay();
		}
		if(md instanceof Comparator) {
			if(((Comparator) md).getMode().equals(Mode.SUBTRACT))
				return 1;
			return 0;
		}
		return -1;
	}
	
	private void setCompState(Block block, int state) {
		BlockState bs = block.getState();
		BlockData md = block.getState().getBlockData();
		if(md instanceof Repeater) {
			Repeater diode = (Repeater) md;
			diode.setDelay(1 + (state-1)%4);
			bs.setBlockData(diode);
			bs.update();
			return;
		}
		if(md instanceof Comparator) {
			Comparator comparator = (Comparator) md;
			comparator.setMode((state % 2 == 1)?Mode.SUBTRACT:Mode.COMPARE);
			bs.setBlockData(comparator);
			bs.update();
			return;
		}
	}
	
}
