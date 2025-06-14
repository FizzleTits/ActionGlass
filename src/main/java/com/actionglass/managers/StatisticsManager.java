package com.actionglass.managers;

import com.actionglass.ActionGlass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player statistics for glass breaking
 */
public class StatisticsManager {
    
    private final ActionGlass plugin;
    private final Map<UUID, Integer> playerGlassBreaks = new ConcurrentHashMap<>();
    private final File statsFile;
    private FileConfiguration statsConfig;
    
    public StatisticsManager(ActionGlass plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "statistics.yml");
        loadStatistics();
    }
    
    /**
     * Load statistics from file
     */
    public void loadStatistics() {
        if (!statsFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                statsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create statistics file: " + e.getMessage());
            }
        }
        
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        
        // Load player statistics
        if (statsConfig.contains("players")) {
            for (String uuidString : statsConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    int breaks = statsConfig.getInt("players." + uuidString + ".glass-breaks", 0);
                    playerGlassBreaks.put(uuid, breaks);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in statistics: " + uuidString);
                }
            }
        }
        
        plugin.getLogger().info("Loaded statistics for " + playerGlassBreaks.size() + " players.");
    }
    
    /**
     * Save statistics to file
     */
    public void saveStatistics() {
        try {
            // Clear existing player data
            statsConfig.set("players", null);
            
            // Save player statistics
            for (Map.Entry<UUID, Integer> entry : playerGlassBreaks.entrySet()) {
                String uuidString = entry.getKey().toString();
                statsConfig.set("players." + uuidString + ".glass-breaks", entry.getValue());
            }
            
            // Save last updated timestamp
            statsConfig.set("last-updated", System.currentTimeMillis());
            
            statsConfig.save(statsFile);
            plugin.debug("Statistics saved for " + playerGlassBreaks.size() + " players.");
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save statistics: " + e.getMessage());
        }
    }
    
    /**
     * Add a glass break for a player
     */
    public void addGlassBreak(Player player) {
        if (player == null) return;
        if (!plugin.getConfigManager().isStatisticsEnabled()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        int currentBreaks = playerGlassBreaks.getOrDefault(uuid, 0);
        playerGlassBreaks.put(uuid, currentBreaks + 1);
        
        plugin.debug("Added glass break for " + player.getName() + " (total: " + (currentBreaks + 1) + ")");
    }
    
    /**
     * Get glass breaks for a player
     */
    public int getGlassBreaks(Player player) {
        if (player == null) return 0;
        return playerGlassBreaks.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Get glass breaks by UUID
     */
    public int getGlassBreaks(UUID uuid) {
        return playerGlassBreaks.getOrDefault(uuid, 0);
    }
    
    /**
     * Get formatted statistics for a player
     */
    public String getStats(Player player) {
        if (player == null) return "Player not found";
        int glassBreaks = getGlassBreaks(player);
        int totalBreaks = getTotalGlassBreaks();
        int trackedPlayers = getTrackedPlayerCount();
        
        StringBuilder stats = new StringBuilder();
        stats.append("§6=== ActionGlass Statistics ===\n");
        stats.append("§7Player: §f").append(player.getName()).append("\n");
        stats.append("§7Glass Broken: §a").append(glassBreaks).append("\n");
        stats.append("§7Total Server Breaks: §e").append(totalBreaks).append("\n");
        stats.append("§7Tracked Players: §b").append(trackedPlayers).append("\n");
        
        // Calculate percentage if there are total breaks
        if (totalBreaks > 0) {
            double percentage = (double) glassBreaks / totalBreaks * 100;
            stats.append("§7Your Percentage: §d").append(String.format("%.1f%%", percentage));
        }
        
        return stats.toString();
    }
    
    /**
     * Get total glass breaks across all players
     */
    public int getTotalGlassBreaks() {
        return playerGlassBreaks.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Get top glass breakers
     */
    public Map<UUID, Integer> getTopGlassBreakers(int limit) {
        return playerGlassBreaks.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }
    
    /**
     * Reset statistics for a player
     */
    public void resetPlayerStats(UUID uuid) {
        playerGlassBreaks.remove(uuid);
        plugin.debug("Reset statistics for player: " + uuid);
    }
    
    /**
     * Reset all statistics
     */
    public void resetAllStats() {
        playerGlassBreaks.clear();
        plugin.getLogger().info("All statistics have been reset.");
    }
    
    /**
     * Get the number of tracked players
     */
    public int getTrackedPlayerCount() {
        return playerGlassBreaks.size();
    }
}
