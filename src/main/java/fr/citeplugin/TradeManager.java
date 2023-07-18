package fr.citeplugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class TradeManager {
    private Map<UUID, Team> playerTeams;
    private Map<UUID, Integer> playerPoints;
    private FileConfiguration teamsConfig;

    public <FileConfiguration> TradeManager(Map<UUID, Team> playerTeams, Map<UUID, Integer> playerPoints, FileConfiguration teamsConfig) {
        this.playerTeams = playerTeams;
        this.playerPoints = playerPoints;
        this.teamsConfig = (org.bukkit.configuration.file.FileConfiguration) teamsConfig;
    }

    public void doTrades(Player player, String teamName, int points) {
        if (teamsConfig.contains("teams." + teamName + ".trades")) {
            for (String trade : teamsConfig.getConfigurationSection("teams." + teamName + ".trades").getKeys(false)) {
                ItemStack tradeItem = teamsConfig.getItemStack("teams." + teamName + ".trades." + trade + ".item");
                int tradeCost = teamsConfig.getInt("teams." + teamName + ".trades." + trade + ".cost");

                if (player.getInventory().containsAtLeast(tradeItem, tradeCost)) {
                    player.getInventory().removeItem(new ItemStack(tradeItem.getType(), tradeCost));
                    playerPoints.put(player.getUniqueId(), points + 1);

                    player.sendMessage(ChatColor.GREEN + "Échange effectué ! Vous avez gagné 1 point pour votre équipe !");
                }
            }
        }
    }
}
