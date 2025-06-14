package com.actionglass.listeners;

import com.actionglass.ActionGlass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Handles player movement and glass breaking mechanics
 */
public class MovementListener implements Listener {
    
    private final ActionGlass plugin;
    
    public MovementListener(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfigManager().isGlassBreakingEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to == null) {
            return;
        }
        
        // Calculate movement speed and velocity
        Vector velocity = player.getVelocity();
        double horizontalSpeed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
        double verticalSpeed = Math.abs(velocity.getY());
        double totalSpeed = velocity.length();
        
        plugin.debug("Player " + player.getName() + " - Total: " + String.format("%.2f", totalSpeed) + 
                    ", Horizontal: " + String.format("%.2f", horizontalSpeed) + 
                    ", Vertical: " + String.format("%.2f", verticalSpeed));
        
        // Check different breaking conditions
        checkElytraBreaking(player, to, totalSpeed);
        checkFallBreaking(player, to, verticalSpeed);
        checkSprintBreaking(player, to, horizontalSpeed);
    }
    
    /**
     * Check for elytra-based glass breaking
     */
    private void checkElytraBreaking(Player player, Location location, double speed) {
        if (!plugin.getConfigManager().isElytraBreakingEnabled()) {
            return;
        }
        
        if (!player.isGliding()) {
            return;
        }
        
        plugin.debug("Player " + player.getName() + " gliding at speed " + speed);
        
        if (speed >= plugin.getConfigManager().getElytraMinSpeed()) {
            plugin.debug("Elytra speed sufficient for glass breaking");
            int radius = (int) Math.round(plugin.getConfigManager().getElytraBreakRadius());
                        breakGlassInRadius(player, location, radius, "elytra");
        }
    }
    
    /**
     * Check for fall-based glass breaking
     */
    private void checkFallBreaking(Player player, Location location, double verticalSpeed) {
        if (!plugin.getConfigManager().isFallingBreakingEnabled()) {
            return;
        }
        
        // Check if player is falling (negative Y velocity)
        if (player.getVelocity().getY() >= -0.3) {
            return;
        }
        
        // Check fall distance
        if (player.getFallDistance() < plugin.getConfigManager().getFallBreakHeight()) {
            return;
        }
        
        plugin.debug("Player " + player.getName() + " falling at speed " + verticalSpeed + 
                    " from height " + player.getFallDistance());
        
        if (verticalSpeed >= plugin.getConfigManager().getFallingMinSpeed()) {
            plugin.debug("Fall speed sufficient for glass breaking");
            int radius = (int) Math.round(plugin.getConfigManager().getFallingBreakRadius());
            breakGlassInRadius(player, location, radius, "falling");
        }
    }
    
    /**
     * Check for sprint-based glass breaking
     * Requires speed potion effect or beacon speed boost
     */
    private void checkSprintBreaking(Player player, Location location, double horizontalSpeed) {
        if (!plugin.getConfigManager().isSprintingBreakingEnabled()) {
            return;
        }
        
        if (!player.isSprinting()) {
            return;
        }
        
        // Check if player has speed effect (potion or beacon)
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            plugin.debug("Player " + player.getName() + " sprinting but no speed effect");
            return;
        }
        
        plugin.debug("Player " + player.getName() + " sprinting with speed effect at speed " + horizontalSpeed);
        
        if (horizontalSpeed >= plugin.getConfigManager().getSprintingMinSpeed()) {
            plugin.debug("Sprint speed sufficient for glass breaking");
            int radius = (int) Math.round(plugin.getConfigManager().getSprintingBreakRadius());
            breakGlassInRadius(player, location, radius, "sprinting");
        }
    }
    
    /**
     * Break glass blocks in a radius around the player
     */
    private void breakGlassInRadius(Player player, Location center, int radius, String method) {
        plugin.debug("Breaking glass in radius " + radius + " around " + center + " (method: " + method + ")");
        
        int glassCount = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = center.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();
                    
                    // Check if it's glass and can be broken
                    if (plugin.getGlassManager().isBreakableGlass(block.getType()) &&
                        plugin.getGlassManager().canBreakGlass(block, player)) {
                        
                        // Break the glass
                        plugin.getGlassManager().breakGlass(blockLocation);
                        glassCount++;
                        
                        // Add to statistics
                        if (plugin.getStatisticsManager() != null) {
                            plugin.getStatisticsManager().addGlassBreak(player);
                        }
                        
                        plugin.debug("Glass broken at " + blockLocation.toString() + " by " + player.getName() + " (" + method + ")");
                    }
                }
            }
        }
        
        if (glassCount > 0) {
            plugin.debug("Total glass blocks broken: " + glassCount + " by " + method);
        }
    }
}

