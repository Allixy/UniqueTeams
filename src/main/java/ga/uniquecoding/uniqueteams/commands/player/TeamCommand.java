package ga.uniquecoding.uniqueteams.commands.player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import ga.uniquecoding.uniqueteams.Team;
import ga.uniquecoding.uniqueteams.UniqueTeams;
import ga.uniquecoding.uniqueteams.managers.TeamManager;
import ga.uniquecoding.uniqueteams.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamCommand {

    public TeamCommand() {

        CommandAPICommand teamCreate = new CommandAPICommand("create")
                .executesPlayer((player, args) -> {
                    TeamManager teamManager = UniqueTeams.plugin.getManager();

                    if (!teamManager.isInTeam(player)) {
                        teamManager.createTeam(player);
                        player.sendMessage(HexUtils.colorify("&aSuccessfully created a new team!"));
                    } else {
                        player.sendMessage(HexUtils.colorify("&cYou cannot create a team if you're already in a team!"));
                    }
                });

        CommandAPICommand teamInvite = new CommandAPICommand("invite")
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];
                    TeamManager teamManager = UniqueTeams.plugin.getManager();

                    if (player == target) {
                        player.sendMessage(HexUtils.colorify("&cYou cannot invite yourself to your own team!"));
                        return;
                    }

                    if (!teamManager.isInTeam(player)) {
                        teamManager.createTeam(player);

                        Team team = teamManager.getTeam(player);

                        Player owner = Bukkit.getPlayer(team.getOwner());

                        if (owner == null) return;

                        player.sendMessage(HexUtils.colorify("&aCreated a team!"));
                        player.sendMessage(HexUtils.colorify("&6Sent a team invite to " + target.getName() + "!"));
                        target.sendMessage(HexUtils.colorify("&6You've been invited to join " + owner.getName() + "'s team! /team accept " + owner.getName()));
                        teamManager.invitePlayer(player, target.getUniqueId());
                    } else {
                        Team team = teamManager.getTeam(player);
                        Player owner = Bukkit.getPlayer(team.getOwner());

                        if (owner == null) return;

                        if (teamManager.isInvited(owner, target.getUniqueId())) {
                            player.sendMessage(HexUtils.colorify("&cYou've already invited this player to the party!"));
                            return;
                        }

                        if (teamManager.isInTeam(target)) {
                            player.sendMessage(HexUtils.colorify("&cThis player is already in your party!"));
                            return;
                        }

                        if (team.getMembers().size() == 3) {
                            player.sendMessage(HexUtils.colorify("&cYou cannot have more than 3 members in your team!"));
                            return;
                        }
                        player.sendMessage(HexUtils.colorify("&6Sent a team invite to " + target.getName() + "!"));
                        target.sendMessage(HexUtils.colorify("&6You've been invited to join " + owner.getName() + "'s team! /team accept " + owner.getName()));
                        teamManager.invitePlayer(player, target.getUniqueId());
                    }
                });

        CommandAPICommand teamAccept = new CommandAPICommand("accept")
                .withArguments(new PlayerArgument("players"))
                .executesPlayer((player, args) -> {
                    Player owner = (Player) args[0];
                    TeamManager teamManager = UniqueTeams.plugin.getManager();

                    if (teamManager.isInTeam(player)) {
                        player.sendMessage(HexUtils.colorify("&cYou're already in a team!"));
                        return;
                    }

                    if (owner == null) return;

                    if (teamManager.isInvited(owner, player.getUniqueId())) {
                        if (teamManager.getTeam(owner).getMembers().size() == 3) {
                            player.sendMessage(HexUtils.colorify("&cYou cannot have more than 3 members in your team!"));
                            teamManager.removePlayerInvite(owner, player.getUniqueId());
                            return;
                        }

                        teamManager.addPlayer(owner, player, "member");
                        player.sendMessage(HexUtils.colorify("&6Joined " + owner.getName() + "'s team!"));
                        teamManager.removePlayerInvite(owner, player.getUniqueId());

                        teamManager.getPlayers().forEach((uuid, team) -> {
                            Player target = Bukkit.getPlayer(uuid);

                            if (target == null) return;

                            target.sendMessage(HexUtils.colorify("&6" + player.getName() + " joined the team!"));
                        });

                    } else {
                        player.sendMessage(HexUtils.colorify("&cYou don't have any pending invites from that team!"));
                    }
                });

        CommandAPICommand teamLeave = new CommandAPICommand("leave")
                .executesPlayer((player, args) -> {
                    Player owner = player.getPlayer();
                    TeamManager teamManager = UniqueTeams.plugin.getManager();

                    if (teamManager.isInTeam(player)) {
                        teamManager.removePlayer(owner, player);
                        player.sendMessage(HexUtils.colorify("&6You left " + owner.getName() + "'s team!"));

                        teamManager.getPlayers().forEach((uuid, team) -> {
                            Player target = Bukkit.getPlayer(uuid);

                            if (target == null) return;

                            target.sendMessage(HexUtils.colorify("&6" + player.getName() + " left the team!"));
                        });
                    }
                });

        CommandAPICommand teamList = new CommandAPICommand("list")
                .executesPlayer((player, args) -> {
                    TeamManager teamManager = UniqueTeams.plugin.getManager();
                    Team team = teamManager.getTeam(player);
                    List<String> members = new ArrayList<>();

                    if (team == null) return;

                    for (UUID uuid : team.getMembers()) {
                        Player member = Bukkit.getPlayer(uuid);

                        if (member == null) return;

                        members.add(member.getName());
                    }

                    String memberList = members.toString().replaceAll("(^\\[|\\]$)", "");

                    if (teamManager.isInTeam(player)) {
                        player.sendMessage(HexUtils.colorify("&aTeam members:&e\n" + memberList));
                    }

                });

        new CommandAPICommand("team")
                .withSubcommand(teamCreate)
                .withSubcommand(teamInvite)
                .withSubcommand(teamLeave)
                .withSubcommand(teamList)
                .withSubcommand(teamAccept).register();
    }
}