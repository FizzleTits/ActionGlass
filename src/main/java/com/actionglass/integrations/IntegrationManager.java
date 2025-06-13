package com.actionglass.integrations;

import com.actionglass.ActionGlass;
import org.bukkit.Bukkit;

public class IntegrationManager {
    private final ActionGlass plugin;
    private boolean worldGuardEnabled = false;
    private boolean plotSquaredEnabled = false;
    
    public IntegrationManager(ActionGlass plugin) {
        this.plugin = plugin;
        checkIntegrations();
    }
    
    private void checkIntegrations() {
        // Check for WorldGuard
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardEnabled = true;
            plugin.getLogger().info("WorldGuard integration enabled");
        }
        
        // Check for PlotSquared
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") != null) {
            plotSquaredEnabled = true;
            plugin.getLogger().info("PlotSquared integration enabled");
        }
    }
    
    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }
    
    public boolean isPlotSquaredEnabled() {
        return plotSquaredEnabled;
    }
    
    public void initializeIntegrations() {
        // Initialize any additional integration features
        plugin.getLogger().info("Integrations initialized");
    }
    
    public void shutdown() {
        // Cleanup integration resources
        plugin.getLogger().info("Integrations shutdown");
    }
    
    public void reload() {
        // Reload integration settings
        checkIntegrations();
        plugin.getLogger().info("Integrations reloaded");
    }
}
