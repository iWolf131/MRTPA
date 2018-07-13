package me.wolf_131.mrtpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.wolf_131.mrtpa.commands.OtherTpaCommands;
import me.wolf_131.mrtpa.commands.StaffCommands;
import me.wolf_131.mrtpa.commands.TpAceitarCommand;
import me.wolf_131.mrtpa.commands.TpNegarCommand;
import me.wolf_131.mrtpa.commands.TpaCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;;

public class Main extends JavaPlugin {
	
	public HashMap<String, Long> cooldown;
	public HashMap<String, String> teleporte_pendente;
	public HashMap<String, String> teleporte_expirar;
	public List<String> teleportando;
	public List<String> tptoggle;
	
	public static Main getPlugin() {
		return Main.getPlugin(Main.class);
	}
	
	public void onEnable() {
		cooldown = new HashMap<String, Long>();
		teleporte_pendente = new HashMap<String, String>();
		teleporte_expirar = new HashMap<String, String>();
		teleportando = new ArrayList<String>();
		tptoggle = new ArrayList<String>();
		registerCommands();
		Bukkit.getPluginManager().registerEvents(new Events(this), this);
	}
	
	public void onDisable() {
		teleportando.clear();
		tptoggle.clear();
		teleporte_expirar.clear();
		teleporte_pendente.clear();
		cooldown.clear();
		Bukkit.getScheduler().cancelAllTasks();
	}
	
	public void registerCommands() {
		getCommand("tpa").setExecutor(new TpaCommand());
		getCommand("tpnegar").setExecutor(new TpNegarCommand());
		getCommand("tptoggle").setExecutor(new OtherTpaCommands());
		getCommand("tpaceitar").setExecutor(new TpAceitarCommand());
		getCommand("tpcancelar").setExecutor(new OtherTpaCommands());
		getCommand("tpo").setExecutor(new StaffCommands());
		getCommand("tpaqui").setExecutor(new StaffCommands());
		getCommand("tpaleatorio").setExecutor(new StaffCommands());
	}
	
	public void sendJSONMsg(Player p, String text, String JSON, String text2, String hoverEvent, String command) {
		TextComponent msg = new TextComponent(text);
		TextComponent msg2 = new TextComponent(JSON);
		TextComponent msg3 = new TextComponent(text2);
		msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverEvent).create()));
		msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		msg.addExtra(msg2);
		msg.addExtra(msg3);
		p.spigot().sendMessage(msg);
	}
	
	public void sendTitle(Player p, String title, String subtitle) {
		PacketPlayOutTitle titulo = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":" + "\"" + title + "\"}"), 5, 5, 5);
		PacketPlayOutTitle subtitulo = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":" + "\"" + subtitle + "\"}"), 5, 5, 5);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(titulo);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitulo);
	}
	
    public void sendActionBar(Player p, String msg){
        PacketPlayOutChat action = new PacketPlayOutChat(new ChatComponentText(msg), (byte)2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(action);
    }
	
    public boolean msgCooldown(Player p) {
    	  if(!cooldown.containsKey(p.getName())) 
    	    return false;
    	  
    	  Long segundos = (cooldown.get(p.getName())/1000) - (System.currentTimeMillis()/1000);
    	  if (segundos > 0) {
    	    p.sendMessage("§cVocê precisa de esperar mais " + Long.toString(segundos) + " segundos!");
    	    return true;
    	  } else
    		  cooldown.remove(p.getName());    
    	  return false;
    }
	
}
