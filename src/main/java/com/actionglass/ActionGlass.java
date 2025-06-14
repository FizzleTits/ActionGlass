package com.actionglass;

import com.actionglass.commands.ActionGlassCommand;
import com.actionglass.config.ConfigManager;
import com.actionglass.listeners.GlassBreakListener;
import com.actionglass.listeners.MovementListener;
import com.actionglass.listeners.ProjectileListener;
import com.actionglass.managers.GlassManager;
import com.actionglass.managers.StatisticsManager;
import com.actionglass.integrations.IntegrationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionGlass extends JavaPlugin {
    
    private ConfigManager configManager;
    private GlassManager glassManager;
    private StatisticsManager statisticsManager;
    private IntegrationManager integrationManager;
    
    @Override
    public void onEnable() {
        // Initialize managers
        this.configManager = new ConfigManager(this);
        configManager.loadConfig(); // Load the config
        this.glassManager = new GlassManager(this);
        this.statisticsManager = new StatisticsManager(this);
        this.integrationManager = new IntegrationManager(this);
        integrationManager.initializeIntegrations();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new GlassBreakListener(this), this);
        
        // Register commands
        getCommand("actionglass").setExecutor(new ActionGlassCommand(this));
        
        getLogger().info("ActionGlass plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ActionGlass shutting down...");
        
        // Clean up broken glass before shutdown
        if (glassManager != null) {
            getLogger().info("Regenerating broken glass blocks...");
            glassManager.regenerateAllGlass();
            glassManager.cleanup();
        }
        
        // Save statistics
        if (statisticsManager != null) {
            getLogger().info("Saving player statistics...");
            statisticsManager.saveStatistics();

        // Cancel all scheduled tasks to prevent memory leaks
        getServer().getScheduler().cancelTasks(this);
        }
        
        getLogger().info("ActionGlass plugin disabled!");
    }
    
    // Debug method that other classes are calling
    public void debug(String message) {
        if (configManager != null && configManager.isDebugMode()) {
            getLogger().info("[DEBUG] " + message);
        }
    }
    
    // Reload method that commands are calling
    public void reloadPlugin() {
        if (configManager != null) {
            configManager.reloadConfig();
        }
        getLogger().info("ActionGlass plugin reloaded!");
    }
    
    // Getters for managers
    public ConfigManager getConfigManager() { return configManager; }
    public GlassManager getGlassManager() { return glassManager; }
    public StatisticsManager getStatisticsManager() { return statisticsManager; }
    public IntegrationManager getIntegrationManager() { return integrationManager; }
}
