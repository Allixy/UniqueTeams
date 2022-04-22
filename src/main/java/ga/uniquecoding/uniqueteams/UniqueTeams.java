package ga.uniquecoding.uniqueteams;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import ga.uniquecoding.uniqueteams.commands.player.TeamCommand;
import ga.uniquecoding.uniqueteams.handlers.PlaceholderHandler;
import ga.uniquecoding.uniqueteams.listeners.EntityDamageListener;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class UniqueTeams extends JavaPlugin {

    public TeamManager manager;
    private PlaceholderHandler placeholderHandler;
    public static UniqueTeams plugin;


    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig());
        CommandAPI.unregister("team");
    }

    @Override
    public void onEnable() {

        Server server = getServer();
        PluginManager pluginManager = server.getPluginManager();

        plugin = this;
        this.manager = new TeamManager();
        placeholderHandler = new PlaceholderHandler();

        CommandAPI.onEnable(this);
        placeholderHandler.register();

        new TeamCommand(this, manager);

        pluginManager.registerEvents(new EntityDamageListener(manager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public TeamManager getManager() {
        return manager;
    }
}
