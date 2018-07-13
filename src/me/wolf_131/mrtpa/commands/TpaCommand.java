package me.wolf_131.mrtpa.commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wolf_131.mrtpa.Main;

public class TpaCommand implements CommandExecutor {

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
			p.sendMessage("§cUso incorreto! Utilize /tpa <jogador>.");
			return false;
		}
			
		Player target = Bukkit.getPlayerExact(args[0]);
		
		if(target == null) {
			p.sendMessage("§cO jogador " + args[0] + " não está online!");
			return false;
		}
		
		if(p == target) {
			p.sendMessage("§cVocê não pode enviar pedidos de teleporte para si mesmo!");
			return false;
		}
		
		if(plugin.teleporte_pendente.containsKey(p.getName()) || plugin.teleporte_expirar.containsKey(p.getName())) {
			p.sendMessage("§cVocê já pediu um pedido de teleporte, aguarde um pouco!");
			return false;
		}
		
		if(plugin.teleporte_pendente.containsValue(target.getName()) || plugin.teleporte_expirar.containsValue(target.getName())
				|| plugin.teleporte_pendente.containsKey(target.getName()) || plugin.teleporte_expirar.containsKey(target.getName())) {
			p.sendMessage("§cEsse jogador já tem um pedido de teleporte, aguarde um pouco!");
			return false;
		}
		
		if(!plugin.msgCooldown(p)) {
			if(plugin.tptoggle.contains(p.getName())) {
				p.sendMessage("§cVocê não pode enviar pedidos de teleporte! Use /tptoggle.");
				return false;
			}
			if(plugin.tptoggle.contains(target.getName()) && !p.hasPermission("mrtpa.staff")) {
				p.sendMessage("§cVocê não pode enviar pedidos de teleporte para esse jogador, pois ele está com teleporte desativado!");
				return false;
			}
			p.sendMessage("§ePedido de teleporte enviado para o jogador §6" + target.getName() + "§e.");
			target.sendMessage("§eO jogador §6" + p.getName() + " §edeseja se teleportar até você.");
			plugin.sendJSONMsg(target, "§eClique ", "§a§lAQUI", " §epara aceitar.", "§aClique aqui para aceitar!" , "/tpaceitar " + p.getName());
			plugin.sendJSONMsg(target, "§eClique ", "§c§lAQUI", " §epara negar.", "§cClique aqui para negar!" , "/tpnegar " + p.getName());
				
			if(!p.hasPermission("mrtpa.cooldown"))
				plugin.cooldown.put(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
			
			plugin.teleporte_pendente.put(p.getName(), target.getName());
			plugin.teleporte_expirar.put(p.getName(), target.getName());
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					if(plugin.teleporte_expirar.containsKey(p.getName())) {
						p.sendMessage("§cO seu pedido de teleporte expirou.");
						target.sendMessage("§cO pedido de teleporte do jogador " + p.getName() + " expirou.");
						plugin.teleporte_expirar.remove(p.getName());
						if(plugin.teleporte_pendente.containsKey(p.getName()))
							plugin.teleporte_pendente.remove(p.getName());
					} 
				}
			}, 20L * 30);
			return true;
		}
		return false;
	}
}
