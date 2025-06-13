
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
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void saveConfig() {
        plugin.saveConfig();
    }
    
    // Glass restore time methods
    public int getGlassRestoreTime() {
        return config.getInt("glass.restore-time.normal", 30);
    }
    
    public int getGlassPaneRestoreTime() {
        return config.getInt("glass.restore-time.pane", 25);
    }
    
    public int getStainedGlassRestoreTime() {
        return config.getInt("glass.restore-time.stained", 35);
    }
    
    public int getStainedGlassPaneRestoreTime() {
        return config.getInt("glass.restore-time.stained-pane", 30);
    }
    
    public int getTintedGlassRestoreTime() {
        return config.getInt("glass.restore-time.tinted", 40);
    }
    
    // Sound configuration methods
    public boolean isGlassBreakSoundEnabled() {
        return config.getBoolean("effects.sounds.break.enabled", true);
    }
    
    public float getGlassBreakSoundVolume() {
        return (float) config.getDouble("effects.sounds.break.volume", 1.0);
    }
    
    public float getGlassBreakSoundPitch() {
        return (float) config.getDouble("effects.sounds.break.pitch", 1.0);
    }
    
    public boolean isGlassRestoreSoundEnabled() {
        return config.getBoolean("effects.sounds.restore.enabled", true);
    }
    
    public float getGlassRestoreSoundVolume() {
        return (float) config.getDouble("effects.sounds.restore.volume", 0.8);
    }
    
    public float getGlassRestoreSoundPitch() {
        return (float) config.getDouble("effects.sounds.restore.pitch", 1.2);
    }
    
    // Particle configuration methods
    public boolean isGlassBreakParticlesEnabled() {
        return config.getBoolean("effects.particles.break.enabled", true);
    }
    
    public int getGlassBreakParticleCount() {
        return config.getInt("effects.particles.break.count", 10);
    }
    
    public boolean isGlassRestoreParticlesEnabled() {
        return config.getBoolean("effects.particles.restore.enabled", true);
    }
    
    public int getGlassRestoreParticleCount() {
        return config.getInt("effects.particles.restore.count", 5);
    }
    
    // Feature toggles
    public boolean isPunchToBreakEnabled() {
        return config.getBoolean("features.punch-to-break", false);
    }
    
    public boolean isProjectileBreakEnabled() {
        return config.getBoolean("features.projectile-break", true);
    }
    
    public boolean isStatisticsEnabled() {
        return config.getBoolean("features.statistics", true);
    }
    
    // Debug configuration
    public boolean isDebugMode() {
        return config.getBoolean("debug.enabled", false);
    }
}
