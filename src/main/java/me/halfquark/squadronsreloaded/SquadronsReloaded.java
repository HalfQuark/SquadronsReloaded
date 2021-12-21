package me.halfquark.squadronsreloaded;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import me.halfquark.squadronsreloaded.async.SRAsyncManager;
import me.halfquark.squadronsreloaded.command.SquadronCommand;
import me.halfquark.squadronsreloaded.formation.FormationManager;
import me.halfquark.squadronsreloaded.listener.RedstoneComponentListener;
import me.halfquark.squadronsreloaded.listener.ReleaseListener;
import me.halfquark.squadronsreloaded.listener.RotationListener;
import me.halfquark.squadronsreloaded.listener.SRInteractListener;
import me.halfquark.squadronsreloaded.listener.SRPlayerListener;
import me.halfquark.squadronsreloaded.listener.SRSignLeftClickListener;
import me.halfquark.squadronsreloaded.listener.SinkListener;
import me.halfquark.squadronsreloaded.listener.SwitchListener;
import me.halfquark.squadronsreloaded.listener.TranslationListener;
import me.halfquark.squadronsreloaded.move.CraftProximityManager;
import me.halfquark.squadronsreloaded.move.CraftRotateManager;
import me.halfquark.squadronsreloaded.move.CraftTranslateManager;
import me.halfquark.squadronsreloaded.sign.SRAscendSign;
import me.halfquark.squadronsreloaded.sign.SRCruiseSign;
import me.halfquark.squadronsreloaded.sign.SRDescendSign;
import me.halfquark.squadronsreloaded.sign.SRFormationSign;
import me.halfquark.squadronsreloaded.sign.SRHelmSign;
import me.halfquark.squadronsreloaded.sign.SRLeadSign;
import me.halfquark.squadronsreloaded.sign.SRReleaseSign;
import me.halfquark.squadronsreloaded.sign.SRSyncedSign;
import me.halfquark.squadronsreloaded.squadron.SquadronManager;

public class SquadronsReloaded extends JavaPlugin {
	
	private static SquadronsReloaded inst;
	private SquadronCommand sc;
	
	public static File FORMATIONFOLDER;
	
	public static boolean TPTONEWLEAD;
	
	public static List<String> CARRIERTYPES;
	public static List<String> CARRIEDTYPES;
	public static boolean NEEDSCARRIER;
	public static int PILOTCHECKTICKS;
	public static int MANOVERBOARDTIME;
	public static int TURNTICKS;
	public static double SQUADMAXSIZE;
	public static double SQUADMAXSIZECARRIERMULT;
	public static double SQUADMAXDISP;
	public static double SQUADMAXDISPCARRIERMULT;
	public static double FORMATIONROUNDDISTANCE;
	public static double FORMATIONSPEEDMULTIPLIER;
	
	public static List<String> SYNCEDSIGNS;
	
	@Override
	public void onEnable() {
		inst = this;
		saveDefaultConfig();
		
		FORMATIONFOLDER = new File(SquadronsReloaded.inst.getDataFolder(), File.separator + "formations");
		CARRIERTYPES = getConfig().getStringList("carrierTypes");
		CARRIEDTYPES = getConfig().getStringList("carriedTypes");
		NEEDSCARRIER = getConfig().getBoolean("needsCarrier");
		PILOTCHECKTICKS = getConfig().getInt("pilotCheckTicks");
		MANOVERBOARDTIME = getConfig().getInt("manoverboardTime");
		TPTONEWLEAD = getConfig().getBoolean("tpToNewLead");
		TURNTICKS = getConfig().getInt("turnTicks");
		SQUADMAXSIZE = getConfig().getDouble("squadMaxSize");
		SQUADMAXSIZECARRIERMULT = getConfig().getDouble("squadMaxSizeCarrierMultiplier");
		SQUADMAXDISP = getConfig().getDouble("squadMaxDisplacement");
		SQUADMAXDISPCARRIERMULT = getConfig().getDouble("squadMaxDisplacementCarrierMultiplier");
		FORMATIONROUNDDISTANCE = getConfig().getDouble("formationRoundDistance");
		SYNCEDSIGNS = getConfig().getStringList("syncedSigns");
		FORMATIONSPEEDMULTIPLIER = getConfig().getDouble("formationSpeedMultiplier");
		
		loadResources();
		
		SquadronManager.initialize();
		FormationManager.initialize();
		CraftTranslateManager.initialize();
		CraftRotateManager.initialize();
		CraftProximityManager.initialize();
		SRAsyncManager.initialize(this);
		
		getServer().getPluginManager().registerEvents(new SRSignLeftClickListener(), this);
		getServer().getPluginManager().registerEvents(new SRInteractListener(), this);
		getServer().getPluginManager().registerEvents(new SRPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new SRCruiseSign(), this);
		getServer().getPluginManager().registerEvents(new SRHelmSign(), this);
		getServer().getPluginManager().registerEvents(new SRAscendSign(), this);
		getServer().getPluginManager().registerEvents(new SRDescendSign(), this);
		getServer().getPluginManager().registerEvents(new SRLeadSign(), this);
		getServer().getPluginManager().registerEvents(new SRReleaseSign(), this);
		getServer().getPluginManager().registerEvents(new SRFormationSign(), this);
		getServer().getPluginManager().registerEvents(new SRSyncedSign(), this);
		
		getServer().getPluginManager().registerEvents(new ReleaseListener(), this);
		getServer().getPluginManager().registerEvents(new SinkListener(), this);
		getServer().getPluginManager().registerEvents(new SwitchListener(), this);
		getServer().getPluginManager().registerEvents(new RedstoneComponentListener(), this);
		
		getServer().getPluginManager().registerEvents(new TranslationListener(), this);
		getServer().getPluginManager().registerEvents(new RotationListener(), this);
		sc = new SquadronCommand();
		getCommand("squadron").setExecutor(sc);
		getCommand("squadron").setTabCompleter(sc);
	}
	
	public static SquadronsReloaded getInstance() {return inst;}
	
	private void loadResources() {
		File[] files = SquadronsReloaded.FORMATIONFOLDER.listFiles();
		if(files != null)
			return;
		saveResource("formations" + File.separator + "Echelon.formation", false);
		saveResource("formations" + File.separator + "Line.formation", false);
		saveResource("formations" + File.separator + "Column.formation", false);
		saveResource("formations" + File.separator + "Vic.formation", false);
	}
	
}
