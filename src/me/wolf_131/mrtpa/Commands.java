package me.wolf_131.mrtpa;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
					if(!plugin.msgCooldown(p)) {
						if(plugin.tptoggle.contains(p.getName())) {
							p.sendMessage("§cVocê não pode enviar pedidos de teleporte! Use /tptoggle.");
							return true;
						}
						if(plugin.tptoggle.contains(target.getName()) && !p.hasPermission("mrtpa.staff")) {
							p.sendMessage("§cVocê não pode enviar pedidos de teleporte para esse jogador, pois ele está com teleporte desativado!");
							return true;
						}
						if(p == target) {
							p.sendMessage("§cVocê não pode enviar pedidos de teleporte para si mesmo!");
							return true;
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
								if(!plugin.teleporte_expirar.isEmpty()) {
									p.sendMessage("§cO seu pedido de teleporte expirou.");
									target.sendMessage("§cO pedido de teleporte do jogador " + p.getName() + " expirou.");
									plugin.teleporte_expirar.remove(target.getName());
								} 
							}
						}, 20L*30);
						return true;
					}
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
					if(plugin.teleporte_pendente.containsValue(p.getName()) && plugin.teleporte_expirar.containsValue(p.getName())) {
						plugin.teleporte_expirar.remove(p.getName());
						plugin.teleportando.add(enviou.getName());
						plugin.teleporte_pendente.remove(p.getName());
						if(!enviou.hasPermission("mrtpa.bypass")) {
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								if(plugin.teleportando.contains(enviou.getName())) {
									enviou.teleport(p.getLocation());
									enviou.sendMessage("§eVocê foi teleportado para o jogador " + p.getName() + ".");
									plugin.sendTitle(enviou, "§6Teleportado", "§fpara " + p.getName() + ".");
									plugin.sendActionBar(enviou, "§a▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");
									plugin.teleportando.remove(enviou.getName());
								} else
									p.sendMessage("§cO teleporte foi cancelado!");
							}
						}, (long)(20*10)); 
						Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
							int cool = 3;
							public void run() {
								if(plugin.teleportando.contains(enviou.getName()) && 0 < cool) {
									if(cool != 1) {
										plugin.sendTitle(enviou, "§6Teleportando", "§fem " + cool + " segundos!");
										enviou.sendMessage("§eTeleportando em " + cool + " segundos.");
										p.sendMessage("§eO jogador " + enviou.getName() + " irá teleportar-se até si em " + cool + " segundos.");
									} else {
										enviou.sendMessage("§eTeleportando em 1 segundo.");
										p.sendMessage("§eO jogador " + enviou.getName() + " irá teleportar-se até si em 1 segundo.");
										plugin.sendTitle(enviou, "§6Teleportando", "§fem 1 segundo!");
									}
									cool--;
								} else {
									Bukkit.getScheduler().cancelTask(420);
								}
							}
						}, 20L, 20L*3);
						
						new BukkitRunnable(){
							int vermelho = 20;
							int verde = 0;
							String verde_cor = "§a▉";
							String vermelho_cor = "§c▉";
							public void run() {
								if(plugin.teleportando.contains(enviou.getName()) 
										&& 0 <= vermelho
										&& verde <= 20) {
									vermelho--;
									verde++;
									StringBuilder stg_verm = new StringBuilder();
									StringBuilder stg_verd = new StringBuilder();
									for(int verm = 0; verm <= vermelho; verm++)
										stg_verm.append(vermelho_cor);
									for(int verd = 0; verd <= verde; verd++)
										stg_verd.append(verde_cor);
									plugin.sendActionBar(enviou, stg_verd.toString() + stg_verm.toString());
								} else
								cancel();
							}
				               
						}.runTaskTimerAsynchronously(plugin, 5L, 10L);
						
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
				Player enviou = Bukkit.getPlayer(args[0]);
				if(enviou != null) {
					if(plugin.teleporte_pendente.get(enviou.getName()).equals(p.getName()) && plugin.teleporte_expirar.containsValue(p.getName())) {
						plugin.teleporte_expirar.remove(enviou.getName());
						plugin.teleporte_pendente.remove(enviou.getName());
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
		
		if(cmd.getName().equalsIgnoreCase("tpcancelar")) {
			if(plugin.teleportando.contains(p.getName())
					|| plugin.teleporte_pendente.containsKey(p.getName())) {
				if(plugin.teleportando.contains(p.getName())) 
					plugin.teleportando.remove(p.getName());
				if(plugin.teleporte_pendente.containsKey(p.getName())) {
					plugin.teleporte_pendente.remove(p.getName());
					plugin.teleporte_expirar.remove(p.getName());
				}
				p.sendMessage("§cVocê cancelou o seu teleporte.");
				plugin.sendTitle(p, "§6Teleporte", "§fcancelado!");
				return true;
				} else {
					p.sendMessage("§cVocê não tem pedidos de teleporte recentes.");
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
						plugin.sendTitle(p, "§6Teleportando", "§f" + target.getName() + " para você.");
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
