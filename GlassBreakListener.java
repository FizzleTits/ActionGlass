package com.actionglass;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles all glass breaking events from projectiles and player movement
 * Implements realistic physics-based glass breaking mechanics
 */
public class GlassBreakListener implements Listener {
    
    private final ActionGlass plugin;
    // Cooldown tracking to prevent spam breaking
    private final Map<UUID, Long> lastGlassBreak = new HashMap<>();
    // Track player locations for movement analysis
    private final Map<UUID, Location> lastPlayerLocation = new HashMap<>();
    
    public GlassBreakListener(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle projectiles hitting glass blocks
     * Different projectiles have different breaking patterns
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // Check if arrow breaking is enabled
        if (!plugin.isArrowBreakingEnabled()) return;
        
        Projectile projectile = event.getEntity();
        Block hitBlock = event.getHitBlock();
        
        // Only process if we hit a glass block
        if (hitBlock != null && plugin.getGlassManager().isGlass(hitBlock.getType())) {
            // Different break radius based on projectile type
            int breakRadius = getProjectileBreakRadius(projectile);
            if (breakRadius > 0) {
                plugin.getGlassManager().breakGlassArea(hitBlock.getLocation(), breakRadius);
            }
        }
    }
    
    /**
     * Determine break radius based on projectile type
     * More powerful projectiles break more glass
     */
    private int getProjectileBreakRadius(Projectile projectile) {
        if (projectile instanceof Arrow) {
            return plugin.getProjectileBreakRadius("arrow");
        } else if (projectile instanceof Trident) {
            return plugin.getProjectileBreakRadius("trident");
        } else if (projectile instanceof WindCharge) {
            return plugin.getProjectileBreakRadius("wind-charge");
        }
        return 0;
    }
    
    /**
     * Handle player movement for breaking glass through movement
     * This is the main method for elytra, falling, and running through glass
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        // Basic validation
        if (to == null || player.getGameMode() == GameMode.SPECTATOR) return;
        
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Apply cooldown to prevent spam breaking
        Long lastBreak = lastGlassBreak.get(playerId);
        if (lastBreak != null && currentTime - lastBreak < plugin.getGlassBreakCooldown()) {
            return;
        }
        
        // Calculate movement vector and speeds
        Vector velocity = to.toVector().subtract(from.toVector());
        double totalSpeed = velocity.length();
        double horizontalSpeed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
        double verticalSpeed = Math.abs(velocity.getY());
        
        // Don't process if moving too slowly
        if (totalSpeed < plugin.getMinimumSpeed()) return;
        
        // Determine movement type
        boolean isGliding = player.isGliding();
        boolean isFalling = velocity.getY() < -plugin.getMinimumSpeed() && !player.isOnGround();
        boolean isRunning = horizontalSpeed > plugin.getMinimumSpeed() && !isGliding && !isFalling;
        boolean isSprinting = player.isSprinting();
        
        // Check if the relevant feature is enabled
        if ((isGliding && !plugin.isElytraBreakingEnabled()) ||
            (isFalling && !plugin.isFallingBreakingEnabled()) ||
            (isRunning && !plugin.isRunningBreakingEnabled())) {
            return;
        }
        
        // Enhanced glass collision detection
        if (checkPlayerGlassCollision(player, from, to, isGliding, isFalling, isRunning, isSprinting, totalSpeed)) {
            lastGlassBreak.put(playerId, currentTime);
        }
        
        // Store location for next movement check
        lastPlayerLocation.put(playerId, to.clone());
    }
    
    /**
     * Comprehensive glass collision detection for player movement
     * Handles different movement types with appropriate breaking patterns
     */
    private boolean checkPlayerGlassCollision(Player player, Location from, Location to,
                                           boolean isGliding, boolean isFalling,
                                           boolean isRunning, boolean isSprinting, double speed) {
        
        boolean brokeGlass = false;
        
        // Get movement direction for better collision detection
        Vector direction = to.toVector().subtract(from.toVector());
        if (direction.lengthSquared() == 0) return false;
        
        direction.normalize();
        
        // Check player's bounding box (feet and head level)
        brokeGlass |= checkPlayerPositionForGlass(to, isGliding, isFalling, isRunning, isSprinting);
        
        // For high-speed movement, check the path between positions
        double distance = from.distance(to);
        if (distance > 0.3) {
            brokeGlass |= checkMovementPathForGlass(from, to, direction, distance, isGliding, isFalling, isRunning, isSprinting);
        }
        
        // Special handling for running through vertical glass walls
        if (isRunning && !brokeGlass) {
            brokeGlass |= checkForVerticalGlassWalls(player, to, direction, isSprinting);
        }
        
        // Special handling for falling through horizontal glass floors/ceilings
        if (isFalling && !brokeGlass) {
            brokeGlass |= checkForHorizontalGlassStructures(player, to, direction);
        }
        
        return brokeGlass;
    }
    
