package me.halfquark.squadronsreloaded.move;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.BitmapHitBox;

public class CraftDirectionDetection {
	
	@Nullable
	public static CruiseDirection detect(Craft craft) {
		World w = craft.getW();
		BitmapHitBox hitbox = craft.getHitBox();
		CruiseDirection cd = null;
		for(MovecraftLocation mLoc : hitbox.asSet()) {
			Block block = w.getBlockAt(mLoc.toBukkit(w));
			if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)
	            continue;
	        Sign sign = (Sign) block.getState();
	        String line = sign.getLine(0);
	        if(!line.equalsIgnoreCase("Cruise: OFF") && !line.equalsIgnoreCase("Cruise: ON"))
	        	continue;
	        org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) block.getState().getData();
            if(block.getType() == Material.SIGN_POST)
                continue;
            if(cd != null) {
            	if(!cd.equals(CruiseDirection.fromBlockFace(materialSign.getFacing()))){
            		return null;
            	}
            } else {
            	cd = CruiseDirection.fromBlockFace(materialSign.getFacing());
            }
		}
		if(cd == null)
			return CruiseDirection.NONE;
		return cd;
	}
	
}
