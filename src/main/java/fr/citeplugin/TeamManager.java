package fr.citeplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class TeamManager {

    private final Scoreboard scoreboard;
    private final FileConfiguration teamsConfig;

    public TeamManager(TeamManager teamManager, Scoreboard scoreboard, FileConfiguration teamsConfig) {
        this.scoreboard = scoreboard;
        this.teamsConfig = teamsConfig;
    }

    public boolean createTeam(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /createTeam <nom> <couleur> <nombre_max_joueurs>");
            return true;
        }

        String teamName = args[0];
        ChatColor teamColor = ChatColor.valueOf(args[1].toUpperCase());
        int maxPlayers = Integer.parseInt(args[2]);

        if (scoreboard.getTeam(teamName) != null) {
            sender.sendMessage(ChatColor.RED + "Une équipe avec ce nom existe déjà !");
            return true;
        }

        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(teamColor);
        team.setDisplayName(teamColor + teamName);

        // Enregistrement des propriétés de l'équipe dans le fichier de configuration
        teamsConfig.set("teams." + teamName + ".color", teamColor.name());
        teamsConfig.set("teams." + teamName + ".maxPlayers", maxPlayers);
        teamsConfig.set("teams." + teamName + ".displayName", teamColor + teamName);

        // Sauvegarde de la configuration des équipes
        try {
            JavaPlugin plugin = null;
            teamsConfig.save(new File(plugin.getDataFolder(), "teams.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatColor.GREEN + "L'équipe " + team.getDisplayName() + ChatColor.GREEN + " a été créée !");
        return true;
    }

    public boolean joinTeam(CommandSender sender, String[] args, Map<UUID, Team> playerTeams) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande peut uniquement être exécutée par un joueur !");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /joinTeam <nom de l'équipe>");
            return true;
        }

        String teamName = args[0];
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Cette équipe n'existe pas !");
            return true;
        }

        if (playerTeams.containsKey(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Vous faites déjà partie d'une équipe !");
            return true;
        }

        int maxPlayers = teamsConfig.getInt("teams." + teamName + ".maxPlayers");
        if (team.getSize() >= maxPlayers) {
            sender.sendMessage(ChatColor.RED + "L'équipe est déjà complète !");
            return true;
        }

        team.addEntry(player.getName());

        playerTeams.put(player.getUniqueId(), team);

        sender.sendMessage(ChatColor.GREEN + "Vous avez rejoint l'équipe " + team.getDisplayName() + ChatColor.GREEN + " !");
        return true;
    }
}
