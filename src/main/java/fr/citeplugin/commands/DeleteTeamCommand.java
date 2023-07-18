package fr.citeplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class DeleteTeamCommand implements CommandExecutor {
    private Scoreboard scoreboard;
    private Map<UUID, Team> playerTeams;

    public DeleteTeamCommand(Scoreboard scoreboard, Map<UUID, Team> playerTeams) {
        this.scoreboard = scoreboard;
        this.playerTeams = playerTeams;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /deleteTeam <nom de l'équipe>");
            return true;
        }

        String teamName = args[0];

        if (scoreboard == null) {
            sender.sendMessage(ChatColor.RED + "Le scoreboard n'a pas été initialisé correctement !");
            return true;
        }

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Cette équipe n'existe pas !");
            return true;
        }

        for (UUID playerUUID : playerTeams.keySet()) {
            Team playerTeam = playerTeams.get(playerUUID);
            if (playerTeam != null && playerTeam.equals(team)) {
                playerTeams.remove(playerUUID);
            }
        }

        team.unregister();

        sender.sendMessage(ChatColor.GREEN + "L'équipe " + team.getDisplayName() + ChatColor.GREEN + " a été supprimée !");
        return true;
    }
}
