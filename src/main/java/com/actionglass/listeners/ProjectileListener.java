package com.actionglass.listeners;

import com.actionglass.ActionGlass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles projectile impacts with glass
 */
public class ProjectileListener implements Listener {
    
    private final ActionGlass plugin;
    
    public ProjectileListener(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!plugin.getConfigManager().isGlassBreakingEnabled()) {
            return;
        }
        
        Projectile projectile = event.getEntity();
        Block hitBlock = event.getHitBlock();
        
        // Only handle arrows, tridents, and wind charges
        if (!(projectile instanceof Arrow) && 
            !(projectile instanceof Trident) && 
            !(projectile instanceof WindCharge)) {
            return;
        }
        
        if (hitBlock == null) {
            return;
        }
        
        // Check if hit block is glass
        if (!plugin.getGlassManager().isBreakableGlass(hitBlock.getType())) {
            return;
        }
        
        // Check if glass can be broken (single thickness, permissions, etc.)
        if (!plugin.getGlassManager().canBreakGlass(hitBlock, null)) {
            return;
        }
        
        plugin.debug("Projectile " + projectile.getType() + " hit glass at " + hitBlock.getLocation());
        
        // Find all connected glass blocks (window structure)
        Set<Block> glassStructure = findConnectedGlass(hitBlock);
        
        // Break all glass in the structure
        for (Block glassBlock : glassStructure) {
            plugin.getGlassManager().breakGlass(glassBlock.getLocation());
        }
        
        // Make projectile fall after a short delay
        new BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isValid() && !projectile.isDead()) {
                    // Remove the projectile from being stuck and let it fall
                    if (projectile.isValid() && !projectile.isDead()) {
            org.bukkit.util.Vector currentVel = projectile.getVelocity();
            org.bukkit.util.Vector randomVel = new org.bukkit.util.Vector(
                (Math.random() - 0.5) * 0.2,
                -0.1, // Small downward velocity
                (Math.random() - 0.5) * 0.2
            );
            projectile.setVelocity(currentVel.add(randomVel));
        }
                    projectile.setGravity(true);
                }
            }
        }.runTaskLater(plugin, 5L); // 5 ticks delay
    }
    
    /**
     * Find all glass blocks connected to the hit block (flood fill algorithm)
     */
    private Set<Block> findConnectedGlass(Block startBlock) {
        Set<Block> visited = new HashSet<>();
        List<Block> toCheck = new ArrayList<>();
        toCheck.add(startBlock);
        
        Material glassType = startBlock.getType();
        
        while (!toCheck.isEmpty()) {
            Block current = toCheck.remove(0);
            
            if (visited.contains(current)) {
                continue;
            }
            
            if (!plugin.getGlassManager().isBreakableGlass(current.getType())) {
                continue;
            }
            
            // Only break same type of glass (don't mix different glass types)
            if (current.getType() != glassType) {
                continue;
            }
            
            // Check if this glass block can be broken
            if (!plugin.getGlassManager().canBreakGlass(current, null)) {
                continue;
            }
            
            visited.add(current);
            
            // Add adjacent blocks to check (6 directions)
            toCheck.add(current.getRelative(0, 1, 0));   // Up
            toCheck.add(current.getRelative(0, -1, 0));  // Down
            toCheck.add(current.getRelative(1, 0, 0));   // East
            toCheck.add(current.getRelative(-1, 0, 0));  // West
            toCheck.add(current.getRelative(0, 0, 1));   // South
            toCheck.add(current.getRelative(0, 0, -1));  // North
            
            // Limit the size to prevent breaking massive structures
            if (visited.size() > 64) { // Max 64 blocks per window
                break;
            }
        }
        
        plugin.debug("Found connected glass structure with " + visited.size() + " blocks");
        return visited;
    }
}
