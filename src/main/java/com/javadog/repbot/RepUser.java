/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class RepUser {

    public static boolean isHardClear(Member member) {
        for (Role role : member.getRoles()) {
            if (role.getId().equalsIgnoreCase(Settings.hardClearID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHardClear(Member member, String userID) {
        if (member == null) {
            return isHardClear(userID);
        }
        for (Role role : member.getRoles()) {
            if (role.getId().equals(Settings.hardClearID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHardClear(String userID) {
        return UserRepDataBase.getRepNumber(userID) >= Settings.requiredForHardClear;
    }

    public static void checkHC_disable(String userID, MessageReceivedEvent event, long repAmount) {
        
        event.getTextChannel().sendMessage("A part of the code was called but should be disabled....").queue();
        
        
        List<Role> roles = event.getGuild().getMemberById(userID).getRoles();
        if (roles != null && repAmount == 0) {
            for (Role role : roles) {
                if (role.getName().equals("disabled")) {
                    UserRepDataBase.setRepNumber(userID, Settings.requiredForHardClear);
                    Vote vote = new Vote(event.getMember(), userID, true, 10, "automatic: had hardclear already", true);
                    VoteHistoryDataBase.addNewVote(vote);
                    RepUser.addToHardClear(event, userID);
                }
            }

        }
        if (repAmount >= Settings.requiredForHardClear) {
            RepUser.addToHardClear(event, userID);

        } else {
            RepUser.removeFromHardClear(event, userID);

        }
    }

    public static boolean removeFromHardClear(MessageReceivedEvent event, String userid) {
        Role role = event.getGuild().getRoleById(Settings.hardClearID);
        try {
            event.getGuild().removeRoleFromMember(userid, role).complete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean addToHardClear(MessageReceivedEvent event, String userID) {
        Role role = event.getGuild().getRoleById(Settings.hardClearID);
        try {
            event.getGuild().addRoleToMember(userID, role).complete();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean CheckIfVaildID(MessageReceivedEvent event, String receiverID) {

        try {
            Main.jda.retrieveUserById(receiverID).complete().getName();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static long getWeight(Member member, String userID) {
        if (isHardClear(member, userID)) {
            return 2;
        } else {
            return 1;
        }
    }
    
    public static boolean isAdmin(MessageReceivedEvent event)
    {
        return (event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("122437019707244548")
                || event.getAuthor().getId().equals("164254093865385985"));
    }

}
