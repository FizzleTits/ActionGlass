package com.actionglass.listeners;

import com.actionglass.ActionGlass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Handles glass breaking events from various sources
 */
public class GlassBreakListener implements Listener {
    
    private final ActionGlass plugin;
    
    public GlassBreakListener(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle projectile hits on glass blocks
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();
        if (hitBlock == null) return;
        
        if (!plugin.getGlassManager().isBreakableGlass(hitBlock.getType())) {
            return;
        }
        
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        
        // Check if shot by a player
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            
            // Check permissions
            if (!player.hasPermission("actionglass.break")) {
                return;
            }
            
            // Check if player can break glass at this location
            if (!plugin.getGlassManager().canBreakGlass(hitBlock, player)) {
                return;
            }
            
            // Break the glass
            plugin.getGlassManager().breakGlass(hitBlock.getLocation());
            
            // Update statistics
            plugin.getStatisticsManager().addGlassBreak(player);
        }
    }
    
    /**
     * Handle player interactions with glass (optional feature)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        
        if (!plugin.getGlassManager().isBreakableGlass(clickedBlock.getType())) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Check if punch-to-break is enabled in config
        if (!plugin.getConfigManager().isPunchToBreakEnabled()) {
            return;
        }
        
        // Check permissions
        if (!player.hasPermission("actionglass.break.punch")) {
            return;
        }
        
        // Check if player can break glass at this location
        if (!plugin.getGlassManager().canBreakGlass(clickedBlock, player)) {
            return;
        }
        
        // Cancel the event to prevent normal block breaking
        event.setCancelled(true);
        
        // Break the glass
        plugin.getGlassManager().breakGlass(clickedBlock.getLocation());
        
        // Update statistics
        plugin.getStatisticsManager().addGlassBreak(player);
    }
    
    /**
     * Handle entity damage events that might break glass
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // This could be used for explosion damage to glass, etc.
        // Implementation depends on specific requirements
    }
}
