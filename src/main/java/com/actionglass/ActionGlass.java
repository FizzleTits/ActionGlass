package com.actionglass;

import com.actionglass.commands.ActionGlassCommand;
import com.actionglass.config.ConfigManager;
import com.actionglass.integrations.IntegrationManager;
import com.actionglass.listeners.GlassBreakListener;
import com.actionglass.managers.GlassManager;
import com.actionglass.managers.StatisticsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * ActionGlass - Realistic glass breaking mechanics for Minecraft servers
 * 
 * @author FizzleTi1s
 * @version 1.0.0
 */
public final class ActionGlass extends JavaPlugin {
    
    private static ActionGlass instance;
    
    // Core managers
    private ConfigManager configManager;
    private GlassManager glassManager;
    private StatisticsManager statisticsManager;
    private IntegrationManager integrationManager;
    
    // Listeners
    private GlassBreakListener glassBreakListener;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // ASCII art banner
        getLogger().info("  ___       _   _          _____ _               ");
        getLogger().info(" / _ \\     | | (_)        |  ___| |              ");
        getLogger().info("/ /_\\ \\ ___| |_ _  ___  _ _| |__ | | __ _ ___ ___ ");
        getLogger().info("|  _  |/ __| __| |/ _ \\| '_ \\  __|| |/ _` / __/ __|");
        getLogger().info("| | | | (__| |_| | (_) | | | |___| | (_| \\__ \\__ \\");
        getLogger().info("\\_| |_/\\___|\\__|_|\\___/|_| |_\\____/_|\\__,_|___/___/");
        getLogger().info("");
        getLogger().info("Version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString());
        getLogger().info("Realistic glass breaking mechanics for Minecraft servers");
        getLogger().info("");
        
        try {
            // Initialize core components
            initializeManagers();
            
            // Register listeners
            registerListeners();
            
            // Register commands
            registerCommands();
            
            // Initialize integrations
            initializeIntegrations();
            
            // Success message
            getLogger().info("ActionGlass has been enabled successfully!");
            getLogger().info("Glass breaking mechanics are now active.");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable ActionGlass!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        try {
            // Save statistics
            if (statisticsManager != null) {
                statisticsManager.saveStatistics();
            }
            
            // Regenerate all broken glass
            if (glassManager != null) {
                glassManager.regenerateAllGlass();
                getLogger().info("All broken glass has been regenerated.");
            }
            
            // Cleanup
            cleanup();
            
            getLogger().info("ActionGlass has been disabled successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error during plugin shutdown", e);
        }
        
        instance = null;
    }
    
    /**
     * Initialize all core managers
     */
    private void initializeManagers() {
        getLogger().info("Initializing core managers...");
        
        // Configuration manager
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Statistics manager
        statisticsManager = new StatisticsManager(this);
        
        // Glass manager (core functionality)
        glassManager = new GlassManager(this);
        
        getLogger().info("Core managers initialized successfully.");
    }
    
    /**
     * Register event listeners
     */
    private void registerListeners() {
        getLogger().info("Registering event listeners...");
        
        glassBreakListener = new GlassBreakListener(this);
        getServer().getPluginManager().registerEvents(glassBreakListener, this);
        
        getLogger().info("Event listeners registered successfully.");
    }
    
    /**
     * Register commands
     */
    private void registerCommands() {
        getLogger().info("Registering commands...");
        
        ActionGlassCommand commandExecutor = new ActionGlassCommand(this);
        getCommand("actionglass").setExecutor(commandExecutor);
        getCommand("actionglass").setTabCompleter(commandExecutor);
        
        getLogger().info("Commands registered successfully.");
    }
    
    /**
     * Initialize third-party integrations
     */
    private void initializeIntegrations() {
        getLogger().info("Initializing third-party integrations...");
        
        integrationManager = new IntegrationManager(this);
        integrationManager.initializeIntegrations();
        
        getLogger().info("Integration initialization completed.");
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        if (glassManager != null) {
            glassManager.shutdown();
        }
        
        if (integrationManager != null) {
            integrationManager.shutdown();
        }
    }
    
    /**
     * Reload the plugin configuration and managers
     */
    public void reloadPlugin() {
        try {
            getLogger().info("Reloading ActionGlass...");
            
            // Reload configuration
            configManager.reloadConfig();
            
            // Reload managers
            glassManager.reload();
            integrationManager.reload();
            
            getLogger().info("ActionGlass reloaded successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to reload ActionGlass!", e);
        }
    }
    
    // Getters for managers
    public static ActionGlass getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public GlassManager getGlassManager() {
        return glassManager;
    }
    
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }
    
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }
    
    /**
     * Check if debug mode is enabled
     */
    public boolean isDebugMode() {
        return configManager != null && configManager.isDebugMode();
    }
    
    /**
     * Log debug message if debug mode is enabled
     */
    public void debug(String message) {
        if (isDebugMode()) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
