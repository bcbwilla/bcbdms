package net.electronexchange.plugins.bcbdms;

import org.bukkit.plugin.java.JavaPlugin;


public class BcbDMS extends JavaPlugin {	
	
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
	}

	@Override
	public void onDisable(){

	}
}
