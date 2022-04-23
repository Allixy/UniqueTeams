package ga.uniquecoding.uniqueteams;

import ga.uniquecoding.uniqueteams.managers.TeamManager;
import ga.uniquecoding.uniqueteams.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Team {

    private HashMap<UUID, String> members = new HashMap<>();
    private String hex;

    public Team(Player owner) {
        members.put(owner.getUniqueId(), "owner");
    }

    public boolean isInTeam(Player player) {
        return members.containsKey(player.getUniqueId());
    }

    public Set<UUID> getMembers() {
        return members.keySet();
    }

    public UUID getOwner() {
        Set<UUID> keySet = members.keySet();
        for (UUID uuid : keySet) {
            if (members.get(uuid).equals("owner")) return uuid;
        }
        return null;
    }

    public void setRank(Player player, String rank) {
        if (rank.equals("owner")) {
            UUID uuid = getOwner();
            members.replace(uuid, "owner", "default");
            members.replace(player.getUniqueId(), members.get(player.getUniqueId()), "owner");
        } else {
            members.replace(player.getUniqueId(), members.get(player.getUniqueId()), rank);
        }
    }

    public void disband() {
        TeamManager manager = UniqueTeams.plugin.getManager();
        Player owner = Bukkit.getPlayer(getOwner());

        if (owner == null) return;

        manager.getTeams().remove(owner.getName());

        for (UUID uuid : getMembers()) {
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) return;

            target.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c The team has been disbanded!"));
            manager.getPlayers().remove(uuid);
        }

        members.clear();
    }

    public void addMember(Player player, String rank) {
        members.put(player.getUniqueId(), rank);
    }

    public void removeMember(Player player) {
        if (members.containsKey(player.getUniqueId())) {
            members.remove(player.getUniqueId());
        }
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public String getRank(Player player) {
        if (members.containsKey(player.getUniqueId())) {
            return members.get(player.getUniqueId());
        }
        return null;
    }
}
