/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Main;
import com.javadog.repbot.Settings;
import com.javadog.repbot.User;
import com.javadog.repbot.UserRepDataBase;
import com.javadog.repbot.Vote;
import com.javadog.repbot.VoteHistoryDataBase;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

/**
 *
 * @author James Anderson
 */
public class MinusRep {

    
    public static String MinusRep(MessageReceivedEvent event) {
        if (!Time.canVoteNow(event.getMember().getId())) {
            return "You can't give rep yet. Type $time" ;
        }
        String receiverID = null;
        Member mentionedMember = null;
        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        if (mentionedMembers.isEmpty()) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length >= 2) {
                receiverID = split[1];
                boolean vaild = User.CheckIfVaildID(event, receiverID);
                if (!vaild) {
                    return "Sorry the number doesn't seem to be vaild. Please double check this";
                }
            } else {
                return "Please tag a user to give rep too!";
            }
        } else
        {
            mentionedMember = mentionedMembers.get(0);
            receiverID = mentionedMember.getId();
        }

       /* if (mentionedMembers.size() > 1) {
            return "Please only mention one user with the @user";
        } */
        
        
        if (!Main.debug && event.getMember().getId().equals(receiverID)) {
            return "You can not rep yourself";
        }

        String voterID = event.getMember().getId();
        
        if (!Main.debug && VoteHistoryDataBase.hasPlusAlreadyVotedForUser(voterID, receiverID)) {
            return "You can only give rep to someone once";
        }

        if (UserRepDataBase.getRepNumber(receiverID) >= UserRepDataBase.getMaxRep()) {
            return "This user is already maxed out their rep points of " + UserRepDataBase.getMaxRep();
        }

        int weight = -1;
        if (User.isHardClear(event.getMember())) {
            weight = -2;
        }
        String[] split = event.getMessage().getContentRaw().split(" ");
        if (split.length <= 2) {
            return "You need to provide a reason";
        }

        String reason = split[2];
        for (int i = 3; i < split.length; i++) {
            reason = reason + " " + split[i];

        }
        if (reason.length() < 5 || reason.length() > 100) {
            return "Please enter a reason within 5-100 characters";
        }

        if (reason.contains("\n")) {
            return "Sorry please don't include extra lines";
        }

        long repNumber = UserRepDataBase.getRepNumber(receiverID);
        if (User.isHardClear(mentionedMember,receiverID) && repNumber == 0) {
            repNumber = Settings.requiredForHardClear;
            UserRepDataBase.setRepNumber(receiverID, repNumber);
            Vote vote = new Vote(mentionedMember, receiverID, User.isHardClear(mentionedMember,receiverID), 10, "automatic: had hardclear already", true);
            VoteHistoryDataBase.addNewVote(vote);
            User.addToHardClear(event, receiverID);
        }

        //Vote vote = new Vote(event.getMember(), mentionedMember, User.isHardClear(mentionedMember), weight, reason,false);
        Vote vote = new Vote(event.getMember(), receiverID, User.isHardClear(mentionedMember,receiverID), weight, reason, false);
        //vote.updateRecieverName();
        VoteHistoryDataBase.addNewVote(vote);
        long oldRepAmount = UserRepDataBase.getRepNumber(receiverID);
        long newRepAmount = oldRepAmount + weight;
        UserRepDataBase.setRepNumber(receiverID, newRepAmount);

        //Promote Check
        String receiverName = Main.jda.retrieveUserById(receiverID).complete().getName();
        if (mentionedMember != null) receiverName = mentionedMember.getAsMention();
        
        
        if (newRepAmount >= Settings.requiredForHardClear) {
            User.addToHardClear(event, receiverID);
            if (newRepAmount - weight < Settings.requiredForHardClear)
            {
                event.getChannel().sendMessage(receiverName + " is now a " + Settings.HardClearName).queue();
            }
        } else
        {
            User.removeFromHardClear(event, receiverID);
            if (newRepAmount - weight >= Settings.requiredForHardClear)
            {
                event.getChannel().sendMessage(receiverName + " is no longer a " + Settings.HardClearName).queue();
            }
        }

        String message = weight + " point has been taken from " + receiverName + " and now has a total of " + newRepAmount;
        return message;
    }
    
    
    public static String MinusRepOld(MessageReceivedEvent event) {
        if (!Time.canVoteNow(event.getMember().getId()))
        {
            return "You can't rep yet. " + Time.Time(event.getMember().getId());
        }
        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        if (mentionedMembers.isEmpty()) {
            return "Please tag a user to give rep too!";
        }

        if (mentionedMembers.size() > 1) {
            return "Please only mention one user with the @user";
        }
        Member mentionedMember = mentionedMembers.get(0);
        if (!Main.debug && event.getMember().getId().equals(mentionedMember.getId()))
        {
            return "You can not rep yourself";
        }
        String voterID = event.getMember().getId();
        String receiverID = mentionedMember.getId();
        if (!Main.debug && VoteHistoryDataBase.hasMinusAlreadyVotedForUser(voterID, receiverID)) {
            return "You can only remove rep to someone once";
        }

        int weight = -1;
        if (User.isHardClear(event.getMember())) {
            weight = -2;
        }
        String[] split = event.getMessage().getContentRaw().split(" ");
        if (split.length <= 2) {
            return "You need to provide a reason";
        }

        String reason = split[2];
        for (int i = 3; i < split.length; i++) {
            reason = reason + " " + split[i];
        }
        if (reason.length() < 5 || reason.length() > 100)
        {
            return "Please enter a reason within 5-100 characters";
        }
        
        if (reason.contains("\n"))
        {
            return "Sorry please don't include extra lines";
        }
        
        long repNumber = UserRepDataBase.getRepNumber(mentionedMember.getId());
        if (User.isHardClear(mentionedMember) && repNumber == 0)
        {
            repNumber = Settings.requiredForHardClear;
            UserRepDataBase.setRepNumber(mentionedMember.getId(), repNumber);
            Vote vote = new Vote(mentionedMember, mentionedMember, User.isHardClear(mentionedMember), 10, "automatic: had hardclear already",true);
            VoteHistoryDataBase.addNewVote(vote);
        }

        //Vote vote = new Vote(event.getMember(), mentionedMember, User.isHardClear(mentionedMember), weight, reason,false);
        Vote vote = new Vote(event.getMember(), mentionedMember.getId(), User.isHardClear(mentionedMember), weight, reason,false);
        //vote.updateRecieverName();
        VoteHistoryDataBase.addNewVote(vote);
        
        long newRepAmount = UserRepDataBase.getRepNumber(receiverID) + weight;
        UserRepDataBase.setRepNumber(receiverID, newRepAmount);
        
        //demote Check
        if (User.isHardClear(mentionedMember)) {
            if (newRepAmount < Settings.requiredForHardClear) {
                event.getGuild().removeRoleFromMember(mentionedMember, role).queue();
                event.getChannel().sendMessage(mentionedMember.getAsMention() + " is no longer a " + Settings.HardClearName).queue();
            }

        } else
        {
            if (newRepAmount >= Settings.requiredForHardClear) {
                AuditableRestAction<Void> addRoleToMember = event.getGuild().addRoleToMember(mentionedMember, role);
                addRoleToMember.queue();
                event.getChannel().sendMessage(mentionedMember.getAsMention() + " is now a " + Settings.HardClearName).queue();
            }
        }
        
        String message = weight + " point has been taken from " + mentionedMember.getAsMention() + " and now has a total of " + newRepAmount;
        return message;
    }

}
