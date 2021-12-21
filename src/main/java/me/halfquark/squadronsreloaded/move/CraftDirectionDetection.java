package me.halfquark.squadronsreloaded.move;

import javax.annotation.Nullable;

import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.hitboxes.HitBox;

public class CraftDirectionDetection {
	
	@Nullable
	public static CruiseDirection detect(Craft craft) {
		World w = craft.getWorld();
		HitBox hitbox = craft.getHitBox();
		CruiseDirection cd = null;
		for(MovecraftLocation mLoc : hitbox.asSet()) {
			Block block = w.getBlockAt(mLoc.toBukkit(w));
			if (!Tag.SIGNS.isTagged(block.getType()))
	            continue;
	        Sign sign = (Sign) block.getState();
	        String line = sign.getLine(0);
	        if(!line.equalsIgnoreCase("Cruise: OFF") && !line.equalsIgnoreCase("Cruise: ON"))
	        	continue;
            if(!(sign.getBlockData() instanceof WallSign))
                continue;
            if(cd != null) {
            	if(!cd.equals(CruiseDirection.fromBlockFace(((WallSign) sign.getBlockData()).getFacing()))){
            		return null;
            	}
            } else {
            	cd = CruiseDirection.fromBlockFace(((WallSign) sign.getBlockData()).getFacing());
            }
		}
		if(cd == null)
			return CruiseDirection.NONE;
		return cd;
	}
	
}
