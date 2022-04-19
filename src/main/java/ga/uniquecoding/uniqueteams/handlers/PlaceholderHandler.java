package ga.uniquecoding.uniqueteams.handlers;

import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHandler extends PlaceholderExpansion {

    public PlaceholderHandler() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "uniqueteams";
    }

    @Override
    public @NotNull String getAuthor() {
        return "RealAllixy";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("color")) {
            TeamManager teamManager = UniqueTeams.plugin.getManager();
            Team team = teamManager.getTeam(player);

            if (team == null) return "";

            String hex = "&" + team.getHex() + "Ⓣ";

            return hex;
        }

        return null;
    }

}
