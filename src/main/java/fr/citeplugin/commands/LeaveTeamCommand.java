package fr.citeplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class LeaveTeamCommand implements CommandExecutor {
    private Map<UUID, Team> playerTeams;

    public LeaveTeamCommand(Map<UUID, Team> playerTeams) {
        this.playerTeams = playerTeams;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande peut uniquement être exécutée par un joueur !");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (!playerTeams.containsKey(playerUUID)) {
            sender.sendMessage(ChatColor.RED + "Vous ne faites partie d'aucune équipe !");
            return true;
        }

        Team team = playerTeams.get(playerUUID);
        team.removeEntry(player.getName());
        playerTeams.remove(playerUUID);

        sender.sendMessage(ChatColor.GREEN + "Vous avez quitté l'équipe " + team.getDisplayName() + ChatColor.GREEN + " !");
        return true;
    }
}

