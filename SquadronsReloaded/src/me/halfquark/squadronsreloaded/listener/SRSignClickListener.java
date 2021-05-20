package me.halfquark.squadronsreloaded.listener;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.CraftType;
import net.countercraft.movecraft.craft.ICraft;
import net.countercraft.movecraft.events.CraftReleaseEvent.Reason;
import net.countercraft.movecraft.localisation.I18nSupport;

public class SRSignClickListener implements Listener {

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        CraftType type = CraftManager.getInstance().getCraftTypeFromString(ChatColor.stripColor(sign.getLine(0)));
        if (type == null) {
            return;
        }
        if(!SquadronsReloaded.CARRIEDTYPES.contains(type.getCraftName()))
        	return;
        Craft pilotedCraft = CraftManager.getInstance().getCraftByPlayer(event.getPlayer());
        if(pilotedCraft == null)
        	return;
        if(!SquadronsReloaded.CARRIERTYPES.contains(pilotedCraft.getType().getCraftName()))
        	return;
        // Valid sign prompt for ship command.
        if (!event.getPlayer().hasPermission("movecraft." + ChatColor.stripColor(sign.getLine(0)) + ".pilot")) {
            event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return;
        }
        if (!event.getPlayer().hasPermission("movecraft.squadron.pilot")) {
            event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return;
        }
        Player p = event.getPlayer();
        if(!SquadronManager.getInstance().hasSquadron(p))
        	SquadronManager.getInstance().putSquadron(p, new Squadron(p));
        Squadron squadron = SquadronManager.getInstance().getSquadron(p);
        // Attempt to run detection
        Location loc = event.getClickedBlock().getLocation();
        MovecraftLocation startPoint = new MovecraftLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        final Craft c = new ICraft(type, loc.getWorld());

        if (c.getType().getCruiseOnPilot())
        	return;
        c.detect(null, event.getPlayer(), startPoint);
        event.setCancelled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
            	if(c.getHitBox().size() == 0) {
            		CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
            		return;
            	}
            	Craft carrier;
            	if (squadron.getCarrier() == null) {
            		carrier = CraftManager.getInstance().getCraftByPlayer(event.getPlayer());
            		if(carrier == null) {
            			event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(
                                "Squadrons - Needs to be carried"));
                        CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                        return;
            		}
            		if(!SquadronsReloaded.CARRIERTYPES.contains(carrier.getType().getCraftName())) {
            			event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(
                                "Squadrons - Needs to be carried"));
                        CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                        return;
            		}
            		squadron.setCarrier(carrier);
            	} else {
            		carrier = squadron.getCarrier();
            	}
            	
            	Set<Craft> craftsInWorld = CraftManager.getInstance().getCraftsInWorld(c.getW());

                boolean isSubcraft = false;

                if (c.getType().getCruiseOnPilot() || p != null) {
                    for (Craft craft : craftsInWorld) {
                        if (craft.getHitBox().intersection(c.getHitBox()).isEmpty()) {
                            continue;
                        }
                        if(craft.equals(c))
                        	continue;
                        if (craft.getType() == c.getType()
                                || craft.getHitBox().size() <= c.getHitBox().size()) {
                            event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(
                                    "Squadrons - Failed Craft is already being controlled"));
                            CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                            return;
                        }
                        if (!craft.isNotProcessing()) {
                        	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Parent Craft is busy"));
                        	CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                        	return;
                        }
                        if(!craft.equals(squadron.getCarrier())) {
                        	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Needs to be carried"));
                        	CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                        	return;
                        }
                        isSubcraft = true;
                        craft.setHitBox(craft.getHitBox().difference(c.getHitBox()));
                        craft.setOrigBlockCount(craft.getOrigBlockCount() - c.getHitBox().size());
                    }
                }
                if(!isSubcraft) {
                	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Needs to be carried"));
                	CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                	return;
                }
                if(squadron.getSize() + 1 > SquadronsReloaded.SQUADMAXSIZE + SquadronsReloaded.SQUADMAXSIZECARRIERMULT * squadron.getCarrier().getOrigBlockCount()) {
                	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Too many crafts"));
                	CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                	return;
                }
                if(squadron.getDisplacement() + c.getOrigBlockCount() > SquadronsReloaded.SQUADMAXDISP + SquadronsReloaded.SQUADMAXDISPCARRIERMULT * squadron.getCarrier().getOrigBlockCount()) {
                	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString("Squadrons - Too much displacement"));
                	CraftManager.getInstance().removeCraft(c, Reason.EMPTY);
                	return;
                }
                 
                c.setPilotLocked(carrier.getPilotLocked());
            	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(c.getType().getCraftName() + "(" + c.getHitBox().size() + ") added to squadron in position") + " " + squadron.putCraft(c));
            }
        }.runTaskLater(SquadronsReloaded.getInstance(), SquadronsReloaded.PILOTCHECKTICKS);
	}
	
}
