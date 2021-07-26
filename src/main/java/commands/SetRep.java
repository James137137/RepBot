/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Settings;
import com.javadog.repbot.RepUser;
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
public class SetRep {

    public static String SetRep(MessageReceivedEvent event) {
        if (!RepUser.isAdmin(event)) {
            return "Sorry you don't have permission for that command";
        }
        
        Role role = event.getGuild().getRoleById(Settings.hardClearID);
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        if (mentionedMembers.isEmpty()) {
            return "You forgot to tag someone, silly!";
        }

        if (mentionedMembers.size() > 1) {
            return "Please only mention one user with the @user";
        }
        Member mentionedMember = mentionedMembers.get(0);
        
        
        String voterID = event.getMember().getId();
        String receiverID = mentionedMember.getId();
        
        long weight = 1;
        if (RepUser.isHardClear(event.getMember())) {
            weight = 2;
        }
        String[] split = event.getMessage().getContentRaw().split(" ");
        if (split.length < 4) {
            return "You need to provide a reason";
        }
        long newRepAmount = 0;
        try {
            newRepAmount = Long.parseLong(split[2]);
        } catch (Exception e) {
            return split[2] + ": must be a number";
        }
        
        weight = newRepAmount - UserRepDataBase.getRepNumber(mentionedMember.getId());

        String reason = split[3];
        for (int i = 4; i < split.length; i++) {
            reason = reason + " " + split[i];

        }
        if (reason.length() < 5 || reason.length() > 100)
        {
            return "Please enter a reason within 5-100 characters";
        }

        Vote vote = new Vote(event.getMember(), mentionedMember, RepUser.isHardClear(mentionedMember), weight, reason,true);
        VoteHistoryDataBase.addNewVote(vote);
        
        UserRepDataBase.setRepNumber(receiverID, newRepAmount);

        //Promote Check
        if (RepUser.isHardClear(mentionedMember)) {
            if (newRepAmount < Settings.requiredForHardClear) {
                event.getGuild().removeRoleFromMember(mentionedMember, role).queue();
                event.getChannel().sendMessage(mentionedMember.getAsMention() + " is no longer a hard clear member").queue();
            }

        } else
        {
            if (newRepAmount >= Settings.requiredForHardClear) {
                AuditableRestAction<Void> addRoleToMember = event.getGuild().addRoleToMember(mentionedMember, role);
                addRoleToMember.queue();
                event.getChannel().sendMessage(mentionedMember.getAsMention() + " is now a hard clear member").queue();
            }
        }

        String message = mentionedMember.getAsMention() + " now has a total of " + newRepAmount + " rep points";
        return message;
    }
    
}
