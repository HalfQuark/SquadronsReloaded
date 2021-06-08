package me.halfquark.squadronsreloaded.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;

public class SwitchListener implements Listener {

	/*@EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
        	return;
        if(event.getClickedBlock().getType() != Material.STONE_BUTTON
        && event.getClickedBlock().getType() != Material.WOOD_BUTTON
        && event.getClickedBlock().getType() != Material.LEVER) {
        	return;
        }
        Squadron sq = SquadronManager.getInstance().getSquadron(event.getPlayer());
		if(sq == null)
			return;
		if(sq.getCrafts() == null)
			return;
		if(sq.getCrafts().size() == 0)
			return;
		Location switchLoc = event.getClickedBlock().getLocation();
		Craft pressedCraft = null;
		for(Craft craft : sq.getCrafts()) {
			if(MathUtils.locationInHitBox(craft.getHitBox(), switchLoc)) {
				pressedCraft = craft;
				break;
			}
		}
		if(pressedCraft == null)
			return;
		Bukkit.broadcastMessage("Switch Pressed");
		World w = event.getClickedBlock().getWorld();
		Material switchMaterial = event.getClickedBlock().getType();
		Material switchBlockMaterial = getSwitchBlock(event.getClickedBlock()).getType();
		boolean switchState = getSwitchState(event.getClickedBlock());
		for(Craft craft : sq.getCrafts()) {
			for (MovecraftLocation tloc : craft.getHitBox()) {
				Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
				if(!tb.getType().equals(switchMaterial))
					continue;
				Bukkit.broadcastMessage(getSwitchBlock(tb).getType().toString());
				if(!getSwitchBlock(tb).getType().equals(switchBlockMaterial))
					continue;
				setSwitchState(tb, switchState);
			}
		}
    }*/
	
	@EventHandler
	public void onRedstoneEvent(BlockRedstoneEvent event) {
		if(!Tag.BUTTONS.isTagged(event.getBlock().getType())
        && !(event.getBlock().getType().equals(Material.LEVER))) {
        	return;
        }
		/*if(event.getOldCurrent() <= event.getNewCurrent() || event.getNewCurrent() != 0)
			return;*/
		Location switchLoc = event.getBlock().getLocation();
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
				if(MathUtils.locationInHitBox(craft.getHitBox(), switchLoc)) {
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
        World w = event.getBlock().getWorld();
		Material switchMaterial = event.getBlock().getType();
		BlockData switchBlockData = getSwitchBlock(event.getBlock()).getState().getBlockData();
		boolean switchState = (event.getNewCurrent() != 0);
		for(Craft craft : squad.getCrafts()) {
			for (MovecraftLocation tloc : craft.getHitBox()) {
				Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
				if(!tb.getType().equals(switchMaterial))
					continue;
				if(!getSwitchBlock(tb).getState().getBlockData().equals(switchBlockData))
					continue;
				setSwitchState(tb, switchState);
			}
		}
        
	}
	
	
	private Block getSwitchBlock(Block block) {
		final Switch button = (Switch) block.getState().getBlockData();
		BlockFace face = button.getFacing();
		switch (button.getFace()) {
	    case FLOOR:
	        face = BlockFace.UP;
	        break;
	    case CEILING:
	        face = BlockFace.DOWN;
	        break;
		default:
			break;
	    }
		Block behind = block.getRelative(face.getOppositeFace());
		return behind;
	}
	
	// Use event.getNewPower() instead
	/*private boolean getSwitchState(Block block) {
		SimpleAttachableMaterialData s = (SimpleAttachableMaterialData) block.getState().getData();
		if(s instanceof Button) {
			Button button = (Button) s;
			return button.isPowered();
		}
		if(s instanceof Lever) {
			Lever lever = (Lever) s;
			return lever.isPowered();
		}
		return false;
	}*/
	
	private void setSwitchState(Block block, boolean state) {
		BlockState bs = block.getState();
		Switch s = (Switch) bs.getBlockData();
		s.setPowered(state);
		bs.setBlockData(s);
		bs.update();
	}
	
}
