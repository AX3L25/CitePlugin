package fr.citeplugin.commands;

import fr.citeplugin.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class JoinTeamCommand implements CommandExecutor {
    private TeamManager teamManager;
    private Map<UUID, Team> playerTeams;

    public JoinTeamCommand(TeamManager teamManager, Map<UUID, Team> playerTeams) {
        this.teamManager = teamManager;
        this.playerTeams = playerTeams;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return teamManager.joinTeam(sender, args, playerTeams);
    }
}

