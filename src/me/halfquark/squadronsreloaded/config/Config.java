package me.halfquark.squadronsreloaded.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class Config extends YamlConfiguration {
	
	public File file;
	public String path;
	
	public Config(String path) {
		this.path = path;
		file = new File(path);
		try {
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		loadFields();
	}
	
	public Config(File file) {
		this.file = file;
		try {
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		loadFields();
	}

	public void save() {
		saveFields();
		try {
			save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload() {
		try {
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		loadFields();
	}
	
	public abstract void loadFields();
	public abstract void saveFields();
	
}
