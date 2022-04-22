package ga.uniquecoding.uniqueteams.commands.player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import ga.uniquecoding.uniqueteams.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamCommand {

    public TeamCommand(UniqueTeams plugin, TeamManager manager) {

        CommandAPICommand teamCreate = new CommandAPICommand("create")
                .executesPlayer((player, args) -> {
                    if (!manager.isInTeam(player)) {
                        manager.createTeam(player);
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&a Successfully created a new team!"));
                    } else {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You cannot create a team if you're already in a team!"));
                    }
                });

        CommandAPICommand teamInvite = new CommandAPICommand("invite")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];

                    if (player == target) {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You cannot invite yourself to your own team!"));
                        return;
                    }

                    if (manager.isInTeam(player)) {
                        Team team = manager.getTeam(player);
                        Player owner = Bukkit.getPlayer(team.getOwner());

                        if (owner == null) {
                            System.out.println("Error: The owner cannot be found for team " + team);
                            return;
                        }

                        if (team.getMembers().contains(target.getUniqueId())) {
                            player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c That player is already in your team!"));
                            return;
                        }

                        if (manager.isInvited(owner, target.getUniqueId())) {
                            player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You've already invited this player to the team!"));
                            return;
                        }

                        if (team.isInTeam(target)) {
                            player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c This player is already in your team!"));
                            return;
                        }

                        if (team.getMembers().size() == 3) {
                            player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You cannot have more than 3 members in your team!"));
                            return;
                        }

                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&f Sent a team invite to&6 " + target.getName() + "&f!"));
                        target.sendMessage(HexUtils.colorify("&6&lTEAM &8»&f You've been invited to join&6 " + owner.getName() + "'s&f team!&a /team accept " + owner.getName()));

                        manager.invitePlayer(owner, target.getUniqueId());
                    } else {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You don't own a team! /team create"));
                    }
                });

        CommandAPICommand teamAccept = new CommandAPICommand("accept")
                .withArguments(new PlayerArgument("players"))
                .executesPlayer((player, args) -> {
                    Player owner = (Player) args[0];

                    if (manager.isInTeam(player)) {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You're already in a team!"));
                        return;
                    }

                    if (owner == null) {
                        System.out.println("Error: The owner cannot be found, name:" + player.getName());
                        return;
                    }

                    if (manager.isInvited(owner, player.getUniqueId())) {
                        Team team = manager.getTeam(owner);

                        if (team == null) {
                            System.out.println("Error: A team cannot be found for " + player.getName());
                            return;
                        }

                        manager.removePlayerInvite(owner, player.getUniqueId());

                        manager.addPlayer(owner, player, "default");
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&a Joined&6 " + owner.getName() + "'s&a team!"));

                        team.getMembers().forEach(uuid -> {
                            Player target = Bukkit.getPlayer(uuid);
                            if (target == null) return;
                            target.sendMessage(HexUtils.colorify("&6&lTEAM &8»&6 " + player.getName() + "&a joined the team!"));
                        });
                    } else {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You don't have any pending invites from that team!"));
                    }
                });

        CommandAPICommand teamLeave = new CommandAPICommand("leave")
                .executesPlayer((player, args) -> {
                            Team team = manager.getTeam(player);

                            if (team == null) {
                                System.out.println("Error: A team cannot be found for " + player.getName());
                                return;
                            }

                            if (manager.isInTeam(player)) {

                                Player owner = Bukkit.getPlayer(team.getOwner());

                                if (team.getRank(player).equals("owner")) {
                                    player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You can't leave the team because you're the team owner! You can disband the team instead. /team disband"));
                                    return;
                                }

                                manager.removePlayer(owner, player);
                                team.removeMember(player);
                                player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c You left the team!"));

                                team.getMembers().forEach(uuid -> {
                                    Player target = Bukkit.getPlayer(uuid);
                                    if (target == null) return;
                                    if (team.getMembers().size() == 0) return;
                                    target.sendMessage(HexUtils.colorify("&6&lTEAM &8»&c " + player.getName() + " left the team!"));
                                });

                            }
                        });

        CommandAPICommand teamList = new CommandAPICommand("list")
                .executesPlayer((player, args) -> {
                    Team team = manager.getTeam(player);
                    List<String> members = new ArrayList<>();

                    if (team == null) {
                        System.out.println("Error: A team cannot be found for " + player.getName());
                        return;
                    }

                    for (UUID uuid : team.getMembers()) {
                        Player member = Bukkit.getPlayer(uuid);
                        if (member == null) return;
                        members.add(member.getName());
                    }

                    String memberList = members.toString().replaceAll("(^\\[|\\]$)", "");

                    if (manager.isInTeam(player)) {
                        player.sendMessage(HexUtils.colorify("&6Team members:&f\n" + memberList));
                    } else {
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&cYou're not in a team!"));
                    }

                });

        CommandAPICommand teamDisband = new CommandAPICommand("disband")
                .executesPlayer((player, args) -> {
                    Team team = manager.getTeam(player);

                    if (team == null) {
                        System.out.println("Error: A team cannot be found for " + player.getName());
                        return;
                    }

                    Player owner = Bukkit.getPlayer(team.getOwner());

                    if (owner == null) {
                        System.out.println("Error: The owner cannot be found, name:" + player.getName());
                        return;
                    }


                    if (team.getOwner().equals(player.getUniqueId())) {
                        team.disband();
                        player.sendMessage(HexUtils.colorify("&6&lTEAM &8»&a You've disbanded your team!"));
                    }
                });

        new CommandAPICommand("team")
                .withSubcommand(teamCreate)
                .withSubcommand(teamInvite)
                .withSubcommand(teamLeave)
                .withSubcommand(teamList)
                .withSubcommand(teamDisband)
                .withSubcommand(teamAccept).register();
    }
}