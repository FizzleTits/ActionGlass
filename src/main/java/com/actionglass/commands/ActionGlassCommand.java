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
 * Handles plugin commands
 */
public class ActionGlassCommand implements CommandExecutor, TabCompleter {
    
    private final ActionGlass plugin;
    
    public ActionGlassCommand(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "help":
                showHelp(sender);
                break;
                
            case "info":
                showInfo(sender);
                break;
                
            case "debug":
                toggleDebug(sender);
                break;
                
            case "reload":
                if (!sender.hasPermission("actionglass.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                reloadPlugin(sender);
                break;
                
            case "restore":
                if (!sender.hasPermission("actionglass.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                restoreGlass(sender);
                break;
                
            case "stats":
                showStats(sender);
                break;
                
            case "test":
                if (!sender.hasPermission("actionglass.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                testGlassBreaking(sender);
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /actionglass help for available commands.");
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass info" + ChatColor.WHITE + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass debug" + ChatColor.WHITE + " - Toggle debug mode");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass stats" + ChatColor.WHITE + " - Show your glass breaking statistics");
        
        if (sender.hasPermission("actionglass.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass reload" + ChatColor.WHITE + " - Reload plugin configuration");
            sender.sendMessage(ChatColor.YELLOW + "/actionglass restore" + ChatColor.WHITE + " - Restore all broken glass immediately");
            sender.sendMessage(ChatColor.YELLOW + "/actionglass test" + ChatColor.WHITE + " - Test glass breaking on target block");
        }
    }
    
    private void showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Glass Breaking: " + ChatColor.WHITE + 
                          (plugin.getConfigManager().isGlassBreakingEnabled() ? "Enabled" : "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Debug Mode: " + ChatColor.WHITE + 
                          (plugin.getConfigManager().isDebugMode() ? "Enabled" : "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Statistics: " + ChatColor.WHITE + 
                          (plugin.getConfigManager().isStatisticsEnabled() ? "Enabled" : "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Broken Glass Blocks: " + ChatColor.WHITE + 
                          plugin.getGlassManager().getBrokenGlassCount());
    }
    
    private void toggleDebug(CommandSender sender) {
        // Toggle debug mode in the config
        boolean currentDebug = plugin.getConfigManager().isDebugMode();
        boolean newDebug = !currentDebug;
        
        // Update the config
        plugin.getConfig().set("debug", newDebug);
        plugin.saveConfig();
        
        // Reload config to apply changes
        plugin.getConfigManager().reloadConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Debug mode " + (newDebug ? "enabled" : "disabled") + ".");
        
        if (newDebug) {
            sender.sendMessage(ChatColor.YELLOW + "Debug messages will now appear in console.");
            plugin.debug("Debug mode enabled by " + sender.getName());
        }
    }
    
    private void reloadPlugin(CommandSender sender) {
        try {
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "ActionGlass configuration reloaded successfully.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }
    
    private void restoreGlass(CommandSender sender) {
        int brokenCount = plugin.getGlassManager().getBrokenGlassCount();
        plugin.getGlassManager().restoreAllGlass();
        sender.sendMessage(ChatColor.GREEN + "Restored " + brokenCount + " broken glass blocks.");
    }
    
    private void showStats(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getStatisticsManager() == null || !plugin.getConfigManager().isStatisticsEnabled()) {
            sender.sendMessage(ChatColor.RED + "Statistics are disabled.");
            return;
        }
        
        int glassBreaks = plugin.getStatisticsManager().getGlassBreaks(player);
        sender.sendMessage(ChatColor.GOLD + "=== Your Glass Breaking Statistics ===");
        sender.sendMessage(ChatColor.YELLOW + "Glass Blocks Broken: " + ChatColor.WHITE + glassBreaks);
    }
    
    private void testGlassBreaking(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        org.bukkit.block.Block targetBlock = player.getTargetBlock(null, 10);
        
        if (targetBlock == null || targetBlock.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(ChatColor.RED + "Look at a glass block to test breaking.");
            return;
        }
        
        if (!plugin.getGlassManager().isBreakableGlass(targetBlock.getType())) {
            sender.sendMessage(ChatColor.RED + "Target block is not breakable glass: " + targetBlock.getType());
            return;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Testing glass breaking on " + targetBlock.getType() + " at " + targetBlock.getLocation());
        
        boolean canBreak = plugin.getGlassManager().canBreakGlass(targetBlock, player);
        sender.sendMessage(ChatColor.YELLOW + "Can break glass: " + canBreak);
        
        if (canBreak) {
            plugin.getGlassManager().breakGlass(targetBlock.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Glass broken successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "Glass cannot be broken (too thick or other restriction).");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = Arrays.asList("help", "info", "debug", "stats");
            
            if (sender.hasPermission("actionglass.admin")) {
                commands = new ArrayList<>(commands);
                commands.add("reload");
                commands.add("restore");
                commands.add("test");
            }
            
            String partial = args[0].toLowerCase();
            for (String cmd : commands) {
                if (cmd.startsWith(partial)) {
                    completions.add(cmd);
                }
            }
        }
        
        return completions;
    }
}




