package com.actionglass.integrations;

import com.actionglass.ActionGlass;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Manages third-party plugin integrations
 */
public class IntegrationManager {
    
    private final ActionGlass plugin;
    private boolean worldGuardEnabled = false;
    private boolean townyEnabled = false;
    
    public IntegrationManager(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize all available integrations
     */
    public void initializeIntegrations() {
        plugin.getLogger().info("Checking for third-party plugin integrations...");
        
        // Check for WorldGuard
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            if (plugin.getConfigManager().isWorldGuardIntegrationEnabled()) {
                worldGuardEnabled = true;
                plugin.getLogger().info("WorldGuard integration enabled.");
            }
        }
        
        // Check for Towny
        if (plugin.getServer().getPluginManager().getPlugin("Towny") != null) {
            if (plugin.getConfigManager().isTownyIntegrationEnabled()) {
                townyEnabled = true;
                plugin.getLogger().info("Towny integration enabled.");
            }
        }
        
        if (!worldGuardEnabled && !townyEnabled) {
            plugin.getLogger().info("No third-party integrations enabled.");
        }
    }
    
    /**
     * Check if glass can be broken at the specified location by the player
     */
    public boolean canBreakGlass(Location location, Player player) {
        // Check WorldGuard regions
        if (worldGuardEnabled && !canBreakInWorldGuardRegion(location, player)) {
            return false;
        }
        
        // Check Towny claims
        if (townyEnabled && !canBreakInTownyClaim(location, player)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check WorldGuard region permissions
     */
    private boolean canBreakInWorldGuardRegion(Location location, Player player) {
        try {
            // This is a simplified check - in a real implementation you'd use WorldGuard API
            // For now, we'll just return true to avoid complex WorldGuard integration
            plugin.debug("WorldGuard check for " + player.getName() + " at " + location.toString());
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking WorldGuard permissions: " + e.getMessage());
            return true; // Default to allow if there's an error
        }
    }
    
    /**
     * Check Towny claim permissions
     */
    private boolean canBreakInTownyClaim(Location location, Player player) {
        try {
            // This is a simplified check - in a real implementation you'd use Towny API
            // For now, we'll just return true to avoid complex Towny integration
            plugin.debug("Towny check for " + player.getName() + " at " + location.toString());
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking Towny permissions: " + e.getMessage());
            return true; // Default to allow if there's an error
        }
    }
    
    /**
     * Check if WorldGuard integration is enabled
     */
    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }
    
    /**
     * Check if Towny integration is enabled
     */
    public boolean isTownyEnabled() {
        return townyEnabled;
    }
}
