package me.wolf_131.mrtpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private Main plugin;
	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			System.out.println("Apenas jogadores podem executar esse comando!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("tpa")) {
			if(args.length == 1) {
				Player target = Bukkit.getPlayerExact(args[0]);
				if(target != null) {
					if(!plugin.cooldown.containsKey(p.getName())) {
						if(plugin.tptoggle.contains(p.getName())) {
							p.sendMessage("§cVocê não pode enviar pedidos de teleporte! Use /tptoggle.");
							return true;
						}
						if(plugin.tptoggle.contains(target.getName()) && !p.hasPermission("mrtpa.staff")) {
							p.sendMessage("§cVocê não pode enviar pedidos de teleporte para esse jogador, pois ele está com teleporte desativado!");
							return true;
						}
						p.sendMessage("§ePedido de teleporte enviado para o jogador §6" + target.getName() + "§e.");
						target.sendMessage("§eO jogador §6" + p.getName() + " §edeseja se teleportar até você.");
						plugin.sendJSONMsg(target, "§eClique ", "§a§lAQUI", " §epara aceitar.", "§aClique aqui para aceitar!" , "/tpaceitar " + target.getName());
						plugin.sendJSONMsg(target, "§eClique ", "§c§lAQUI", " §epara negar.", "§cClique aqui para negar!" , "/tpnegar " + target.getName());
						plugin.cooldown.put(p.getName(), System.currentTimeMillis());
						plugin.teleporte_pendente.add(target.getName());
						plugin.teleporte_expirar.add(target.getName());
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								if(plugin.teleporte_expirar.contains(target.getName())) {
									p.sendMessage("§cO seu pedido de teleporte expirou.");
									target.sendMessage("§cO pedido de teleporte do jogador " + p.getName() + " expirou.");
									plugin.teleporte_expirar.remove(target.getName());
								} 
							}
						}, 20L*30);
						
				        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				        	public void run() {
				        		if(plugin.cooldown.containsKey(p.getName()))
				        			plugin.cooldown.remove(p.getName());
				        	}
				        }, (long) (15 * 20L));
						return true;
					} else
						plugin.msgCooldown(p);
				} else {
					p.sendMessage("§cO jogador " + args[0] + " não está online!");
					return true;
				}
			} else {
				p.sendMessage("§cUso incorreto! Utilize /tpa <jogador>.");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("tpaceitar")) {
			if(args.length == 1) {
				Player enviou = Bukkit.getPlayerExact(args[0]);
				if(enviou != null) {
					if(plugin.teleporte_pendente.contains(p.getName()) && plugin.teleporte_expirar.contains(p.getName())) {
						plugin.teleporte_expirar.remove(p.getName());
						plugin.teleportando.add(enviou.getName());
						plugin.teleporte_pendente.remove(p.getName());
						if(enviou.hasPermission("mrtpa.bypass")) {
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								if(plugin.teleportando.contains(enviou.getName())) {
									enviou.teleport(p.getLocation());
									enviou.sendMessage("§eVocê foi teleportado para o jogador " + p.getName() + ".");
									plugin.sendTitle(enviou, "§6Teleportado", "§fpara " + p.getName() + ".");
									plugin.teleportando.remove(enviou.getName());
								}
							}
						}, (long)(20*9)); 
						Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
							int cool = 3;
							public void run() {
								if(plugin.teleportando.contains(enviou.getName()) && 0 < cool) {
									enviou.sendMessage("§eTeleportando em " + cool + " segundos.");
									p.sendMessage("§eO jogador " + enviou.getName() + " irá teleportar-se até si em " + cool + " segundos.");
									if(cool != 1) {
										plugin.sendTitle(enviou, "§6Teleportando", "§fem " + cool + " segundos!");
										if(cool == 3)
											plugin.sendActionBar(p, "§a▉▉▉▉▉§c▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");
										 else
											plugin.sendActionBar(p, "§a▉▉▉▉▉▉▉▉▉▉▉▉§c▉▉▉▉▉▉▉▉");
									} else {
										plugin.sendTitle(enviou, "§6Teleportando", "§fem " + cool + " segundo!");
										plugin.sendActionBar(p, "§a▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");
									}
									cool--;
								} else
								Bukkit.getScheduler().cancelTask(420);
							}
						}, 20L, 20L*3);
						
						} else {
							enviou.teleport(p.getLocation());
							enviou.sendMessage("§eVocê foi teleportado para o jogador " + p.getName() + ".");
							plugin.sendTitle(enviou, "§6Teleportado", "§fpara " + p.getName() + ".");
						}
					} else {
						p.sendMessage("§cVocê não tem pedidos recentes!");
					}
				} else {
					p.sendMessage("§cO jogador " + args[0] + " não está online!");
					return true;
				}
			} else
				p.sendMessage("§cUso incorreto. Utlilize /tpaceitar <jogador>");
		}
		
		if(cmd.getName().equalsIgnoreCase("tpnegar")) {
			if(args.length == 1) {
				Player enviou = Bukkit.getPlayerExact(args[0]);
				if(enviou != null) {
					if(plugin.teleporte_pendente.contains(p.getName()) && plugin.teleporte_expirar.contains(p.getName())) {
						plugin.teleporte_expirar.remove(p.getName());
						plugin.teleporte_pendente.remove(p.getName());
						p.sendMessage("§cVocê negou o teleporte do jogador " + enviou.getName() + "!");
						enviou.sendMessage("§cO jogador " + p.getName() + " negou o seu pedido de teleporte!");
					} else {
						p.sendMessage("§cVocê não tem pedidos recentes!");
					}
				} else {
					p.sendMessage("§cO jogador " + args[0] + " não está online!");
					return true;
				}
			} else
				p.sendMessage("§cUso incorreto. Utlilize /tpnegar <jogador>");
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
		
		if(cmd.getName().equalsIgnoreCase("tpo")) {
			if(p.hasPermission("mrtpa.staff")) {
				if(args.length == 1) {
					Player target = Bukkit.getPlayerExact(args[0]);
					if(target != null) {
						p.sendMessage("§eTeleportando-se para o jogador " + target.getName() + ".");
						plugin.sendTitle(p, "§6Teleportado", "§fpara " + target.getName() + ".");
						p.teleport(target.getLocation());
						return true;
					} else {
						p.sendMessage("§cO jogador " + args[0] + " não está online!");
						return true;
					}
				} else
					p.sendMessage("§cUso incorreto. Utilize /tpo <jogador>.");
			} else {
				p.sendMessage("§cVocê não tem permissão para isso!");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("tpaqui")) {
			if(p.hasPermission("mrtpa.staff")) {
				if(args.length == 1) {
					Player target = Bukkit.getPlayerExact(args[0]);
					if(target != null) {
						p.sendMessage("§eTeleportando o jogador " + target.getName() + " para você.");
						plugin.sendTitle(p, "§6Teleportando", "§f" + p.getName() + " para você.");
						plugin.sendTitle(target, "§6Teleportado", "§fpara " + p.getName() + ".");
						target.sendMessage("§eTeleportando-se para o jogador " + p.getName() + ".");
						target.teleport(p.getLocation());
						return true;
					} else {
						p.sendMessage("§cO jogador " + args[0] + " não está online!");
						return true;
					}
				} else
					p.sendMessage("§cUso incorreto. Utilize /tpaqui <jogador>.");
			} else {
				p.sendMessage("§cVocê não tem permissão para isso!");
				return true;
			}
		}
		
		return true;
	}

}
