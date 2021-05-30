package me.halfquark.squadronsreloaded.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;

public class CruiseSubcommand {

	public void run(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
            sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Must Be Player"));
            return;
        }
		Player player = (Player) sender;
		if(!player.hasPermission("movecraft.squadron.cruise")) {
			player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return;
		}
		
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(player, true);
		if(sq == null) {
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
		}
		
		CruiseDirection cd;
		// Normalize yaw from [-360, 360] to [0, 360]
		float yaw = (player.getLocation().getYaw() + 360.0f);
        if (yaw >= 360.0f) {
            yaw %= 360.0f;
        }
        if (yaw >= 45 && yaw < 135) { // west
            cd = CruiseDirection.WEST;
        } else if (yaw >= 135 && yaw < 225) { // north
            cd = CruiseDirection.NORTH;
        } else if (yaw >= 225 && yaw <= 315){ // east
            cd = CruiseDirection.EAST;
        } else { // default south
            cd = CruiseDirection.SOUTH;
        }
	
	    if(args.length < 2){
			for(Craft craft : sq.getCrafts()) {
				if (craft == null)
		            continue;
		        if (!player.hasPermission("movecraft.commands") || !player.hasPermission("movecraft.squadron.cruise")) {
		            craft.setCruising(false);
		            continue;
		        }
		        if(craft.getCruising()){
		            craft.setCruising(false);
		            continue;
		        }
		        craft.setCruiseDirection(cd);
		        craft.setCruising(true);
			}
			return;
	    }
	    if (args[1].equalsIgnoreCase("off")) { //This goes before because players can sometimes freeze while cruising
	        for(Craft craft : sq.getCrafts()) {
		        if (craft == null)
		            continue;
		        craft.setCruising(false);
	        }
	        return;
	    }
	    if (!player.hasPermission("movecraft.squadron.cruise")) {
	        player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
	        return;
	    }
	
	    for(Craft craft : sq.getCrafts()) {
		    if (craft == null)
		        continue;
		
		    if (!player.hasPermission("movecraft." + craft.getType().getCraftName() + ".move")) {
		        player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
		        continue;
		    }
		    if (!craft.getType().getCanCruise()) {
		        player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Cruise - Craft Cannot Cruise"));
		        continue;
		    }
		
		
		    if (args[1].equalsIgnoreCase("on")) {
		        craft.setCruiseDirection(cd);
		        craft.setCruising(true);
		        continue;
		    }
		    if (args[1].equalsIgnoreCase("north") || args[0].equalsIgnoreCase("n")) {
		        craft.setCruiseDirection(CruiseDirection.NORTH);
		        craft.setCruising(true);
		        continue;
		    }
		    if (args[1].equalsIgnoreCase("south") || args[0].equalsIgnoreCase("s")) {
		        craft.setCruiseDirection(CruiseDirection.SOUTH);
		        craft.setCruising(true);
		        continue;
		    }
		    if (args[1].equalsIgnoreCase("east") || args[0].equalsIgnoreCase("e")) {
		        craft.setCruiseDirection(CruiseDirection.EAST);
		        craft.setCruising(true);
		        continue;
		    }
		    if (args[1].equalsIgnoreCase("west") || args[0].equalsIgnoreCase("w")) {
		        craft.setCruiseDirection(CruiseDirection.WEST);
		        craft.setCruising(true);
		        continue;
		    }
	    }
	}
	
}
