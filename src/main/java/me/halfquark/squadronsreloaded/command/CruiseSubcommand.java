package me.halfquark.squadronsreloaded.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.ChatUtils;

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
	        if (!player.hasPermission("movecraft.commands") || !player.hasPermission("movecraft.squadron.cruise")) {
	            sq.setCruising(false);
	            return;
	        }
	        if(sq.getCruising()){
	            sq.setCruising(false);
	            return;
	        }
	        sq.setCruiseDirection(cd);
	        sq.setCruising(true);
			return;
	    }
	    if (args[1].equalsIgnoreCase("off")) { //This goes before because players can sometimes freeze while cruising
	        sq.setCruising(false);
	        return;
	    }
	    if (!player.hasPermission("movecraft.squadron.cruise")) {
	        player.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
	        return;
	    }
	    if (args[1].equalsIgnoreCase("on")) {
	        sq.setCruiseDirection(cd);
	        sq.setCruising(true);
	        return;
	    }
	    if (args[1].equalsIgnoreCase("north") || args[0].equalsIgnoreCase("n")) {
	        sq.setCruiseDirection(CruiseDirection.NORTH);
	        sq.setCruising(true);
	        return;
	    }
	    if (args[1].equalsIgnoreCase("south") || args[0].equalsIgnoreCase("s")) {
	        sq.setCruiseDirection(CruiseDirection.SOUTH);
	        sq.setCruising(true);
	        return;
	    }
	    if (args[1].equalsIgnoreCase("east") || args[0].equalsIgnoreCase("e")) {
	        sq.setCruiseDirection(CruiseDirection.EAST);
	        sq.setCruising(true);
	        return;
	    }
	    if (args[1].equalsIgnoreCase("west") || args[0].equalsIgnoreCase("w")) {
	        sq.setCruiseDirection(CruiseDirection.WEST);
	        sq.setCruising(true);
	        return;
	    }
	}
	
}
