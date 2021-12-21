package me.halfquark.squadronsreloaded.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.halfquark.squadronsreloaded.formation.Formation;
import me.halfquark.squadronsreloaded.formation.FormationManager;
import me.halfquark.squadronsreloaded.squadron.Squadron;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.util.ChatUtils;

public class FormationSubcommand {

	public void run(CommandSender sender, String[] args) {
		Player pSender;
		if(!(sender instanceof Player)){
            sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Must Be Player"));
            return;
        }
		pSender = (Player) sender;
		if(!pSender.hasPermission("movecraft.squadron.formation")) {
			pSender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
			return;
		}
		Squadron sq = SquadronManager.getInstance().getPlayerSquadron(pSender, true);
		if(sq == null){
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - No Squadron Found"));
            return;
		}
		if(args.length < 2){
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid state (ON/OFF)"));
            return;
		}
		if(!args[1].equalsIgnoreCase("ON") && !args[1].equalsIgnoreCase("OFF")) {
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid state (ON/OFF)"));
            return;
		}
		if(args[1].equalsIgnoreCase("OFF")) {
			sq.formationOff();
			sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Formation OFF"));
            return;
		}
		
		
		if(args.length != 4){
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify formation and spacing"));
            return;
		}
		Formation formation = FormationManager.getInstance().getFormation(args[2]);
		if(formation == null) {
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid formation"));
            return;
		}
		int spacing;
		try {
			spacing = Integer.valueOf(args[3]);
		} catch(NumberFormatException e) {
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Specify a valid integer"));
            return;
		}
		if(spacing < formation.getMinSpacing() || formation.getMaxSpacing() < spacing) {
			sender.sendMessage(ChatUtils.ERROR_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Spacing has to be in range")
			+ " [" + String.valueOf(formation.getMinSpacing()) + "," + String.valueOf(formation.getMaxSpacing()) + "]");
            return;
		}
		sq.formationOn(formation, spacing);
		sender.sendMessage(ChatUtils.MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Squadrons - Formation")
		+ " " + formation.getName() + " " + spacing);
        return;
	}
	
}
