package fr.citeplugin.commands;

import fr.citeplugin.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreateTeamCommand implements CommandExecutor {
    private TeamManager teamManager;

    public CreateTeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return teamManager.createTeam(sender, args);
    }
}
