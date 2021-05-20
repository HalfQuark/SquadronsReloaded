package me.halfquark.squadronsreloaded.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SimpleAttachableMaterialData;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.MathUtils;

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
		if(event.getBlock().getType() != Material.STONE_BUTTON
        && event.getBlock().getType() != Material.WOOD_BUTTON
        && event.getBlock().getType() != Material.LEVER) {
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
		MaterialData switchBlockData = getSwitchBlock(event.getBlock()).getState().getData();
		boolean switchState = (event.getNewCurrent() != 0);
		for(Craft craft : squad.getCrafts()) {
			for (MovecraftLocation tloc : craft.getHitBox()) {
				Block tb = w.getBlockAt(tloc.getX(), tloc.getY(), tloc.getZ());
				if(!tb.getType().equals(switchMaterial))
					continue;
				if(!getSwitchBlock(tb).getState().getData().equals(switchBlockData))
					continue;
				setSwitchState(tb, switchState);
			}
		}
        
	}
	
	
	private Block getSwitchBlock(Block block) {
		return block.getRelative(((SimpleAttachableMaterialData) block.getState().getData()).getAttachedFace());
	}
	
	private boolean getSwitchState(Block block) {
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
	}
	
	private void setSwitchState(Block block, boolean state) {
		BlockState bs = block.getState();
		SimpleAttachableMaterialData s = (SimpleAttachableMaterialData) bs.getData();
		if(s instanceof Button) {
			Button button = (Button) s;
			button.setPowered(state);
			bs.setData(button);
			bs.update();
			return;
		}
		if(s instanceof Lever) {
			Lever lever = (Lever) s;
			lever.setPowered(state);
			bs.setData(lever);
			bs.update();
			return;
		}
		return;
	}
	
}
