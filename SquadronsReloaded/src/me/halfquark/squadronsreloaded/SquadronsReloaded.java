package me.halfquark.squadronsreloaded;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import me.halfquark.squadronsreloaded.command.SquadronCommand;
import me.halfquark.squadronsreloaded.craft.CraftListener;
import me.halfquark.squadronsreloaded.listener.RedstoneComponentListener;
import me.halfquark.squadronsreloaded.listener.SRInteractListener;
import me.halfquark.squadronsreloaded.listener.SRPlayerListener;
import me.halfquark.squadronsreloaded.listener.SRSignClickListener;
import me.halfquark.squadronsreloaded.listener.SwitchListener;
import me.halfquark.squadronsreloaded.sign.SRAscendSign;
import me.halfquark.squadronsreloaded.sign.SRCruiseSign;
import me.halfquark.squadronsreloaded.sign.SRDescendSign;
import me.halfquark.squadronsreloaded.sign.SRHelmSign;
import me.halfquark.squadronsreloaded.sign.SRLeadSign;
import me.halfquark.squadronsreloaded.sign.SRReleaseSign;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;

public class SquadronsReloaded extends JavaPlugin {
	
	private static SquadronsReloaded inst;
	private SquadronCommand sc;
	
	public static List<String> CARRIERTYPES;
	public static List<String> CARRIEDTYPES;
	public static int PILOTCHECKTICKS;
	public static int MANOVERBOARDTIME;
	
	@Override
	public void onEnable() {
		inst = this;
		saveDefaultConfig();
		CARRIERTYPES = getConfig().getStringList("carrierTypes");
		CARRIEDTYPES = getConfig().getStringList("carriedTypes");
		PILOTCHECKTICKS = getConfig().getInt("pilotCheckTicks");
		MANOVERBOARDTIME = getConfig().getInt("manoverboardTime");
		SquadronManager.initialize();
		getServer().getPluginManager().registerEvents(new SRSignClickListener(), this);
		getServer().getPluginManager().registerEvents(new SRInteractListener(), this);
		getServer().getPluginManager().registerEvents(new CraftListener(), this);
		getServer().getPluginManager().registerEvents(new SRPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new SRCruiseSign(), this);
		getServer().getPluginManager().registerEvents(new SRHelmSign(), this);
		getServer().getPluginManager().registerEvents(new SRAscendSign(), this);
		getServer().getPluginManager().registerEvents(new SRDescendSign(), this);
		getServer().getPluginManager().registerEvents(new SRLeadSign(), this);
		getServer().getPluginManager().registerEvents(new SRReleaseSign(), this);
		//Remote signs on squadrons turned out to be a very bad idea
		//getServer().getPluginManager().registerEvents(new SRRemoteSign(), this);
		getServer().getPluginManager().registerEvents(new SwitchListener(), this);
		getServer().getPluginManager().registerEvents(new RedstoneComponentListener(), this);
		sc = new SquadronCommand();
		getCommand("squadron").setExecutor(sc);
		getCommand("squadron").setTabCompleter(sc);
	}
	
	public static SquadronsReloaded getInstance() {return inst;}
	
}
