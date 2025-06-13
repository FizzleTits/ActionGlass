package com.actionglass.commands;

import com.actionglass.ActionGlass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for ActionGlass plugin
 */
public class ActionGlassCommand implements CommandExecutor, TabCompleter {
    
    private final ActionGlass plugin;
    
    public ActionGlassCommand(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                return handleReload(sender);
            case "stats":
                return handleStats(sender);
            case "restore":
                return handleRestore(sender, args);
            case "info":
                return handleInfo(sender);
            case "help":
            default:
                sendHelpMessage(sender);
                return true;
        }
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("actionglass.admin.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        plugin.reloadPlugin();
        sender.sendMessage(ChatColor.GREEN + "ActionGlass has been reloaded!");
        return true;
    }
    
    private boolean handleStats(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        var stats = plugin.getStatisticsManager().getStats(player);
        
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Stats ===");
        sender.sendMessage(ChatColor.YELLOW + "Glass Breaks: " + ChatColor.WHITE + stats.glassBreaks);
        sender.sendMessage(ChatColor.YELLOW + "Total Blocks: " + ChatColor.WHITE + stats.totalBlocks);
        
        return true;
    }
    
    private boolean handleRestore(CommandSender sender, String[] args) {
        if (!sender.hasPermission("actionglass.admin.restore")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length > 1 && args[1].equalsIgnoreCase("all")) {
            int count = plugin.getGlassManager().getBrokenGlassCount();
            plugin.getGlassManager().restoreAllGlass();
            sender.sendMessage(ChatColor.GREEN + "Restored " + count + " glass blocks!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /actionglass restore all");
        }
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Info ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Broken Glass: " + ChatColor.WHITE + plugin.getGlassManager().getBrokenGlassCount());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors());
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass info" + ChatColor.WHITE + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass stats" + ChatColor.WHITE + " - Show your glass breaking stats");
        
        if (sender.hasPermission("actionglass.admin.reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass reload" + ChatColor.WHITE + " - Reload the plugin");
        }
        
        if (sender.hasPermission("actionglass.admin.restore")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass restore all" + ChatColor.WHITE + " - Restore all broken glass");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = Arrays.asList("help", "info", "stats");
            
            if (sender.hasPermission("actionglass.admin.reload")) {
                commands = new ArrayList<>(commands);
                commands.add("reload");
            }
            
            if (sender.hasPermission("actionglass.admin.restore")) {
                commands = new ArrayList<>(commands);
                commands.add("restore");
            }
            
            for (String cmd : commands) {
                if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("restore")) {
            if (sender.hasPermission("actionglass.admin.restore")) {
                completions.add("all");
            }
        }
        
        return completions;
    }
}
