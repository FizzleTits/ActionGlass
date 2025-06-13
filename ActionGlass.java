package com.actionglass;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/**
 * ActionGlass - Main plugin class
 * Realistic glass breaking mechanics for Minecraft servers
 */
public class ActionGlass extends JavaPlugin {
    
    private GlassManager glassManager;
    private GlassBreakListener glassBreakListener;
    
    // Configuration values cached for performance
    private boolean arrowBreakingEnabled;
    private boolean elytraBreakingEnabled;
    private boolean fallingBreakingEnabled;
    private boolean runningBreakingEnabled;
    private boolean regenerationEnabled;
    
    private double minimumSpeed;
    private long glassBreakCooldown;
    private long regenerationDelay;
    
    private int arrowBreakRadius;
    private int tridentBreakRadius;
    private int windChargeBreakRadius;
    private int elytraBreakRadius;
    private int fallingBreakRadius;
    private int runningBreakRadius;
    private int sprintingBreakRadius;
    
    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load configuration values
        loadConfiguration();
        
        // Initialize managers
        glassManager = new GlassManager(this);
        glassBreakListener = new GlassBreakListener(this);
        
        // Register event listener
        getServer().getPluginManager().registerEvents(glassBreakListener, this);
        
        // Log successful startup
        getLogger().info("ActionGlass has been enabled!");
        getLogger().info("Glass breaking mechanics are now active.");
    }
    
    @Override
    public void onDisable() {
        // Regenerate all broken glass on shutdown if enabled
        if (regenerationEnabled && glassManager != null) {
            glassManager.regenerateAllGlass();
            getLogger().info("Regenerated all broken glass blocks.");
        }
        
        getLogger().info("ActionGlass has been disabled.");
    }
    
    /**
     * Load configuration values from config.yml
     */
    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        
        // Feature toggles
        arrowBreakingEnabled = config.getBoolean("features.arrow-breaking", true);
        elytraBreakingEnabled = config.getBoolean("features.elytra-breaking", true);
        fallingBreakingEnabled = config.getBoolean("features.falling-breaking", true);
        runningBreakingEnabled = config.getBoolean("features.running-breaking", true);
        regenerationEnabled = config.getBoolean("features.regeneration", true);
        
        // Speed and timing settings
        minimumSpeed = config.getDouble("settings.minimum-speed", 0.3);
        glassBreakCooldown = config.getLong("settings.glass-break-cooldown", 500);
        regenerationDelay = config.getLong("settings.regeneration-delay", 6000);
        
        // Break radius settings
        arrowBreakRadius = config.getInt("break-radius.arrow", 1);
        tridentBreakRadius = config.getInt("break-radius.trident", 2);
        windChargeBreakRadius = config.getInt("break-radius.wind-charge", 3);
        elytraBreakRadius = config.getInt("break-radius.elytra", 2);
        fallingBreakRadius = config.getInt("break-radius.falling", 2);
        runningBreakRadius = config.getInt("break-radius.running", 1);
        sprintingBreakRadius = config.getInt("break-radius.sprinting", 2);
        
        getLogger().info("Configuration loaded successfully.");
    }
    
    /**
     * Handle plugin commands
     */
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
            case "reload":
                return handleReloadCommand(sender);
            case "status":
                return handleStatusCommand(sender);
            case "regenerate":
                return handleRegenerateCommand(sender);
            case "toggle":
                return handleToggleCommand(sender, args);
            case "help":
                sendHelpMessage(sender);
                return true;
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
        
        reloadConfig();
        loadConfiguration();
        sender.sendMessage(ChatColor.GREEN + "ActionGlass configuration reloaded successfully!");
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
        sender.sendMessage(ChatColor.YELLOW + "Arrow Breaking: " + (arrowBreakingEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Elytra Breaking: " + (elytraBreakingEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Falling Breaking: " + (fallingBreakingEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Running Breaking: " + (runningBreakingEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Regeneration: " + (regenerationEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Broken Glass Blocks: " + ChatColor.WHITE + glassManager.getBrokenGlassCount());
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
        
        int brokenCount = glassManager.getBrokenGlassCount();
        glassManager.regenerateAllGlass();
        sender.sendMessage(ChatColor.GREEN + "Regenerated " + brokenCount + " broken glass blocks!");
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
            sender.sendMessage(ChatColor.YELLOW + "Features: arrow, elytra, falling, running, regeneration");
            return true;
        }
        
        String feature = args[1].toLowerCase();
        boolean newValue;
        
        switch (feature) {
            case "arrow":
                arrowBreakingEnabled = !arrowBreakingEnabled;
                newValue = arrowBreakingEnabled;
                break;
            case "elytra":
                elytraBreakingEnabled = !elytraBreakingEnabled;
                newValue = elytraBreakingEnabled;
                break;
            case "falling":
                fallingBreakingEnabled = !fallingBreakingEnabled;
                newValue = fallingBreakingEnabled;
                break;
            case "running":
                runningBreakingEnabled = !runningBreakingEnabled;
                newValue = runningBreakingEnabled;
                break;
            case "regeneration":
                regenerationEnabled = !regenerationEnabled;
                newValue = regenerationEnabled;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown feature: " + feature);
                return true;
        }
        
        String status = newValue ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled";
        sender.sendMessage(ChatColor.YELLOW + "Feature '" + feature + "' has been " + status + ChatColor.YELLOW + "!");
        return true;
    }
    
    /**
     * Send help message
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ActionGlass Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass status" + ChatColor.WHITE + " - Show plugin status");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass reload" + ChatColor.WHITE + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass regenerate" + ChatColor.WHITE + " - Regenerate all broken glass");
        sender.sendMessage(ChatColor.YELLOW + "/actionglass toggle <feature>" + ChatColor.WHITE + " - Toggle a feature on/off");
    }
    
    // Getter methods for configuration values
    public boolean isArrowBreakingEnabled() { return arrowBreakingEnabled; }
    public boolean isElytraBreakingEnabled() { return elytraBreakingEnabled; }
    public boolean isFallingBreakingEnabled() { return fallingBreakingEnabled; }
    public boolean isRunningBreakingEnabled() { return runningBreakingEnabled; }
    public boolean isRegenerationEnabled() { return regenerationEnabled; }
    
    public double getMinimumSpeed() { return minimumSpeed; }
    public long getGlassBreakCooldown() { return glassBreakCooldown; }
    public long getRegenerationDelay() { return regenerationDelay; }
    
    public int getProjectileBreakRadius(String projectileType) {
        switch (projectileType.toLowerCase()) {
            case "arrow": return arrowBreakRadius;
            case "trident": return tridentBreakRadius;
            case "wind-charge": return windChargeBreakRadius;
            default: return 1;
        }
    }
    
    public int getMovementBreakRadius(String movementType) {
        switch (movementType.toLowerCase()) {
            case "elytra": return elytraBreakRadius;
            case "falling": return fallingBreakRadius;
            case "running": return runningBreakRadius;
            case "sprinting": return sprintingBreakRadius;
            default: return 1;
        }
    }
    
    public GlassManager getGlassManager() { return glassManager; }
}
