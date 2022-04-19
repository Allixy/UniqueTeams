package ga.uniquecoding.uniqueteams.listeners;

import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    private TeamManager teamManager;

    public EntityDamageListener(TeamManager manager){
        this.teamManager = manager;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {

        Entity target = e.getEntity();

        if (target instanceof Player) {
            if (e.getDamager() instanceof Player damager) {
                Team team = teamManager.getTeam(damager);
                if (team == null) return;
                if (team.getMembers().contains(target.getUniqueId())) e.setCancelled(true);
            }else if(e.getDamager() instanceof Projectile projectile){
                if(projectile.getShooter() instanceof Player damager){
                    Team team = teamManager.getTeam(damager);
                    if (team == null) return;
                    if (team.getMembers().contains(target.getUniqueId())) e.setCancelled(true);
                }
            }
        }
    }
}