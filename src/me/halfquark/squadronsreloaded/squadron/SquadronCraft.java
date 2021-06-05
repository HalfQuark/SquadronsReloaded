package me.halfquark.squadronsreloaded.squadron;

import javax.annotation.Nonnull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import net.countercraft.movecraft.craft.BaseCraft;
import net.countercraft.movecraft.craft.CraftType;

public class SquadronCraft extends BaseCraft {
	
	private Player pilot;
	private Squadron squadron;
	
	public SquadronCraft(@Nonnull CraftType type, @Nonnull World world, @Nonnull Player p, @Nonnull Squadron sq) {
		super(type, world);
		pilot = p;
		squadron = sq;
	}
	
	public Player getSquadronPilot() {return pilot;}
	public Squadron getSquadron() {return squadron;}
	
	public boolean isLead() {
		return squadron.getLeadCraft().equals(this);
	}
	
}
