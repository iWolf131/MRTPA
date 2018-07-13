package me.wolf_131.mrtpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wolf_131.mrtpa.Main;

public class StaffCommands implements CommandExecutor {

	private Main plugin;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		plugin = Main.getPlugin();
		
		if(!(sender instanceof Player)) {
			System.out.println("Apenas jogadores podem executar esse comando!");
			return false;
		}
		
		if(!p.hasPermission("mrtpa.staff")) {
			p.sendMessage("§cVocê não tem permissão para executar esse comando!");
			return false;
		}
		
		if(args.length == 0) {
			if(cmd.getName().equalsIgnoreCase("tpaleatorio")) {
				Player random;
				
				do {
					random = Bukkit.getOnlinePlayers().iterator().next();
				} while (random != p);
				
				if(random == null) {
					p.sendMessage("§cO jogador saiu do servidor.");
					return false;
				}
				
				p.sendMessage("§eTeleportando-se para o jogador " + random.getName() + ".");
				plugin.sendTitle(p, "§6Teleportado", "§fpara " + random.getName() + ".");
				p.teleport(random.getLocation());
				return true;
			} else {
				p.sendMessage("§cUso incorreto! Utilize /" + cmd.getName() + " <jogador>.");
				return false;
			}
		} else if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				p.sendMessage("§cO jogador " + args[0] + " não está online!");
				return false;
			}
			
			if(target == p) {
				p.sendMessage("§cVocê não pode fazer isso!");
				return false;
			}
			
			if(cmd.getName().equalsIgnoreCase("tpo")) {
				p.sendMessage("§eTeleportando-se para o jogador " + target.getName() + ".");
				plugin.sendTitle(p, "§6Teleportado", "§fpara " + target.getName() + ".");
				p.teleport(target.getLocation());
				return true;
			}
			
			else if(cmd.getName().equalsIgnoreCase("tpaqui")) {
				p.sendMessage("§eTeleportando o jogador " + target.getName() + " para você.");
				plugin.sendTitle(p, "§6Teleportando", "§f" + target.getName() + " para você.");
				plugin.sendTitle(target, "§6Teleportado", "§fpara " + p.getName() + ".");
				target.sendMessage("§eTeleportando-se para o jogador " + p.getName() + ".");
				target.teleport(p.getLocation());
				return true;
			}
		}
		return false;
	}

}
