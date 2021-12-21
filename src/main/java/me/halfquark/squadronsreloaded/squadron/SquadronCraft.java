package me.halfquark.squadronsreloaded.squadron;

import javax.annotation.Nonnull;

import net.countercraft.movecraft.craft.Craft;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.countercraft.movecraft.craft.BaseCraft;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.craft.type.CraftType;
import org.jetbrains.annotations.NotNull;

public class SquadronCraft extends BaseCraft implements SubCraft {
	
	private Player pilot;
	private Squadron squadron;
	
	public SquadronCraft(@Nonnull CraftType type, @Nonnull World world, @Nonnull Player p, @Nonnull Squadron sq) {
		super(type, world);
		pilot = p;
		squadron = sq;
	}

	@Override
	@NotNull
	public Craft getParent() {
		return squadron.getCarrier();
	}

	@Override
	public void setParent(@NotNull Craft parent) {
		return;
	}
	
	public Player getSquadronPilot() {return pilot;}
	public Squadron getSquadron() {return squadron;}
	
	public boolean isLead() {
		return squadron.getLeadCraft().equals(this);
	}
	
}
