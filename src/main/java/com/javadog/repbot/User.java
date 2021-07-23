/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

/**
 *
 * @author James Anderson
 */
public class User {

    public static boolean isHardClear(Member member) {
        for (Role role : member.getRoles()) {
            if (role.getName().equalsIgnoreCase(Settings.HardClearName)) {
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
            if (role.getName().equalsIgnoreCase(Settings.HardClearName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHardClear(String userID) {
        return UserRepDataBase.getRepNumber(userID) >= Settings.requiredForHardClear;
    }

    public static void checkHC(String userID, MessageReceivedEvent event, long repAmount) {

        List<Role> roles = event.getGuild().getMemberById(userID).getRoles();
        if (roles != null && repAmount == 0) {
            for (Role role : roles) {
                if (role.getName().equals(Settings.HardClearName)) {
                    UserRepDataBase.setRepNumber(userID, Settings.requiredForHardClear);
                    Vote vote = new Vote(event.getMember(), userID, true, 10, "automatic: had hardclear already", true);
                    VoteHistoryDataBase.addNewVote(vote);
                    User.addToHardClear(event, userID);
                }
            }

        }
        if (repAmount >= Settings.requiredForHardClear) {
            User.addToHardClear(event, userID);

        } else {
            User.removeFromHardClear(event, userID);

        }
    }

    public static boolean removeFromHardClear(MessageReceivedEvent event, String userid) {
        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        try {
            event.getGuild().removeRoleFromMember(userid, role).complete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean addToHardClear(MessageReceivedEvent event, String userID) {
        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
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

    private static boolean Guild(String userID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
