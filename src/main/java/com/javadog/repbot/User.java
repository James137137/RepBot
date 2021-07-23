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

    public static void checkHardClear(String userID, MessageReceivedEvent event, long repAmount) {

        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        List<Member> members = event.getGuild().getMembers();
        Member member = null;
        for (Member m : members) {
            if (m.getId().equals(userID)) {
                member = m;
            }
            break;
        }

        if (User.isHardClear(member)) {
            if (repAmount < Settings.requiredForHardClear) {
                event.getGuild().removeRoleFromMember(member, role).queue();
                event.getChannel().sendMessage(member.getEffectiveName() + " is no longer a " + Settings.HardClearName).queue();
            }

        } else {
            if (repAmount >= Settings.requiredForHardClear) {
                AuditableRestAction<Void> addRoleToMember = event.getGuild().addRoleToMember(member, role);
                addRoleToMember.queue();
                event.getChannel().sendMessage(member.getEffectiveName() + " is now a " + Settings.HardClearName).queue();
            }
        }
    }

    public static boolean removeFromHardClear(MessageReceivedEvent event, String userid){
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

}
