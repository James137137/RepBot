/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Main;
import com.javadog.repbot.Settings;
import com.javadog.repbot.VoteHistoryDataBase;
import com.javadog.repbot.RepUser;
import com.javadog.repbot.UserRepDataBase;
import com.javadog.repbot.Vote;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class PlusRep {

    public static String PlusRep(MessageReceivedEvent event) {
        if (!Time.canVoteNow(event.getMember().getId())) {
            return "You can't give rep yet. Type $time";
        }
        String receiverID = null;
        Member mentionedMember = null;
        Role role = event.getGuild().getRoleById(Settings.hardClearID);
        Message message2 = event.getMessage();
        List<Member> mentionedMembers = message2.getMentionedMembers();
        if (mentionedMembers.isEmpty()) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length >= 2) {
                receiverID = split[1];
                boolean vaild = RepUser.CheckIfVaildID(event, receiverID);
                if (!vaild) {
                    return "Sorry the number doesn't seem to be vaild. Please double check this";
                }
            } else {
                return "Please tag a user to give rep too!";
            }
        } else {
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

        int weight = 1;
        if (RepUser.isHardClear(event.getMember(), receiverID)) {
            weight = 2;
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

        //User.checkHC(receiverID, event, UserRepDataBase.getRepNumber(receiverID));
        
        //Vote vote = new Vote(event.getMember(), mentionedMember, User.isHardClear(mentionedMember), weight, reason,false);
        Vote vote = new Vote(event.getMember(), receiverID, RepUser.isHardClear(mentionedMember, receiverID), weight, reason, false);
        //vote.updateRecieverName();
        VoteHistoryDataBase.addNewVote(vote);
        long oldRepAmount = UserRepDataBase.getRepNumber(receiverID);
        long newRepAmount = oldRepAmount + weight;
        UserRepDataBase.setRepNumber(receiverID, newRepAmount);

        //Promote Check
        String receiverName = Main.jda.retrieveUserById(receiverID).complete().getName();
        if (mentionedMember != null) {
            receiverName = mentionedMember.getAsMention();
        }

        boolean error = false;
        //
        if (newRepAmount >= Settings.requiredForHardClear) {
            error = !RepUser.addToHardClear(event, receiverID);
            if (!error && newRepAmount - weight < Settings.requiredForHardClear) {
                event.getChannel().sendMessage(receiverName + " is now a hard clear member").queue();
            }
        } else {
            error = !RepUser.removeFromHardClear(event, receiverID);
            if (!error && newRepAmount - weight >= Settings.requiredForHardClear) {
                event.getChannel().sendMessage(receiverName + " is no longer a hard clear member").queue();
            }
        }

        String message = weight + " point has been given to " + receiverName + " and now has a total of " + newRepAmount;
        return message;
    }

}
