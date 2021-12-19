package jp.minecraft.hibi_10000.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlockControl extends JavaPlugin implements Listener {
	//File config = new File(getDataFolder(), "config.yml");
	FileConfiguration config = getConfig();
	String ver = "1.0.0";

	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this,this);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(blockCancel(e.getPlayer(), e.getBlock(), e.getBlock().getType()));
	}

	@EventHandler
	public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
		e.setCancelled(blockCancel(e.getPlayer(), e.getBlock(), e.getBlock().getType()));
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		Material b = Material.getMaterial(e.getBucket().name().replace("_BUCKET", ""));

		e.setCancelled(blockCancel(e.getPlayer(), e.getBlock(), b));
	}

	public boolean blockCancel(Player p, Block tb, Material b) {
		if (getBanBlock().contains(b.name())) {
			if (getWhiteWorld().contains(tb.getWorld().getName())) return false;
			if (!(p.hasPermission(new StringBuilder().append("blockcontrol.plase.").append(b.name()).toString()))) {
				for (Player onpl : Bukkit.getOnlinePlayers()) {
					//onpl.sendMessage(new StringBuilder().append("§c[BlockControl] ").append(p.getName()).append(" が ").append(b.name()).append(" をワールド ").append(tb.getWorld().getName()).append(" で設置しようとしました。").toString());
					onpl.sendMessage(new StringBuilder().append("§c[BlockControl] ").append(p.getName()).append(" が ").append(b.name()).append(" を ").append(tb.getWorld().getName()).append("(").append(tb.getX()).append("/").append(tb.getY()).append("/").append(tb.getZ()).append(")で設置しようとしました。").toString());
				}
				System.out.println(new StringBuilder().append("§c[BlockControl] ").append(p.getName()).append(" が ").append(b.name()).append(" を ").append(tb.getWorld().getName()).append("(").append(tb.getX()).append("/").append(tb.getY()).append("/").append(tb.getZ()).append(")で設置しようとしました。").toString());
				return true;
			}
		}
		return false;
	}

	public List<String> getBanBlock() {
		List<String> bbl;
		bbl = (List<String>) config.get("BanBlock");
		if (bbl.isEmpty()) bbl.add("null");
		return bbl;
	}

	public List<String> getWhiteWorld() {
		List<String> wwl;
		wwl = (List<String>) config.get("WhiteWorld");
		if (wwl.isEmpty()) wwl.add("null");
		return wwl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bc")) {
			if (!(sender.hasPermission("blockcontrol.command"))) {
				sender.sendMessage("§a[BlockControl] §c権限が不足しています。");
				return false;
			}
			if (args.length == 0) {
				sender.sendMessage(new StringBuilder().append("§a[BlockControl] §cコマンドが間違っています。/").append(label).append(" help で使用法を確認してください。").toString());
				return false;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				reloadConfig();
				config = getConfig();
				sender.sendMessage("§a[BlockControl] §b設定をリロードしました。");
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				//if (args[1].equalsIgnoreCase("version")) {
				//	sender.sendMessage(new StringBuilder().append("§a[BlockControl §b").append(ver).append("§a]").toString());
					//sender.sendMessage(" ");
				//	return true;
				//}
				sender.sendMessage("§a[BlockControl] Version" + ver + " §6help");

				sender.sendMessage(" §6[Command]");
				sender.sendMessage(" §b- /bc reload     §rコンフィグファイルをリロードします。");
				sender.sendMessage(" §b- /bc help        §rヘルプを表示します。");

				sender.sendMessage(" §6[Permission]");
				sender.sendMessage(" §b- blockcontrol     §r=blockcontrol.*");
				sender.sendMessage(" §b- blockcontrol.command     §rCommand run permission.");
				sender.sendMessage(" §b- blockcontrol.plase     §r=blockcontrol.plase.*");
				sender.sendMessage(" §b- blockcontrol.plase.[BlockName(大文字)]");
				sender.sendMessage("        §r BanBlock plase permission in not WhiteWorld");
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (cmd.getName().equalsIgnoreCase("bc")) {
			list.clear();
			list.add("reload");
			list.add("help");
			return list;
		}
		list.clear();
		return list;
	}
}
