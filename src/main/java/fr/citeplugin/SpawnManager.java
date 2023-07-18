package fr.citeplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class SpawnManager {
    private Plugin plugin;
    private FileConfiguration teamsConfig;

    public SpawnManager(Plugin plugin, FileConfiguration teamsConfig, File dataFolder) {
        this.plugin = plugin;
        this.teamsConfig = teamsConfig;
    }

    public boolean setSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande peut uniquement être exécutée par un joueur !");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /setSpawn <nom de l'équipe>");
            return true;
        }

        String teamName = args[0];
        if (!teamsConfig.contains("teams." + teamName)) {
            sender.sendMessage(ChatColor.RED + "Cette équipe n'existe pas !");
            return true;
        }

        Location spawnLocation = player.getLocation();
        teamsConfig.set("teams." + teamName + ".spawn", spawnLocation);

        // Sauvegarde de la configuration des équipes
        try {
            teamsConfig.save(new File(plugin.getDataFolder(), "teams.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatColor.GREEN + "Le spawn de l'équipe " + teamName + " a été défini !");
        return true;
    }
}
