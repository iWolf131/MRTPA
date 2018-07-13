package me.wolf_131.mrtpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wolf_131.mrtpa.Main;

public class TpAceitarCommand implements CommandExecutor {

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
			p.sendMessage("§cUso incorreto! Utilize /tpaceitar <jogador>.");
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
		
		if(!plugin.teleporte_pendente.containsValue(p.getName()) || !plugin.teleporte_expirar.containsValue(p.getName())) {
			p.sendMessage("§cVocê não tem pedidos recentes!");
			return false;
		}
		
		if(!plugin.teleporte_pendente.containsKey(enviou.getName()) || !plugin.teleporte_expirar.containsKey(enviou.getName())) {
			p.sendMessage("§cEsse jogador não pediu teleporte para você!");
			return false;
		}
		
		if(!plugin.teleporte_pendente.get(enviou.getName()).equals(p.getName()) || !plugin.teleporte_expirar.get(enviou.getName()).equals(p.getName())) {
			p.sendMessage("§cEsse jogador não pediu teleporte para você!");
			return false;
		}
			
		Location loc = enviou.getLocation();
		loc.setY(enviou.getLocation().getY() + 1);
		plugin.teleporte_expirar.remove(enviou.getName());
		plugin.teleporte_pendente.remove(enviou.getName());
		
		plugin.teleportando.add(enviou.getName());
		
		p.sendMessage("§eVocê aceitou o pedido de teleporte do jogador " + enviou.getName() + "!");
		
		if(!enviou.hasPermission("mrtpa.bypass")) {	
			startRunnables(p, enviou);
			return true;
		} else {
			enviou.teleport(p.getLocation());
			plugin.teleportando.remove(enviou.getName());
			enviou.sendMessage("§eVocê foi teleportado para o jogador " + p.getName() + ".");
			plugin.sendTitle(enviou, "§6Teleportado", "§fpara " + p.getName() + ".");
			enviou.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1000);
			return true;
		}
	}

	public void startRunnables(Player p, Player enviou) {
		Location loc = enviou.getLocation();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(plugin.teleportando.contains(enviou.getName())) {
					enviou.getWorld().playEffect(loc, Effect.PORTAL, 50);
					enviou.teleport(p.getLocation());
					enviou.sendMessage("§eVocê foi teleportado para o jogador " + p.getName() + ".");
					plugin.sendTitle(enviou, "§6Teleportado", "§fpara " + p.getName() + ".");
					plugin.sendActionBar(enviou, "§a▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");
					plugin.teleportando.remove(enviou.getName());
				}
			}
		}.runTaskLater(plugin, 20L * 10);
				
		new BukkitRunnable() {
			int cool = 3;
			@Override
			public void run() {
				if(plugin.teleportando.contains(enviou.getName()) && 0 < cool) {
					enviou.sendMessage("§eTeleportando em " + cool + " " + (cool == 1 ? "segundo" : "segundos"));
					p.sendMessage("§eO jogador " + enviou.getName() + " irá teleportar-se até si em " + cool + " " + (cool == 1 ? "segundo" : "segundos"));
					plugin.sendTitle(enviou, "§6Teleportando", "§fem " + cool + " " + (cool == 1 ? "segundo" : "segundos"));
					cool--;
				} else
					cancel();
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 20L * 3);
			
		new BukkitRunnable() {
			int vermelho = 20;
			int verde = 0;
			String verde_cor = "§a▉";
			String vermelho_cor = "§c▉";
			StringBuilder stg_verm = new StringBuilder();
			StringBuilder stg_verd = new StringBuilder();
			public void run() {
				if(plugin.teleportando.contains(enviou.getName()) && 0 <= vermelho && verde <= 20) {
					stg_verm = new StringBuilder();
					stg_verd = new StringBuilder();
					
					vermelho--;
					verde++;
					
					for(int verm = 0; verm <= vermelho; verm++)
						stg_verm.append(vermelho_cor);
					for(int verd = 0; verd <= verde; verd++)
						stg_verd.append(verde_cor);
					plugin.sendActionBar(enviou, stg_verd.toString() + stg_verm.toString());
				} else
					cancel();
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 10L);
	}
}
