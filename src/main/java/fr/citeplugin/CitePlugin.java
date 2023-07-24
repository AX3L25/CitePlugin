package fr.citeplugin;

import fr.citeplugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CitePlugin extends JavaPlugin implements Listener {
    private Scoreboard scoreboard;
    private Map<UUID, Team> playerTeams;
    private Map<UUID, Integer> playerPoints;

    private FileConfiguration teamsConfig;

    private TeamManager teamManager;
    private SpawnManager spawnManager;
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        // Chargement de la configuration depuis le fichier config.yml
        saveDefaultConfig();

        // Initialisation des variables
        playerTeams = new HashMap<>();
        playerPoints = new HashMap<>();

        // Mise en place du scoreboard
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Chargement de la configuration des équipes
        File teamsFile = new File(getDataFolder(), "teams.yml");
        if (!teamsFile.exists()) {
            saveResource("teams.yml", false);
        }
        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);

        // Initialisation des gestionnaires
        teamManager = new TeamManager(scoreboard, teamsConfig);
        spawnManager = new SpawnManager(this, teamsConfig, getDataFolder());
        tradeManager = new TradeManager(playerTeams, playerPoints, teamsConfig);

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("createTeam").setExecutor(new CreateTeamCommand(teamManager));
        getCommand("setSpawn").setExecutor(new SetSpawnCommand(spawnManager));
        getCommand("joinTeam").setExecutor(new JoinTeamCommand(teamManager, playerTeams));
        getCommand("leaveTeam").setExecutor(new LeaveTeamCommand(playerTeams));
        getCommand("deleteTeam").setExecutor(new DeleteTeamCommand(scoreboard, playerTeams));
        getCommand("setTeamSize").setExecutor(new SetTeamSizeCommand(scoreboard));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Cité désactivé !");
    }

    public <CommandSender> boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "createteam":
                return teamManager.createTeam((org.bukkit.command.CommandSender) sender, args);
            case "setspawn":
                return spawnManager.setSpawn((org.bukkit.command.CommandSender) sender, args);
            case "jointeam":
                return teamManager.joinTeam((org.bukkit.command.CommandSender) sender, args, playerTeams);
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                playerTeams.put(player.getUniqueId(), team);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Villager) {
            Player player = (Player) event.getEntity();
            Villager villager = (Villager) event.getDamager();

            if (villager.getProfession() == Villager.Profession.NITWIT) {
                Team playerTeam = playerTeams.get(player.getUniqueId());
                if (playerTeam != null) {
                    String teamName = playerTeam.getName();
                    if (teamsConfig.contains("teams." + teamName + ".trades")) {
                        int points = playerPoints.getOrDefault(player.getUniqueId(), 0);
                        tradeManager.doTrades(player, teamName, points);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.VILLAGER && event.getPlayer().hasPermission("cite.trade")) {
            Villager villager = (Villager) event.getRightClicked();
            villager.setProfession(Villager.Profession.NITWIT);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Ce villageois est maintenant un PNJ pour les échanges !");
        }
    }

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        if (event.getCommand().equalsIgnoreCase("spawnVillager")) {
            Player player = Bukkit.getPlayer(event.getSender().getName());
            if (player != null) {
                Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                villager.setAI(false);
            }
        }

    }
}

