package ga.uniquecoding.uniqueteams.listeners;

import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    @EventHandler
    public void onHit(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        EntityDamageEvent.DamageCause cause = e.getCause();

        if (entity instanceof Player target) {
            if (entity.getLastDamageCause() == null) return;
            Entity lDmgEntity = entity.getLastDamageCause().getEntity();
            if (lDmgEntity instanceof Player player) {
                TeamManager teamManager = UniqueTeams.plugin.getManager();
                Team team = teamManager.getTeam(player);

                if (team.getMembers().contains(target.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
