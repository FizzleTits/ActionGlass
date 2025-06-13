package com.actionglass;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages glass breaking and regeneration mechanics
 * Handles detection of thin glass structures (1 block thick) and realistic breaking patterns
 */
public class GlassManager {
    
    private final ActionGlass plugin;
    // Thread-safe storage for broken glass blocks
    private final Map<Location, GlassBlock> brokenGlass;
    // All glass materials supported by the plugin
    private final Set<Material> glassTypes;
    
    public GlassManager(ActionGlass plugin) {
        this.plugin = plugin;
        this.brokenGlass = new ConcurrentHashMap<>();
        this.glassTypes = EnumSet.of(
            // Standard glass blocks
            Material.GLASS,
            Material.WHITE_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS,
            Material.LIME_STAINED_GLASS,
            Material.PINK_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS,
            Material.BROWN_STAINED_GLASS,
            Material.GREEN_STAINED_GLASS,
            Material.RED_STAINED_GLASS,
            Material.BLACK_STAINED_GLASS,
            // Glass panes
            Material.GLASS_PANE,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE
        );
    }
    
    /**
     * Check if a material is glass
     */
    public boolean isGlass(Material material) {
        return glassTypes.contains(material);
    }
    
    /**
     * Break glass in an area around the impact point
     * Uses intelligent breaking patterns based on glass structure
     */
    public void breakGlassArea(Location center, int radius) {
        if (radius <= 0) return;
        
        World world = center.getWorld();
        if (world == null) return;
        
        List<Block> glassBlocks = new ArrayList<>();
        
        // Find all glass blocks in the break radius
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(loc);
                    
                    if (isGlass(block.getType())) {
                        // Calculate distance from center for realistic breaking pattern
                        double distance = center.distance(loc);
                        if (distance <= radius) {
                            glassBlocks.add(block);
                        }
                    }
                }
            }
        }
        
        // Break the glass blocks with realistic patterns
        if (!glassBlocks.isEmpty()) {
            breakGlassBlocks(glassBlocks, center);
        }
    }
    
    /**
     * Break a collection of glass blocks with effects and regeneration tracking
     */
    private void breakGlassBlocks(List<Block> blocks, Location impactPoint) {
        for (Block block : blocks) {
            breakSingleGlass(block, impactPoint);
        }
    }
    
    /**
     * Break a single glass block with full effects and regeneration
     */
    public void breakSingleGlass(Block block, Location impactPoint) {
        if (!isGlass(block.getType())) return;
        
        Location loc = block.getLocation();
        
        // Don't break the same glass twice
        if (brokenGlass.containsKey(loc)) return;
        
        // Store original block data for regeneration
        Material originalMaterial = block.getType();
        BlockData originalData = block.getBlockData().clone();
        
        // Create glass block record
        GlassBlock glassBlock = new GlassBlock(loc, originalMaterial, originalData, System.currentTimeMillis());
        brokenGlass.put(loc, glassBlock);
        
        // Break the block
        block.setType(Material.AIR);
        
        // Play breaking effects
        playBreakingEffects(loc, originalMaterial);
        
        // Schedule regeneration if enabled
        if (plugin.isRegenerationEnabled()) {
            scheduleRegeneration(glassBlock);
        }
    }
    
    /**
     * Play visual and audio effects for glass breaking
     */
    private void playBreakingEffects(Location location, Material originalMaterial) {
        World world = location.getWorld();
        if (world == null) return;
        
        // Play glass breaking sound
        world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
        
        // Spawn glass breaking particles
        world.spawnParticle(Particle.BLOCK_CRACK, 
                          location.add(0.5, 0.5, 0.5), 
                          20, 
                          0.3, 0.3, 0.3, 
                          0.1, 
                          originalMaterial.createBlockData());
    }
    
    /**
     * Schedule glass regeneration after the configured delay
     */
    private void scheduleRegeneration(GlassBlock glassBlock) {
        new BukkitRunnable() {
            @Override
            public void run() {
                regenerateGlass(glassBlock);
            }
        }.runTaskLater(plugin, plugin.getRegenerationDelay());
    }
    
    /**
     * Regenerate a broken glass block
     */
    private void regenerateGlass(GlassBlock glassBlock) {
        Location loc = glassBlock.getLocation();
        Block block = loc.getBlock();
        
        // Only regenerate if the block is still air
        if (block.getType() == Material.AIR) {
            block.setType(glassBlock.getOriginalMaterial());
            block.setBlockData(glassBlock.getOriginalData());
            
            // Play regeneration effects
            playRegenerationEffects(loc);
        }
        
        // Remove from broken glass tracking
        brokenGlass.remove(loc);
    }
    
    /**
     * Play effects for glass regeneration
     */
    private void playRegenerationEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // Play a subtle regeneration sound
        world.playSound(location, Sound.BLOCK_GLASS_PLACE, 0.5f, 1.2f);
        
        // Spawn some sparkle particles
        world.spawnParticle(Particle.END_ROD, 
                          location.add(0.5, 0.5, 0.5), 
                          5, 
                          0.2, 0.2, 0.2, 
                          0.02);
    }
    
    /**
     * Get all currently broken glass blocks
     */
    public Map<Location, GlassBlock> getBrokenGlass() {
        return new HashMap<>(brokenGlass);
    }
    
    /**
     * Clear all broken glass (useful for plugin reload)
     */
    public void clearBrokenGlass() {
        brokenGlass.clear();
    }
    
    /**
     * Force regenerate all broken glass immediately
     */
    public void regenerateAllGlass() {
        List<GlassBlock> toRegenerate = new ArrayList<>(brokenGlass.values());
        for (GlassBlock glassBlock : toRegenerate) {
            regenerateGlass(glassBlock);
        }
    }
    
    /**
     * Check if a location has broken glass
     */
    public boolean isBrokenGlass(Location location) {
        return brokenGlass.containsKey(location);
    }
    
    /**
     * Get the number of currently broken glass blocks
     */
    public int getBrokenGlassCount() {
        return brokenGlass.size();
    }
    
    /**
     * Advanced glass structure detection for realistic breaking
     * Determines if glass is part of a thin structure (like a window)
     */
    public boolean isThinGlassStructure(Block glassBlock) {
        if (!isGlass(glassBlock.getType())) return false;
        
        Location loc = glassBlock.getLocation();
        
        // Check if glass is only 1 block thick in any direction
        boolean thinX = !isGlass(loc.clone().add(1, 0, 0).getBlock().getType()) || 
                       !isGlass(loc.clone().add(-1, 0, 0).getBlock().getType());
        boolean thinZ = !isGlass(loc.clone().add(0, 0, 1).getBlock().getType()) || 
                       !isGlass(loc.clone().add(0, 0, -1).getBlock().getType());
        
        return thinX || thinZ;
    }
    
    /**
     * Detect glass wall orientation for better breaking patterns
     */
    public GlassStructureType detectGlassStructure(Block glassBlock) {
        if (!isGlass(glassBlock.getType())) return GlassStructureType.UNKNOWN;
        
        Location loc = glassBlock.getLocation();
        
        // Count glass blocks in each direction
        int glassNorth = countGlassInDirection(loc, 0, 0, -1, 3);
        int glassSouth = countGlassInDirection(loc, 0, 0, 1, 3);
        int glassEast = countGlassInDirection(loc, 1, 0, 0, 3);
        int glassWest = countGlassInDirection(loc, -1, 0, 0, 3);
        int glassUp = countGlassInDirection(loc, 0, 1, 0, 3);
        int glassDown = countGlassInDirection(loc, 0, -1, 0, 3);
        
        // Determine structure type based on glass distribution
        if ((glassNorth + glassSouth) >= 2 && (glassEast + glassWest) >= 2) {
            return GlassStructureType.FLOOR_CEILING;
        } else if ((glassNorth + glassSouth) >= 2 || (glassEast + glassWest) >= 2) {
            return GlassStructureType.WALL;
        } else if ((glassUp + glassDown) >= 2) {
            return GlassStructureType.PILLAR;
        }
        
        return GlassStructureType.ISOLATED;
    }
    
    /**
     * Count glass blocks in a specific direction
     */
    private int countGlassInDirection(Location start, int dx, int dy, int dz, int maxDistance) {
        int count = 0;
        for (int i = 1; i <= maxDistance; i++) {
            Location checkLoc = start.clone().add(dx * i, dy * i, dz * i);
            if (isGlass(checkLoc.getBlock().getType())) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
    
    /**
     * Enum for different glass structure types
     */
    public enum GlassStructureType {
        WALL,           // Vertical glass wall
        FLOOR_CEILING,  // Horizontal glass floor or ceiling
        PILLAR,         // Vertical glass pillar
        ISOLATED,       // Single glass block
        UNKNOWN         // Could not determine
    }
    
    /**
     * Inner class to represent a broken glass block
     */
    public static class GlassBlock {
        private final Location location;
        private final Material originalMaterial;
        private final BlockData originalData;
        private final long breakTime;
        
        public GlassBlock(Location location, Material originalMaterial, BlockData originalData, long breakTime) {
            this.location = location;
            this.originalMaterial = originalMaterial;
            this.originalData = originalData;
            this.breakTime = breakTime;
        }
        
        public Location getLocation() { return location; }
        public Material getOriginalMaterial() { return originalMaterial; }
        public BlockData getOriginalData() { return originalData; }
        public long getBreakTime() { return breakTime; }
    }
}
