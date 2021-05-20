package me.halfquark.squadronsreloaded.command;

import org.bukkit.command.CommandSender;

import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.ChatUtils;

public class ListSubcommand {

	public void run(CommandSender sender, String[] args) {
		if(!sender.hasPermission("movecraft.squadron.list")) {
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
			return;
		}
		sender.sendMessage("Squadron Report");
		for(Squadron sq : SquadronManager.getInstance().getSquadronList()) {
			String stats = "";
			stats += sq.getPilot().getName() + " ";
			stats += sq.getSize() + " (";
			stats += sq.getDisplacement() + ") @ ";
			MovecraftLocation leadLoc = sq.getLeadCraft().getHitBox().getMidPoint();
			stats += leadLoc.getX() + "," + leadLoc.getY() + "," + leadLoc.getZ();
			sender.sendMessage(stats);
		}
	}
	
}
