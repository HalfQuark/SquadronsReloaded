package me.halfquark.squadronsreloaded.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.ChatUtils;

public class ScuttleSubcommand {
	
	public void run(CommandSender sender, String[] args) {
		Player pSender;
		if(args.length == 1) {
			if(!(sender instanceof Player)){
	            sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Must Be Player"));
	            return;
	        }
			pSender = (Player) sender;
			if(!pSender.hasPermission("movecraft.squadron.scuttle")) {
				pSender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
				return;
			}
			Squadron sq = SquadronManager.getInstance().getPlayerSquadron(pSender, true);
			if(sq == null){
				sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
	            return;
			}
			sq.sinkAll();
			SquadronManager.getInstance().removeSquadron(pSender);
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Scuttle Activated"));
			return;
		}
		if(!sender.hasPermission("movecraft.squadron.scuttle.others")) {
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
			return;
		}
		if (args[1].equalsIgnoreCase("-p")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
            	Squadron sq = SquadronManager.getInstance().getPlayerSquadron(p, true);
            	if(sq == null)
            		continue;
            	sq.sinkAll();
    			SquadronManager.getInstance().removeSquadron(p);
            }
            sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Scuttled All Squadrons"));
            return;
        }
		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
            sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Player - Not Found"));
            return;
        }
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(target, true);
		if(sq == null){
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Player not piloting"));
            return;
		}
		sq.sinkAll();
		SquadronManager.getInstance().removeSquadron(target);
		sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Successful Force Scuttle"));
		return;
	}
	
}
