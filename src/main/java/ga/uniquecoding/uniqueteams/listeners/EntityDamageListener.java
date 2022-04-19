package ga.uniquecoding.uniqueteams.listeners;

import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity target = e.getEntity();

        if (target instanceof Player) {
            if (e.getDamager() instanceof Player damager) {
                TeamManager teamManager = UniqueTeams.plugin.getManager();
                Team team = teamManager.getTeam(damager);

                if (team == null) return;

                if (team.getMembers().contains(target.getUniqueId())) e.setCancelled(true);
            }
        }
    }
}
