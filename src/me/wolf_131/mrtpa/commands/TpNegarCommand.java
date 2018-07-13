package me.wolf_131.mrtpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wolf_131.mrtpa.Main;

public class TpNegarCommand implements CommandExecutor {

	private Main plugin;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		plugin = Main.getPlugin();
		
		if(!(sender instanceof Player)) {
			System.out.println("Apenas jogadores podem executar esse comando!");
			return false;
		}
		
		if(args.length != 1) {
			p.sendMessage("§cUso incorreto! Utilize /tpnegar <jogador>.");
			return false;
		}
			
		Player enviou = Bukkit.getPlayerExact(args[0]);
		
		if(enviou == null) {
			p.sendMessage("§cO jogador " + args[0] + " não está online!");
			return false;
		}
			
		if(p == enviou) {
			p.sendMessage("§cVocê não pode aceitar pedidos de teleporte de si mesmo!");
			return false;
		}
		
		if(!plugin.teleporte_expirar.containsValue(p.getName()) || !plugin.teleporte_pendente.get(enviou.getName()).equals(p.getName())) {
			p.sendMessage("§cVocê não tem pedidos recentes!");
			return false;
		}
		
		plugin.teleporte_expirar.remove(enviou.getName());
		plugin.teleporte_pendente.remove(enviou.getName());
		p.sendMessage("§cVocê negou o teleporte do jogador " + enviou.getName() + "!");
		enviou.sendMessage("§cO jogador " + p.getName() + " negou o seu pedido de teleporte!");
		return true;
	}

}
