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
		if(plugin.teleportando.contains(e.getPlayer().getName())) 
		if(e.getFrom().getBlockX() != e.getTo().getBlockX()
			|| e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
			e.getPlayer().sendMessage("§cVocê se moveu em teleporte, o teleporte foi cancelado!");
			plugin.teleportando.remove(e.getPlayer().getName());
		}
	}
}

