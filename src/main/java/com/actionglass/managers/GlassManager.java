package com.actionglass.managers;

import com.actionglass.ActionGlass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Manages glass breaking and regeneration mechanics
 */
public class GlassManager {
    
    private final ActionGlass plugin;
    private final Map<Location, GlassData> brokenGlass = new ConcurrentHashMap<>();
    private final Map<Location, Integer> regenerationTasks = new ConcurrentHashMap<>();
    private Location normalizeLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    
    // All 6 directions to check for thickness
    private static final BlockFace[] ALL_FACES = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, 
        BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };
    
    public GlassManager(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Data class to store glass information
     */
    private static class GlassData {
        final Material material;
        final BlockData blockData;
        
        GlassData(Material material, BlockData blockData) {
            this.material = material;
            this.blockData = blockData;
        }
    }
    
    /**
     * Check if a material is breakable glass
     */
    public boolean isBreakableGlass(Material material) {
        if (material == null) return false;
        return material == Material.GLASS ||
               material == Material.GLASS_PANE ||
               material == Material.WHITE_STAINED_GLASS ||
               material == Material.ORANGE_STAINED_GLASS ||
               material == Material.MAGENTA_STAINED_GLASS ||
               material == Material.LIGHT_BLUE_STAINED_GLASS ||
               material == Material.YELLOW_STAINED_GLASS ||
               material == Material.LIME_STAINED_GLASS ||
               material == Material.PINK_STAINED_GLASS ||
               material == Material.GRAY_STAINED_GLASS ||
               material == Material.LIGHT_GRAY_STAINED_GLASS ||
               material == Material.CYAN_STAINED_GLASS ||
               material == Material.PURPLE_STAINED_GLASS ||
               material == Material.BLUE_STAINED_GLASS ||
               material == Material.BROWN_STAINED_GLASS ||
               material == Material.GREEN_STAINED_GLASS ||
               material == Material.RED_STAINED_GLASS ||
               material == Material.BLACK_STAINED_GLASS ||
               material == Material.WHITE_STAINED_GLASS_PANE ||
               material == Material.ORANGE_STAINED_GLASS_PANE ||
               material == Material.MAGENTA_STAINED_GLASS_PANE ||
               material == Material.LIGHT_BLUE_STAINED_GLASS_PANE ||
               material == Material.YELLOW_STAINED_GLASS_PANE ||
               material == Material.LIME_STAINED_GLASS_PANE ||
               material == Material.PINK_STAINED_GLASS_PANE ||
               material == Material.GRAY_STAINED_GLASS_PANE ||
               material == Material.LIGHT_GRAY_STAINED_GLASS_PANE ||
               material == Material.CYAN_STAINED_GLASS_PANE ||
               material == Material.PURPLE_STAINED_GLASS_PANE ||
               material == Material.BLUE_STAINED_GLASS_PANE ||
               material == Material.BROWN_STAINED_GLASS_PANE ||
               material == Material.GREEN_STAINED_GLASS_PANE ||
               material == Material.RED_STAINED_GLASS_PANE ||
               material == Material.BLACK_STAINED_GLASS_PANE ||
               material == Material.TINTED_GLASS;
    }
    
    /**
     * Check if glass structure is only 1 block thick in any direction
     * A structure like 4x4x1 should be breakable, but 4x4x2 should not
     */
    private boolean isGlassSingleThickness(Block block) {
        // For each direction, check if there are glass blocks 2 deep
        for (BlockFace face : ALL_FACES) {
            if (hasGlassDepth(block, face, 2)) {
                plugin.debug("Glass at " + block.getLocation() + " is too thick in direction " + face);
                return false;
            }
        }
        
        plugin.debug("Glass at " + block.getLocation() + " is single thickness - can break");
        return true;
    }
    
    /**
     * Check if there are glass blocks at the specified depth in a direction
     */
    private boolean hasGlassDepth(Block startBlock, BlockFace direction, int depth) {
        Block currentBlock = startBlock;
        
        for (int i = 0; i < depth; i++) {
            currentBlock = currentBlock.getRelative(direction);
            if (!isBreakableGlass(currentBlock.getType())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if glass can be broken at this location
     */
    public boolean canBreakGlass(Block block, Player player) {
        // Check if already broken
        if (brokenGlass.containsKey(block.getLocation())) {
            plugin.debug("Glass at " + block.getLocation() + " already broken");
            return false;
        }
        
        // Check if glass is single thickness
        if (!isGlassSingleThickness(block)) {
            plugin.debug("Glass at " + block.getLocation() + " too thick to break");
            return false;
        }
        
        // Check integrations (WorldGuard, Towny, etc.)
        if (plugin.getIntegrationManager() != null && player != null) {
            boolean canBreak = plugin.getIntegrationManager().canBreakGlass(block.getLocation(), player);
            if (!canBreak) {
                plugin.debug("Integration manager denied glass breaking at " + block.getLocation());
                return false;
            }
        }
        
        plugin.debug("Glass at " + block.getLocation() + " can be broken");
        return true;
    }
    
    /**
     * Break glass at the specified location
     */
    public void breakGlass(Location location) {
        if (location == null || location.getWorld() == null) return;
        location = normalizeLocation(location);
        Block block = location.getBlock();
        Material originalMaterial = block.getType();
        
        if (!isBreakableGlass(originalMaterial)) {
            plugin.debug("Block at " + location + " is not breakable glass: " + originalMaterial);
            return;
        }
        
        // Store original material and block data for exact regeneration
        BlockData originalBlockData = block.getBlockData().clone();
        brokenGlass.put(location, new GlassData(originalMaterial, originalBlockData));
        
        // Break the glass (set to air)
        block.setType(Material.AIR);
        
        // Play break effects
        playBreakEffects(location);
        
        // Schedule regeneration
        scheduleRegeneration(location, originalMaterial, originalBlockData);
        
        plugin.debug("Glass broken at " + location.toString() + " (was " + originalMaterial + ")");
    }
    
    /**
     * Play breaking effects
     */
    private void playBreakEffects(Location location) {
        if (plugin.getConfigManager().isGlassBreakSoundEnabled()) {
            location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK,
                    plugin.getConfigManager().getGlassBreakSoundVolume(),
                    plugin.getConfigManager().getGlassBreakSoundPitch());
        }
        
        if (plugin.getConfigManager().isGlassBreakParticlesEnabled()) {
            Location particleLocation = location.clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                    particleLocation,
                    plugin.getConfigManager().getGlassBreakParticleCount(),
                    0.3, 0.3, 0.3, 0.1,
                    Material.GLASS.createBlockData());
        }
    }
    
    /**
     * Schedule glass regeneration
     */
    private void scheduleRegeneration(Location location, Material originalMaterial, BlockData originalBlockData) {
        int delay = plugin.getConfigManager().getGlassRestoreTime() * 20; // Convert to ticks
        
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                regenerateGlass(location, originalMaterial, originalBlockData);
            }
        }.runTaskLater(plugin, delay).getTaskId();
        
        regenerationTasks.put(location.clone(), taskId);
    }
    
    /**
     * Regenerate glass at exact location with exact block data
     */
    private void regenerateGlass(Location location, Material originalMaterial, BlockData originalBlockData) {
        Block block = location.getBlock();
        
        // Only regenerate if still air
        if (block.getType() == Material.AIR) {
            // Set the exact block data to preserve orientation, properties, etc.
            block.setBlockData(originalBlockData);
            
            // Play restore effects
            playRestoreEffects(location);
            
            plugin.debug("Glass regenerated at " + location.toString() + " (restored to " + originalMaterial + ")");
        }
        
        // Clean up tracking
        brokenGlass.remove(location);
        regenerationTasks.remove(location);
    }
    
    /**
     * Play restoration effects
     */
    private void playRestoreEffects(Location location) {
        if (plugin.getConfigManager().isGlassRestoreSoundEnabled()) {
            location.getWorld().playSound(location, Sound.BLOCK_GLASS_PLACE,
                    plugin.getConfigManager().getGlassRestoreSoundVolume(),
                    plugin.getConfigManager().getGlassRestoreSoundPitch());
        }
        
        if (plugin.getConfigManager().isGlassRestoreParticlesEnabled()) {
            Location particleLocation = location.clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                    particleLocation,
                    plugin.getConfigManager().getGlassRestoreParticleCount(),
                    0.3, 0.3, 0.3, 0.1);
        }
    }
    
    /**
     * Regenerate all broken glass immediately (used on plugin disable)
     */
    public void regenerateAllGlass() {
        plugin.getLogger().info("Regenerating " + brokenGlass.size() + " broken glass blocks...");
        
        // Cancel all pending regeneration tasks
        for (Integer taskId : regenerationTasks.values()) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        regenerationTasks.clear();
        
        // Restore all broken glass with exact block data
        for (Map.Entry<Location, GlassData> entry : brokenGlass.entrySet()) {
            Location location = entry.getKey();
            GlassData glassData = entry.getValue();
            Block block = location.getBlock();
            
            if (block.getType() == Material.AIR) {
                block.setBlockData(glassData.blockData);
            }
        }
        
        brokenGlass.clear();
    }
    
    /**
     * Restore all broken glass immediately (alias for regenerateAllGlass)
     */
    public void restoreAllGlass() {
        regenerateAllGlass();
    }
    
    /**
     * Get the number of currently broken glass blocks
     */
    public int getBrokenGlassCount() {
        return brokenGlass.size();
    }
    
    /**
     * Check if glass is currently broken at location
     */
    public boolean isGlassBroken(Location location) {
        return brokenGlass.containsKey(location);
    }
    /**
     * Clean up all data structures to prevent memory leaks
     */
    public void cleanup() {
        // Cancel all scheduled regeneration tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
        
        // Clear all collections
        brokenGlass.clear();
        
        plugin.debug("GlassManager cleanup completed");
    }

}
