package com.actionglass.managers;

import com.actionglass.ActionGlass;
import com.actionglass.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages glass breaking and restoration mechanics
 */
public class GlassManager {
    
    private final ActionGlass plugin;
    private final ConfigManager configManager;
    private final Map<Location, GlassBlock> brokenGlass;
    private final Map<Material, Integer> restoreTimes;
    
    public GlassManager(ActionGlass plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.brokenGlass = new ConcurrentHashMap<>();
        this.restoreTimes = new HashMap<>();
        
        initializeRestoreTimes();
    }
    
    private void initializeRestoreTimes() {
        // Load restore times from config
        restoreTimes.put(Material.GLASS, configManager.getGlassRestoreTime());
        restoreTimes.put(Material.GLASS_PANE, configManager.getGlassPaneRestoreTime());
        restoreTimes.put(Material.WHITE_STAINED_GLASS, configManager.getStainedGlassRestoreTime());
        restoreTimes.put(Material.WHITE_STAINED_GLASS_PANE, configManager.getStainedGlassPaneRestoreTime());
        
        // Add all stained glass colors
        for (Material material : Material.values()) {
            if (material.name().contains("STAINED_GLASS") && !material.name().contains("PANE")) {
                restoreTimes.put(material, configManager.getStainedGlassRestoreTime());
            } else if (material.name().contains("STAINED_GLASS_PANE")) {
                restoreTimes.put(material, configManager.getStainedGlassPaneRestoreTime());
            }
        }
        
        // Tinted glass (1.17+)
        if (Material.getMaterial("TINTED_GLASS") != null) {
            restoreTimes.put(Material.valueOf("TINTED_GLASS"), configManager.getTintedGlassRestoreTime());
        }
    }
    
    /**
     * Check if a material is breakable glass
     */
    public boolean isBreakableGlass(Material material) {
        return restoreTimes.containsKey(material);
    }
    
    /**
     * Check if a player can break glass at the given location
     */
    public boolean canBreakGlass(Block block, Player player) {
        // Check if glass is already broken
        if (brokenGlass.containsKey(block.getLocation())) {
            return false;
        }
        
        // Add integration checks here (WorldGuard, PlotSquared, etc.)
        return true;
    }
    
    /**
     * Break glass at the specified location
     */
    public void breakGlass(Location location) {
        Block block = location.getBlock();
        Material originalMaterial = block.getType();
        
        if (!isBreakableGlass(originalMaterial)) {
            return;
        }
        
        // Store the original glass block
        GlassBlock glassBlock = new GlassBlock(originalMaterial, block.getBlockData());
        brokenGlass.put(location, glassBlock);
        
        // Break the glass (set to air)
        block.setType(Material.AIR);
        
        // Play break effects
        playBreakEffects(location);
        
        // Schedule restoration
        scheduleRestore(location, originalMaterial);
    }
    
    private void playBreakEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // Play sound
        if (configManager.isGlassBreakSoundEnabled()) {
            world.playSound(location, Sound.BLOCK_GLASS_BREAK, 
                           configManager.getGlassBreakSoundVolume(), 
                           configManager.getGlassBreakSoundPitch());
        }
        
        // Spawn particles
        if (configManager.isGlassBreakParticlesEnabled()) {
            world.spawnParticle(Particle.BLOCK_CRACK, location.add(0.5, 0.5, 0.5), 
                              configManager.getGlassBreakParticleCount());
        }
    }
    
    private void scheduleRestore(Location location, Material material) {
        int restoreTime = restoreTimes.getOrDefault(material, configManager.getGlassRestoreTime());
        
        new BukkitRunnable() {
            @Override
            public void run() {
                restoreGlass(location);
            }
        }.runTaskLater(plugin, restoreTime * 20L); // Convert seconds to ticks
    }
    
    /**
     * Restore glass at the specified location
     */
    public void restoreGlass(Location location) {
        GlassBlock glassBlock = brokenGlass.remove(location);
        if (glassBlock == null) return;
        
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            // Block has been replaced, don't restore
            return;
        }
        
        // Restore the glass
        block.setType(glassBlock.material);
        block.setBlockData(glassBlock.blockData);
        
        // Play restore effects
        playRestoreEffects(location);
    }
    
    /**
     * Restore glass with specific glass block data
     */
    public void restoreGlass(Location location, GlassBlock glassBlock) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            return;
        }
        
        block.setType(glassBlock.material);
        block.setBlockData(glassBlock.blockData);
        
        playRestoreEffects(location);
    }
    
    private void playRestoreEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // Play sound
        if (configManager.isGlassRestoreSoundEnabled()) {
            world.playSound(location, Sound.BLOCK_GLASS_PLACE, 
                           configManager.getGlassRestoreSoundVolume(), 
                           configManager.getGlassRestoreSoundPitch());
        }
        
        // Spawn particles
        if (configManager.isGlassRestoreParticlesEnabled()) {
            world.spawnParticle(Particle.VILLAGER_HAPPY, location.add(0.5, 0.5, 0.5), 
                              configManager.getGlassRestoreParticleCount());
        }
    }
    
    /**
     * Get the count of currently broken glass blocks
     */
    public int getBrokenGlassCount() {
        return brokenGlass.size();
    }
    
    /**
     * Restore all broken glass immediately
     */
    public void restoreAllGlass() {
        for (Map.Entry<Location, GlassBlock> entry : brokenGlass.entrySet()) {
            restoreGlass(entry.getKey(), entry.getValue());
        }
        brokenGlass.clear();
    }
    
    /**
     * Regenerate all glass (alias for restoreAllGlass)
     */
    public void regenerateAllGlass() {
        restoreAllGlass();
    }
    
    /**
     * Get restore time for a specific material
     */
    public int getRestoreTime(Material material) {
        return restoreTimes.getOrDefault(material, configManager.getGlassRestoreTime());
    }
    
    /**
     * Reload glass manager settings
     */
    /**
     * Shutdown the glass manager
     */
    public void shutdown() {
        // Restore all broken glass before shutdown
        restoreAllGlass();
        plugin.getLogger().info("GlassManager shutdown complete");
    }

    public void reload() {
        restoreTimes.clear();
        initializeRestoreTimes();
    }
    
    /**
     * Inner class to store glass block data
     */
    public static class GlassBlock {
        public final Material material;
        public final org.bukkit.block.data.BlockData blockData;
        
        public GlassBlock(Material material, org.bukkit.block.data.BlockData blockData) {
            this.material = material;
            this.blockData = blockData;
        }
    }
}
