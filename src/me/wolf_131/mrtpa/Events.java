package me.wolf_131.mrtpa;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Events implements Listener {

	private Main plugin;
	public Events(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onTeleportMove(PlayerMoveEvent e) {
		if(e.getFrom().getX() != e.getTo().getX()
			|| e.getFrom().getZ() != e.getTo().getZ()) 
		if(plugin.teleportando.contains(e.getPlayer().getName())) { 
			e.getPlayer().sendMessage("�cVoc� se moveu em teleporte, o teleporte foi cancelado!");
			plugin.teleportando.remove(e.getPlayer().getName());
		}
	}
}
