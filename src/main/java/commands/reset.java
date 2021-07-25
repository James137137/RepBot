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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class reset {

    public static String Reset(MessageReceivedEvent event) {
        if (User.isAdmin(event)) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length != 2) {
                return "command format is $reset @user";
            }

            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            if (mentionedMembers.isEmpty()) {
                return "Please include the @user within the command";
            }
            Member mentionedMember = mentionedMembers.get(0);

            List<Vote> voteList = VoteHistoryDataBase.getVoteListReciver(mentionedMember.getId());
            if (voteList != null && voteList.size() >= 1) {
                for (Vote vote : voteList) {
                    VoteHistoryDataBase.removeVote(vote,true);
                }
            }
            
            Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
            UserRepDataBase.setRepNumber(mentionedMember.getId(), 0);

            event.getGuild().removeRoleFromMember(mentionedMember, role).queue();
            event.getChannel().sendMessage(mentionedMember.getAsMention() + " is no longer a " + Settings.HardClearName).queue();
            return "Reset complete. " + mentionedMember.getAsMention() + " now has " + UserRepDataBase.getRepNumber(mentionedMember.getId()) + " rep points";

        }
        return "Sorry you don't have permission for that command";
    }
}