    /**
     * Check the player's current position for glass blocks
     */
    private boolean checkPlayerPositionForGlass(Location playerLoc, boolean isGliding, boolean isFalling, boolean isRunning, boolean isSprinting) {
        boolean brokeGlass = false;
        
        // Check feet level
        Block feetBlock = playerLoc.getBlock();
        if (plugin.getGlassManager().isGlass(feetBlock.getType())) {
            int radius = getMovementBreakRadius(isGliding, isFalling, isRunning, isSprinting);
            plugin.getGlassManager().breakGlassArea(feetBlock.getLocation(), radius);
            brokeGlass = true;
        }
        
        // Check head level (1.8 blocks up for player height)
        Block headBlock = playerLoc.clone().add(0, 1, 0).getBlock();
        if (plugin.getGlassManager().isGlass(headBlock.getType())) {
            int radius = getMovementBreakRadius(isGliding, isFalling, isRunning, isSprinting);
            plugin.getGlassManager().breakGlassArea(headBlock.getLocation(), radius);
            brokeGlass = true;
        }
        
        return brokeGlass;
    }
    
    /**
     * Check the movement path for glass blocks (for high-speed movement)
     */
    private boolean checkMovementPathForGlass(Location from, Location to, Vector direction, double distance,
                                           boolean isGliding, boolean isFalling, boolean isRunning, boolean isSprinting) {
        
        // Limit steps for performance while ensuring good coverage
        int steps = Math.min(10, Math.max(3, (int)(distance / 0.2)));
        
        for (int i = 1; i <= steps; i++) {
            double stepDistance = (distance / steps) * i;
            Location checkLoc = from.clone().add(direction.clone().multiply(stepDistance));
            
            // Check both feet and head level along the path
            Block pathBlock = checkLoc.getBlock();
            Block pathBlockAbove = checkLoc.clone().add(0, 1, 0).getBlock();
            
            if (plugin.getGlassManager().isGlass(pathBlock.getType()) ||
                plugin.getGlassManager().isGlass(pathBlockAbove.getType())) {
                
                int radius = getMovementBreakRadius(isGliding, isFalling, isRunning, isSprinting);
                Location breakLoc = plugin.getGlassManager().isGlass(pathBlock.getType()) ?
                                   pathBlock.getLocation() : pathBlockAbove.getLocation();
                
                plugin.getGlassManager().breakGlassArea(breakLoc, radius);
                return true; // Only break the first glass encountered
            }
        }
        
        return false;
    }
    
    /**
     * Special detection for running through vertical glass walls
     * Improved to handle 5x5x1 glass wall structures
     */
    private boolean checkForVerticalGlassWalls(Player player, Location playerLoc, Vector direction, boolean isSprinting) {
        // Expand search area for better wall detection
        int searchRadius = isSprinting ? 2 : 1;
        
        // Check in the direction of movement and slightly to the sides
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = 0; y <= 2; y++) { // Check feet, middle, and head level
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip center
                    
                    Location checkLoc = playerLoc.clone().add(x, y, z);
                    Block block = checkLoc.getBlock();
                    
                    if (plugin.getGlassManager().isGlass(block.getType())) {
                        // Check if this glass block is in the direction of movement
                        Vector toGlass = checkLoc.toVector().subtract(playerLoc.toVector());
                        if (toGlass.dot(direction) > 0) { // Glass is in front of player
                            int radius = isSprinting ? 3 : 2;
                            plugin.getGlassManager().breakGlassArea(block.getLocation(), radius);
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Special detection for falling through horizontal glass structures
     * Improved to handle 5x5x1 glass floor/ceiling structures
     */
    private boolean checkForHorizontalGlassStructures(Player player, Location playerLoc, Vector direction) {
        // For falling, check a wider horizontal area
        int searchRadius = 2;
        
        // Check both above and below the player
        for (int y = -2; y <= 2; y++) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    Location checkLoc = playerLoc.clone().add(x, y, z);
                    Block block = checkLoc.getBlock();
                    
                    if (plugin.getGlassManager().isGlass(block.getType())) {
                        // For falling, we want to break glass that's in our path
                        Vector toGlass = checkLoc.toVector().subtract(playerLoc.toVector());
                        if (Math.abs(toGlass.getY()) <= 2) { // Glass is at reasonable height difference
                            plugin.getGlassManager().breakGlassArea(block.getLocation(), 3);
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Determine break radius based on movement type
     * Different movement types create different breaking patterns
     */
    private int getMovementBreakRadius(boolean isGliding, boolean isFalling, boolean isRunning, boolean isSprinting) {
        if (isGliding) {
            return plugin.getMovementBreakRadius("elytra");
        } else if (isFalling) {
            return plugin.getMovementBreakRadius("falling");
        } else if (isRunning) {
            return isSprinting ? plugin.getMovementBreakRadius("sprinting") : plugin.getMovementBreakRadius("running");
        }
        return 1; // Default small break radius
    }
}
