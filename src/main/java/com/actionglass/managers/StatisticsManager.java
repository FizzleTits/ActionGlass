package com.actionglass.managers;

import com.actionglass.ActionGlass;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsManager {
    private final ActionGlass plugin;
    private final Map<UUID, PlayerStats> playerStats;
    
    public StatisticsManager(ActionGlass plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
    }
    
    public void addGlassBreak(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = playerStats.computeIfAbsent(uuid, k -> new PlayerStats());
        stats.glassBreaks++;
        stats.totalBlocks++;
    }
    
    public PlayerStats getStats(Player player) {
        return playerStats.getOrDefault(player.getUniqueId(), new PlayerStats());
    }
    
    public void resetStats(Player player) {
        playerStats.remove(player.getUniqueId());
    }
    
    public void saveStatistics() {
        // Save statistics to file or database
        plugin.getLogger().info("Statistics saved for " + playerStats.size() + " players");
    }
    
    public static class PlayerStats {
        public int glassBreaks = 0;
        public int totalBlocks = 0;
        public long firstBreak = System.currentTimeMillis();
        public long lastBreak = System.currentTimeMillis();
        
        public PlayerStats() {
            this.firstBreak = System.currentTimeMillis();
            this.lastBreak = System.currentTimeMillis();
        }
    }
}
