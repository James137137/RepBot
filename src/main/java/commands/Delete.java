/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Settings;
import com.javadog.repbot.User;
import com.javadog.repbot.UserRepDataBase;
import com.javadog.repbot.Vote;
import com.javadog.repbot.VoteHistoryDataBase;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

/**
 *
 * @author James Anderson
 */
public class Delete {

    public static String Delete(MessageReceivedEvent event) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("122437019707244548")) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length != 3) {
                return "command format is $delete 123456 @user";
            }

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            if (mentionedMembers.isEmpty()) {
                return "Please include the @user within the command";
            }
            Member mentionedMember = mentionedMembers.get(0);

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
                Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
                long newRepAmount = UserRepDataBase.getRepNumber(vote.receiver) - vote.weight;
                if (User.isHardClear(mentionedMember)) {
                    if (newRepAmount < Settings.requiredForHardClear) {
                        event.getGuild().removeRoleFromMember(mentionedMember, role).queue();
                        event.getChannel().sendMessage(mentionedMember.getAsMention() + " is no longer a " + Settings.HardClearName).queue();
                    }

                } else {
                    if (newRepAmount >= Settings.requiredForHardClear) {
                        AuditableRestAction<Void> addRoleToMember = event.getGuild().addRoleToMember(mentionedMember, role);
                        addRoleToMember.queue();
                        event.getChannel().sendMessage(mentionedMember.getAsMention() + " is now a " + Settings.HardClearName).queue();
                    }
                }
                
                
                
                UserRepDataBase.setRepNumber(vote.receiver, UserRepDataBase.getRepNumber(vote.receiver) - vote.weight);
                VoteHistoryDataBase.removeVote(vote,true);
                return "Deleted. " + vote.receiverName + " now has " + UserRepDataBase.getRepNumber(vote.receiver) + " rep points";
            }

        }
        return "Sorry you don't have permission for that command";
    }

}
