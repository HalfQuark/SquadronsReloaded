package me.halfquark.squadronsreloaded.formation;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Nullable;

import me.halfquark.squadronsreloaded.SquadronsReloaded;

public class FormationManager {
	
	private static FormationManager inst;
	private static ArrayList<Formation> formations;
	
	public static void initialize() {
		inst = new FormationManager();
	}
	
	private FormationManager() {
		reloadFormations();
	}
	
	public static FormationManager getInstance() {return inst;}
	public ArrayList<Formation> getFormations() {return formations;}
	@Nullable
	public Formation getFormation(String name) {
		for(Formation f : formations) {
			if(f.getName().equalsIgnoreCase(name))
				return f;
		}
		return null;
	}
	
	public void reloadFormations() {
		formations = new ArrayList<>();
		File[] files = SquadronsReloaded.FORMATIONFOLDER.listFiles();
		if(files == null)
			return;
		for(File f : files) {
			formations.add(new Formation(f));
		}
	}
	
}
