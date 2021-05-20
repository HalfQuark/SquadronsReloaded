package me.halfquark.squadronsreloaded.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class SquadronCommand implements TabExecutor {

	ManOverBoardSubcommand mo;
	CruiseSubcommand cruise;
	ReleaseSubcommand release;
	ScuttleSubcommand scuttle;
	InfoSubcommand info;
	ListSubcommand list;
	
	public SquadronCommand() {
		mo = new ManOverBoardSubcommand();
		cruise = new CruiseSubcommand();
		release = new ReleaseSubcommand();
		scuttle = new ScuttleSubcommand();
		info = new InfoSubcommand();
		list = new ListSubcommand();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("squadron"))
            return false;
		if(args.length == 0)
			return false;
		switch(args[0].toLowerCase()) {
		case "manoverboard":
		case "mo":
			mo.run(sender, args);
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
		return Collections.emptyList();
	}
	
}
