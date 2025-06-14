package com.actionglass.listeners;

import com.actionglass.ActionGlass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Handles glass breaking events
 */
public class GlassBreakListener implements Listener {
    
    private final ActionGlass plugin;
    
    public GlassBreakListener(ActionGlass plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigManager().isGlassBreakingEnabled()) {
            return;
        }
        
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        // Check if it's glass
        if (!plugin.getGlassManager().isBreakableGlass(block.getType())) {
            return;
        }
        
        // Let vanilla Minecraft handle punch breaking - no special mechanics
        plugin.debug("Player " + player.getName() + " broke glass block normally at " + block.getLocation());
        
        // Add to statistics if enabled
        if (plugin.getStatisticsManager() != null) {
            plugin.getStatisticsManager().addGlassBreak(player);
        }
    }
}

