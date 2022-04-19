package ga.uniquecoding.uniqueteams.managers;

import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.utils.HexUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static ga.uniquecoding.uniqueteams.UniqueTeams.plugin;

public class TeamManager {

    private HashMap<String, Team> teams = new HashMap<>();
    private HashMap<UUID, Team> players = new HashMap<>();
    private static HashMap<UUID, Team> invitedPlayers = new HashMap<>();

    public void createTeam(Player owner) {
        if (players.containsKey(owner.getUniqueId())) return;

        Team team = new Team(owner);

        team.setHex(HexUtils.randomHex());
        teams.put(owner.getName(), team);
        players.put(owner.getUniqueId(), team);
    }

    public void addPlayer(Player owner, Player player, String rank) {
        if (teams.containsKey(owner.getName()) && !players.containsKey(player.getUniqueId())) {
            Team team = teams.get(owner.getName());

            team.addMember(player, rank);
            players.put(player.getUniqueId(), team);
        }
    }

    public void removePlayer(Player owner, Player player) {
        if (players.containsKey(player.getUniqueId())) {
            players.remove(player.getUniqueId());

            Team team = teams.get(owner.getName());

            if (team == null) return;

            String rank = team.getRank(player);

            team.removeMember(player);

            if (rank.equals("owner")) {
                Set<UUID> members = team.getMembers();

                for (UUID uuid : members) {
                    players.remove(uuid);
                }
                teams.remove(owner.getName());
            }
        }
    }

    public void invitePlayer(Player owner, UUID uuid) {
        Team team = getTeam(owner);

        invitedPlayers.put(uuid, team);

        new BukkitRunnable() {
            @Override
            public void run() {
                invitedPlayers.remove(uuid);
            }
        }.runTaskLaterAsynchronously(plugin, 60 * 20L);
    }

    public void removePlayerInvite(Player owner, UUID uuid) {
        Team team = getTeam(owner);

        if (invitedPlayers.get(uuid) == team) {
            invitedPlayers.remove(uuid);
        }
    }

    public boolean isInvited(Player owner, UUID uuid) {
        Team team = teams.get(owner.getName());

        if (invitedPlayers.get(uuid) == null) {
            return false;
        } else {
            return invitedPlayers.get(uuid).equals(team);
        }
    }

    public HashMap<UUID, Team> getPlayers() {
        return players;
    }

    public boolean isInTeam(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public Team getTeam(Player player) {
        if (players.containsKey(player.getUniqueId())) return players.get(player.getUniqueId());
        return null;
    }

    public Set<String> getTeams() {
        return teams.keySet();
    }
}
