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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class Delete {

    public static String Delete(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("122437019707244548")
                || event.getAuthor().getId().equals(Settings.hardClearID)) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length != 2) {
                return "command format is $delete 123456";
            }

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            if (mentionedMembers.isEmpty()) {
                return "Please include the @user within the command";
            }

            Vote vote = null;
            try {
                long id = Long.parseLong(split[1]);
                vote = VoteHistoryDataBase.getVote(id);
            } catch (Exception e) {
                return split[1] + " is not a number";
            }
            if (vote == null) {
                return "Can not find. Please check the number again";
            } else {

                //demote Check
                Role role = event.getGuild().getRoleById(Settings.hardClearID);
                long oldRepAmount = UserRepDataBase.getRepNumber(vote.receiver);
                long weight = vote.weight;
                String receiverID = vote.receiver;
                String receiverName = vote.receiverName;
                boolean error = false;
                if (oldRepAmount - weight >= Settings.requiredForHardClear) {
                    error = !RepUser.addToHardClear(event, receiverID);
                    if (!error && oldRepAmount < Settings.requiredForHardClear) {
                        event.getChannel().sendMessage(receiverName + " is now a hard clear member").queue();
                    }
                } else {
                    error = !RepUser.removeFromHardClear(event, receiverID);
                    if (!error && oldRepAmount >= Settings.requiredForHardClear) {
                        event.getChannel().sendMessage(receiverName + " is no longer a hard clear member").queue();
                    }
                }

                UserRepDataBase.setRepNumber(vote.receiver, UserRepDataBase.getRepNumber(vote.receiver) - vote.weight);
                VoteHistoryDataBase.removeVote(vote, true);
                return "Deleted. " + vote.receiverName + " now has " + UserRepDataBase.getRepNumber(vote.receiver) + " rep points";
            }

        }
        return "Sorry you don't have permission for that command";
    }

}
