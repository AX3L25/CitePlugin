package fr.citeplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class SetTeamSizeCommand implements CommandExecutor {
    private Scoreboard scoreboard;
    private Map<String, Integer> teamSizes;

    public SetTeamSizeCommand(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.teamSizes = teamSizes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /setTeamSize <nom de l'équipe> <nombre_max_joueurs>");
            return true;
        }

        String teamName = args[0];
        int maxPlayers;

        try {
            maxPlayers = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Le nombre maximum de joueurs doit être un entier !");
            return true;
        }

        if (scoreboard == null) {
            sender.sendMessage(ChatColor.RED + "Le scoreboard n'a pas été initialisé correctement !");
            return true;
        }

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Cette équipe n'existe pas !");
            return true;
        }

        teamSizes.put(teamName, maxPlayers);

        sender.sendMessage(ChatColor.GREEN + "La taille maximale de l'équipe " + team.getDisplayName() + ChatColor.GREEN + " a été redéfinie !");
        return true;
    }
}
