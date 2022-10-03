package me.halfquark.squadronsreloaded.listener;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.craft.PlayerCraftImpl;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.processing.functions.Result;
import net.countercraft.movecraft.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.halfquark.squadronsreloaded.SquadronsReloaded;
import me.halfquark.squadronsreloaded.move.CraftDirectionDetection;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronCraft;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent.Reason;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.ChatUtils;
import net.countercraft.movecraft.util.MathUtils;

public class SRSignLeftClickListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        BlockState block = event.getClickedBlock().getState();
        if (!(block instanceof Sign)) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if(sign.getLine(0).equalsIgnoreCase("Formation")) {
        	Squadron sq = SquadronManager.getInstance().getPlayerSquadron(event.getPlayer(), true);
            if(sq == null) {
            	event.getPlayer().sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
                return;
            }
            sq.formationOff();
            event.getPlayer().sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Formation OFF"));
            return;
        }
        CraftType type = CraftManager.getInstance().getCraftTypeFromString(ChatColor.stripColor(sign.getLine(0)));
        if (type == null) {
            return;
        }
        if(!SquadronsReloaded.CARRIEDTYPES.contains(type.getStringProperty(CraftType.NAME)))
        	return;
        PlayerCraft pilotedCraft = CraftManager.getInstance().getCraftByPlayer(event.getPlayer());
        if(pilotedCraft == null) {
	        for(Craft c : CraftManager.getInstance().getCraftsInWorld(block.getWorld())) {
	        	if(MathUtils.locationInHitBox(c.getHitBox(), block.getLocation())) {
	        		if(c instanceof PlayerCraft) {
		        		pilotedCraft = (PlayerCraft) c;
		        		break;
	        		}
	        	}
	        }
        }
        if(pilotedCraft == null) {
        	event.getPlayer().sendMessage(I18nSupport.getInternationalisedString(
                    "Squadrons - Must pilot a carrier"));
            return;
        }
        if(!SquadronsReloaded.CARRIERTYPES.contains(pilotedCraft.getType().getStringProperty(CraftType.NAME)))
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
        Player player = event.getPlayer();
        Squadron squadron;
        if(SquadronManager.getInstance().getPlayerSquadron(player, false) == null) {
        	squadron = new Squadron(player);
        	squadron.setCarrier(pilotedCraft);
        	SquadronManager.getInstance().putSquadron(player, squadron);
        } else {
        	squadron = SquadronManager.getInstance().getPlayerSquadron(player, false);
        }
        // Attempt to run detection
        Location loc = event.getClickedBlock().getLocation();
        MovecraftLocation startPoint = new MovecraftLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        boolean carried = false;
        for (Craft craft : CraftManager.getInstance().getCraftsInWorld(loc.getWorld())) {
            if (!craft.getHitBox().contains(startPoint)) {
                continue;
            }
            if(!craft.equals(squadron.getCarrier()) && SquadronsReloaded.NEEDSCARRIER) {
                player.sendMessage(I18nSupport.getInternationalisedString("Squadrons - Needs to be carried"));
            	return;
            }
            carried = true;
        }
        if(!carried && SquadronsReloaded.NEEDSCARRIER) {
            player.sendMessage(I18nSupport.getInternationalisedString("Squadrons - Needs to be carried"));
        	return;
        }

        CraftManager.getInstance().detect(
                startPoint,
                type, (t, w, p, parents) -> {
                    assert p != null; // Note: This only passes in a non-null player.
                    assert parents.size() > 0;

                    final SquadronCraft c = new SquadronCraft(type, loc.getWorld(), p, squadron);
                    event.setCancelled(true);
                    return new Pair<>(Result.succeed(), c);
                },
                player.getWorld(), player,
                Movecraft.getAdventure().player(player),
                craft -> () -> {
                    // Release old craft if it exists
                }
        );
	}

    // TODO: Implement customisable squad max size per craft type and max distance from carrier
	@EventHandler
	public void onDetect(CraftDetectEvent event) {
		if(!(event.getCraft() instanceof SquadronCraft))
			return;
		SquadronCraft c = (SquadronCraft) event.getCraft();
		Player player = c.getSquadronPilot();
		Squadron squadron = c.getSquadron();
		if(c.getHitBox().size() == 0) {
    		CraftManager.getInstance().release(c, Reason.EMPTY, true);
    		return;
    	}
        if(squadron.getSize() + 1 > SquadronsReloaded.SQUADMAXSIZE + SquadronsReloaded.SQUADMAXSIZECARRIERMULT * squadron.getCarrier().getOrigBlockCount()) {
        	player.sendMessage(I18nSupport.getInternationalisedString("Squadrons - Too many crafts"));
        	CraftManager.getInstance().release(c, Reason.FORCE, true);
        	return;
        }
        if(squadron.getDisplacement() + c.getOrigBlockCount() > SquadronsReloaded.SQUADMAXDISP + SquadronsReloaded.SQUADMAXDISPCARRIERMULT * squadron.getCarrier().getOrigBlockCount()) {
        	player.sendMessage(I18nSupport.getInternationalisedString("Squadrons - Too much displacement"));
        	CraftManager.getInstance().release(c, Reason.FORCE, true);
        	return;
        }
        CruiseDirection cd = CraftDirectionDetection.detect(c);
        if(c.equals(squadron.getLeadCraft())) {
        	squadron.setDirection(cd);
        }
        if(cd == null) {
        	player.sendMessage(I18nSupport.getInternationalisedString("Squadrons - Contradicting Cruise signs"));
        	CraftManager.getInstance().release(c, Reason.FORCE, true);
        	return;
        }
        if(!cd.equals(CruiseDirection.NONE))
        	CraftRotateManager.getInstance().setDirection(c, cd);
        int position = squadron.putCraft(c);
    	player.sendMessage(I18nSupport.getInternationalisedString(c.getType().getStringProperty(CraftType.NAME) + "(" + c.getHitBox().size() + ") added to squadron in position") + " " + position);
	}
	
}
