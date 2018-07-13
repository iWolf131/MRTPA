package me.wolf_131.mrtpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wolf_131.mrtpa.Main;

public class OtherTpaCommands implements CommandExecutor {

	private Main plugin;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		plugin = Main.getPlugin();
		
		if(!(sender instanceof Player)) {
			System.out.println("Apenas jogadores podem executar esse comando!");
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("tptoggle")) {
			if(!plugin.tptoggle.contains(p.getName())) {
				plugin.tptoggle.add(p.getName());
				p.sendMessage("§cVocê desativou o seu teleporte!");
				p.sendMessage("§cAgora, outros jogadores não conseguirão fazer um pedido de teleporte para você.");
				return true;
			} else {
				plugin.tptoggle.remove(p.getName());
				p.sendMessage("§eVocê ativou o seu teleporte!");
				p.sendMessage("§eAgora, outros jogadores conseguirão fazer um pedido de teleporte para você.");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("tpcancelar")) {
			if(!plugin.teleportando.contains(p.getName()) && !plugin.teleporte_pendente.containsKey(p.getName()) && !plugin.teleporte_expirar.containsKey(p.getName())) {
				p.sendMessage("§cVocê não tem pedidos de teleporte recentes.");
				return false;
			}
			if(plugin.teleportando.contains(p.getName())) 
				plugin.teleportando.remove(p.getName());
			if(plugin.teleporte_pendente.containsKey(p.getName()))
				plugin.teleporte_pendente.remove(p.getName());
			if(plugin.teleporte_expirar.containsKey(p.getName()))
				plugin.teleporte_expirar.remove(p.getName());
			p.sendMessage("§cVocê cancelou o seu teleporte.");
			plugin.sendTitle(p, "§6Teleporte", "§fcancelado!");
			return true;
		}
		return false;
	}

}
