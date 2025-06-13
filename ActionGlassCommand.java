package com.actionglass;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ActionGlassCommand - Handles all plugin commands
 * Provides administrative control over glass breaking mechanics
 */
public class ActionGlassCommand implements CommandExecutor, TabCompleter {
    
    private final ActionGlass plugin;
    
    public ActionGlassCommand(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("actionglass")) {
            return false;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelpMessage(sender);
                return true;
            case "reload":
                return handleReloadCommand(sender);
            case "status":
                return handleStatusCommand(sender);
            case "regenerate":
                return handleRegenerateCommand(sender);
            case "toggle":
                return handleToggleCommand(sender, args);
            case "info":
                return handleInfoCommand(sender);
            case "version":
                return handleVersionCommand(sender);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /actionglass help for available commands.");
                return true;
        }
    }
    
    /**
     * Handle reload command
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("actionglass.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin.");
            return true;
        }
        
        try {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "ActionGlass configuration reloaded successfully!");
            plugin.getLogger().info(sender.getName() + " reloaded the ActionGlass configuration.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * Handle status command
     */
    private boolean handleStatusCommand(CommandSender sender) {
        if (!sender.hasPermission("actionglass.status")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to view plugin status.");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Status ===");
        sender.sendMessage(ChatColor.YELLOW + "Plugin Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Arrow Breaking: " + getStatusColor(plugin.isArrowBreakingEnabled()) + getStatusText(plugin.isArrowBreakingEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Elytra Breaking: " + getStatusColor(plugin.isElytraBreakingEnabled()) + getStatusText(plugin.isElytraBreakingEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Falling Breaking: " + getStatusColor(plugin.isFallingBreakingEnabled()) + getStatusText(plugin.isFallingBreakingEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Running Breaking: " + getStatusColor(plugin.isRunningBreakingEnabled()) + getStatusText(plugin.isRunningBreakingEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Regeneration: " + getStatusColor(plugin.isRegenerationEnabled()) + getStatusText(plugin.isRegenerationEnabled()));
        sender.sendMessage(ChatColor.YELLOW + "Minimum Speed: " + ChatColor.WHITE + plugin.getMinimumSpeed());
        sender.sendMessage(ChatColor.YELLOW + "Break Cooldown: " + ChatColor.WHITE + plugin.getGlassBreakCooldown() + "ms");
        sender.sendMessage(ChatColor.YELLOW + "Regeneration Delay: " + ChatColor.WHITE + plugin.getRegenerationDelay() + "ms");
        sender.sendMessage(ChatColor.YELLOW + "Broken Glass Blocks: " + ChatColor.WHITE + plugin.getGlassManager().getBrokenGlassCount());
        
        return true;
    }
    
    /**
     * Handle regenerate command
     */
    private boolean handleRegenerateCommand(CommandSender sender) {
        if (!sender.hasPermission("actionglass.regenerate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to regenerate glass.");
            return true;
        }
        
        int brokenCount = plugin.getGlassManager().getBrokenGlassCount();
        if (brokenCount == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No broken glass blocks to regenerate.");
            return true;
        }
        
        plugin.getGlassManager().regenerateAllGlass();
        sender.sendMessage(ChatColor.GREEN + "Successfully regenerated " + brokenCount + " broken glass blocks!");
        plugin.getLogger().info(sender.getName() + " regenerated " + brokenCount + " glass blocks.");
        
        return true;
    }
    
    /**
     * Handle toggle command
     */
    private boolean handleToggleCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("actionglass.toggle")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to toggle features.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /actionglass toggle <feature>");
            sender.sendMessage(ChatColor.YELLOW + "Available features:");
            sender.sendMessage(ChatColor.WHITE + "  - arrow (arrow breaking)");
            sender.sendMessage(ChatColor.WHITE + "  - elytra (elytra breaking)");
            sender.sendMessage(ChatColor.WHITE + "  - falling (falling breaking)");
            sender.sendMessage(ChatColor.WHITE + "  - running (running breaking)");
            sender.sendMessage(ChatColor.WHITE + "  - regeneration (glass regeneration)");
            return true;
        }
        
        String feature = args[1].toLowerCase();
        
        // Note: This is a temporary toggle that doesn't persist
        // For persistent changes, users should edit the config file
        sender.sendMessage(ChatColor.YELLOW + "Note: This toggle is temporary and will reset on server restart.");
        sender.sendMessage(ChatColor.YELLOW + "For permanent changes, edit the config.yml file and use /actionglass reload.");
        
        switch (feature) {
            case "arrow":
                sender.sendMessage(ChatColor.YELLOW + "Arrow breaking is currently: " + 
                    getStatusColor(plugin.isArrowBreakingEnabled()) + getStatusText(plugin.isArrowBreakingEnabled()));
                break;
            case "elytra":
                sender.sendMessage(ChatColor.YELLOW + "Elytra breaking is currently: " + 
                    getStatusColor(plugin.isElytraBreakingEnabled()) + getStatusText(plugin.isElytraBreakingEnabled()));
                break;
            case "falling":
                sender.sendMessage(ChatColor.YELLOW + "Falling breaking is currently: " + 
                    getStatusColor(plugin.isFallingBreakingEnabled()) + getStatusText(plugin.isFallingBreakingEnabled()));
                break;
            case "running":
                sender.sendMessage(ChatColor.YELLOW + "Running breaking is currently: " + 
                    getStatusColor(plugin.isRunningBreakingEnabled()) + getStatusText(plugin.isRunningBreakingEnabled()));
                break;
            case "regeneration":
                sender.sendMessage(ChatColor.YELLOW + "Glass regeneration is currently: " + 
                    getStatusColor(plugin.isRegenerationEnabled()) + getStatusText(plugin.isRegenerationEnabled()));
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown feature: " + feature);
                sender.sendMessage(ChatColor.YELLOW + "Use /actionglass toggle without arguments to see available features.");
                return true;
        }
        
        sender.sendMessage(ChatColor.GRAY + "To change this setting, edit config.yml and use /actionglass reload.");
        return true;
    }
    
    /**
     * Handle info command
     */
    private boolean handleInfoCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + String.join(", ", plugin.getDescription().getAuthors()));
        sender.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + plugin.getDescription().getDescription());
        sender.sendMessage(ChatColor.YELLOW + "Website: " + ChatColor.AQUA + plugin.getDescription().getWebsite());
        
        return true;
    }
    
    /**
     * Handle version command
     */
    private boolean handleVersionCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "ActionGlass " + ChatColor.WHITE + "v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GRAY + "Running on " + plugin.getServer().getName() + " " + plugin.getServer().getVersion());
        
        return true;
    }
    
    /**
     * Send help message
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass info" + ChatColor.WHITE + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass version" + ChatColor.WHITE + " - Show plugin version");
        
        if (sender.hasPermission("actionglass.status")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass status" + ChatColor.WHITE + " - Show plugin status");
        }
        
        if (sender.hasPermission("actionglass.reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass reload" + ChatColor.WHITE + " - Reload configuration");
        }
        
        if (sender.hasPermission("actionglass.regenerate")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass regenerate" + ChatColor.WHITE + " - Regenerate all broken glass");
        }
        
        if (sender.hasPermission("actionglass.toggle")) {
            sender.sendMessage(ChatColor.YELLOW + "/actionglass toggle <feature>" + ChatColor.WHITE + " - View feature status");
        }
        
        sender.sendMessage(ChatColor.GRAY + "Aliases: /ag, /glass");
    }
    
    /**
     * Get status color for boolean values
     */
    private ChatColor getStatusColor(boolean enabled) {
        return enabled ? ChatColor.GREEN : ChatColor.RED;
    }
    
    /**
     * Get status text for boolean values
     */
    private String getStatusText(boolean enabled) {
        return enabled ? "Enabled" : "Disabled";
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument - subcommands
            List<String> subCommands = Arrays.asList("help", "info", "version");
            
            if (sender.hasPermission("actionglass.status")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("status");
            }
            
            if (sender.hasPermission("actionglass.reload")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("reload");
            }
            
            if (sender.hasPermission("actionglass.regenerate")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("regenerate");
            }
            
            if (sender.hasPermission("actionglass.toggle")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("toggle");
            }
            
            String input = args[0].toLowerCase();
            for (String cmd : subCommands) {
                if (cmd.startsWith(input)) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            // Second argument for toggle command
            List<String> features = Arrays.asList("arrow", "elytra", "falling", "running", "regeneration");
            String input = args[1].toLowerCase();
            
            for (String feature : features) {
                if (feature.startsWith(input)) {
                    completions.add(feature);
                }
            }
        }
        
        return completions;
    }
}
