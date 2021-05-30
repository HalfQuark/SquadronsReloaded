package me.halfquark.squadronsreloaded.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.halfquark.squadronsreloaded.formation.Formation;
import me.halfquark.squadronsreloaded.formation.FormationManager;

public class SquadronCommand implements TabExecutor {

	ManOverBoardSubcommand mo;
	CarrierSubcommand carrier;
	CruiseSubcommand cruise;
	ReleaseSubcommand release;
	ScuttleSubcommand scuttle;
	InfoSubcommand info;
	ListSubcommand list;
	FormationSubcommand formation;
	
	public SquadronCommand() {
		mo = new ManOverBoardSubcommand();
		carrier = new CarrierSubcommand();
		cruise = new CruiseSubcommand();
		release = new ReleaseSubcommand();
		scuttle = new ScuttleSubcommand();
		info = new InfoSubcommand();
		list = new ListSubcommand();
		formation = new FormationSubcommand();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("squadron"))
            return false;
		if(args.length == 0)
			return false;
		switch(args[0].toLowerCase()) {
		case "lead":
			mo.run(sender, args);
			break;
		case "carrier":
			carrier.run(sender, args);
			break;
		case "cruise":
			cruise.run(sender, args);
			break;
		case "release":
			release.run(sender, args);
			break;
		case "scuttle":
			scuttle.run(sender, args);
			break;
		case "formation":
		case "f":
			formation.run(sender, args);
			break;
		case "info":
		case "i":
			info.run(sender, args);
			break;
		case "list":
		case "l":
			list.run(sender, args);
			break;
		default:
			return false;
		}
		return true;
	}
	
	private final String[] completions = {"manoverboard", "cruise", "release", "scuttle", "formation", "info", "list"};
	private final String[] cruiseCompletions = {"North", "East", "South", "West", "On", "Off"};
	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
	    if(!command.getName().equalsIgnoreCase("squadron"))
	    	return Collections.emptyList();
	    List<String> returnValues = new ArrayList<>();
	    if(strings.length == 1) {
		    for(String completion : completions)
		        if(completion.toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
		            returnValues.add(completion);
		    return returnValues;
	    }
		if(strings[0].equalsIgnoreCase("cruise")) {
		    for(String completion : cruiseCompletions)
		        if(completion.toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
		            returnValues.add(completion);
		    return returnValues;
		}
		if(strings[0].equalsIgnoreCase("release") || strings[0].equalsIgnoreCase("scuttle") || strings[0].equalsIgnoreCase("info")) {
		    if(commandSender.hasPermission("movecraft.squadron." + strings[0].toLowerCase() + ".others")) {
				for(Player p : Bukkit.getOnlinePlayers())
			        if(p.getName().toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
			            returnValues.add(p.getName());
			    return returnValues;
		    }
		}
		if(strings[0].equalsIgnoreCase("formation") || strings[0].equalsIgnoreCase("f")) {
		    if(commandSender.hasPermission("movecraft.squadron.formation")) {
		    	if(strings.length <= 2) {
		    		if("on".startsWith(strings[strings.length-1].toLowerCase()))
			            returnValues.add("ON");
		    		if("off".startsWith(strings[strings.length-1].toLowerCase()))
			            returnValues.add("OFF");
		    		return returnValues;
		    	}
		    	if(strings.length > 3)
		    		return returnValues;
				for(Formation f : FormationManager.getInstance().getFormations())
			        if(f.getName().toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
			            returnValues.add(f.getName());
			    return returnValues;
		    }
		}
		return Collections.emptyList();
	}
	
}
