package me.wolf_131.mrtpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;;

public class Main extends JavaPlugin{
	
	public HashMap<String, Long> cooldown = new HashMap<String, Long>();
	public HashMap<String, String> teleporte_pendente = new HashMap<String, String>();
	public HashMap<String, String> teleporte_expirar = new HashMap<String, String>();
	public List<String> teleportando = new  ArrayList<String>();
	public List<String> tptoggle = new  ArrayList<String>();
	
	public void onEnable() {
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
		this.getCommand("tpa").setExecutor(new Commands(this));
		this.getCommand("tpnegar").setExecutor(new Commands(this));
		this.getCommand("tptoggle").setExecutor(new Commands(this));
		this.getCommand("tpaceitar").setExecutor(new Commands(this));
		this.getCommand("tpcancelar").setExecutor(new Commands(this));
		this.getCommand("tpo").setExecutor(new Commands(this));
		this.getCommand("tpaqui").setExecutor(new Commands(this));
	}
	
	public void sendJSONMsg(Player p, String text, String JSON, String text2, String hoverEvent, String command) {
		TextComponent msg = new TextComponent(text);
		TextComponent msg_json = new TextComponent(JSON);
		TextComponent msg3 = new TextComponent(text2);
		msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverEvent).create()));
		msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		msg.addExtra(msg_json);
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
