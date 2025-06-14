package com.actionglass.config;

import com.actionglass.ActionGlass;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages plugin configuration
 */
public class ConfigManager {
    
    private final ActionGlass plugin;
    private FileConfiguration config;
    
    public ConfigManager(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load configuration from file
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // Validate config version
        if (config.getInt("config-version", 0) < 1) {
            plugin.getLogger().warning("Configuration file is outdated. Please delete config.yml to generate a new one.");
        }
    }
    
    /**
     * Reload configuration
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    // Glass breaking settings
    public boolean isGlassBreakingEnabled() {
        return config.getBoolean("glass-breaking.enabled", true);
    }
    
    public double getMinimumSpeed() {
        return config.getDouble("glass-breaking.minimum-speed", 0.5);
    }
    
    public int getGlassRestoreTime() {
        return config.getInt("glass-breaking.restore-time", 30);
    }
    
    // Elytra breaking
    public boolean isElytraBreakingEnabled() {
        return config.getBoolean("glass-breaking.elytra-breaking", true);
    }
    
    public double getElytraMinSpeed() {
        return config.getDouble("glass-breaking.elytra-min-speed", 0.8);
    }
    
    public double getElytraBreakRadius() {
        return config.getDouble("glass-breaking.elytra-break-radius", 1.5);
    }
    
    // Sprint breaking
    public boolean isSprintingBreakingEnabled() {
        return config.getBoolean("glass-breaking.sprint-breaking", true);
    }
    
    public double getSprintingMinSpeed() {
        return config.getDouble("glass-breaking.sprint-min-speed", 0.6);
    }
    
    public double getSprintingBreakRadius() {
        return config.getDouble("glass-breaking.sprint-break-radius", 1.0);
    }
    
    // Fall breaking
    public boolean isFallingBreakingEnabled() {
        return config.getBoolean("glass-breaking.fall-breaking", true);
    }
    
    public double getFallBreakHeight() {
        return config.getDouble("glass-breaking.fall-break-height", 6.0);
    }
    
    public double getFallingMinSpeed() {
        return config.getDouble("glass-breaking.fall-min-speed", 0.7);
    }
    
    public double getFallingBreakRadius() {
        return config.getDouble("glass-breaking.fall-break-radius", 1.2);
    }
    
    // Sound effects
    public boolean isGlassBreakSoundEnabled() {
        return config.getBoolean("effects.sounds.glass-break.enabled", true);
    }
    
    public float getGlassBreakSoundVolume() {
        return (float) config.getDouble("effects.sounds.glass-break.volume", 1.0);
    }
    
    public float getGlassBreakSoundPitch() {
        return (float) config.getDouble("effects.sounds.glass-break.pitch", 1.0);
    }
    
    public boolean isGlassRestoreSoundEnabled() {
        return config.getBoolean("effects.sounds.glass-restore.enabled", true);
    }
    
    public float getGlassRestoreSoundVolume() {
        return (float) config.getDouble("effects.sounds.glass-restore.volume", 0.5);
    }
    
    public float getGlassRestoreSoundPitch() {
        return (float) config.getDouble("effects.sounds.glass-restore.pitch", 1.2);
    }
    
    // Particle effects
    public boolean isGlassBreakParticlesEnabled() {
        return config.getBoolean("effects.particles.glass-break.enabled", true);
    }
    
    public int getGlassBreakParticleCount() {
        return config.getInt("effects.particles.glass-break.count", 20);
    }
    
    public boolean isGlassRestoreParticlesEnabled() {
        return config.getBoolean("effects.particles.glass-restore.enabled", true);
    }
    
    public int getGlassRestoreParticleCount() {
        return config.getInt("effects.particles.glass-restore.count", 10);
    }
    
    // Statistics
    public boolean isStatisticsEnabled() {
        return config.getBoolean("statistics.enabled", true);
    }
    
    // Integrations
    public boolean isWorldGuardIntegrationEnabled() {
        return config.getBoolean("integrations.worldguard.enabled", true);
    }
    
    public boolean isTownyIntegrationEnabled() {
        return config.getBoolean("integrations.towny.enabled", true);
    }
    
    // Debug
    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }
}


