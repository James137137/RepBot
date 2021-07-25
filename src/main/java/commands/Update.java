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
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class Update {

    public static void Update(MessageReceivedEvent event) {
        boolean selfupdate = false;
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        if (mentionedMembers == null || mentionedMembers.isEmpty()) {
            selfupdate = true;
        }
        String output = "";
        for (Member mentionedMember : mentionedMembers) {
            try {
                checkForHCAndUpdate(mentionedMember, event);
            } catch (Exception ex) {
                Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
                event.getTextChannel().sendMessage(
                        OnCommand.getEmbed("Update", output, Color.RED)
                ).queue();
            }

        }

        if (selfupdate) {
            try {
                checkForHCAndUpdate(event.getMember(), event);
                return;
            } catch (Exception ex) {
                output = "Errored";
                Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (output.isEmpty()) return;
        event.getTextChannel().sendMessage(
                OnCommand.getEmbed("Update", output, Color.red)
        ).queue();
    }

    private static void checkForHCAndUpdate(Member mentionedMember, MessageReceivedEvent event) throws Exception {
        boolean hasHardClearRole = false;
        Role HCRole = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        List<Role> roles = mentionedMember.getRoles();
        for (Role role : roles) {
            hasHardClearRole = role.getName().equals(HCRole.getName());
            if (hasHardClearRole) {
                break;
            }

        }
        long repNumber = UserRepDataBase.getRepNumber(mentionedMember.getId());
        if (!hasHardClearRole && repNumber >= Settings.requiredForHardClear) {
            User.addToHardClear(event, mentionedMember.getId());
            event.getTextChannel().sendMessage(OnCommand.getEmbed("Update",
                    mentionedMember.getAsMention() + " has got the required rep but no role. This has been fixed and is now HardClear",
                    Color.RED)).queue();
            return;
        }

        if (hasHardClearRole && repNumber < Settings.requiredForHardClear) {
            UserRepDataBase.setRepNumber(mentionedMember.getId(), Settings.requiredForHardClear);
            Vote vote = new Vote(event.getMember(), mentionedMember.getId(), true, 10, "automatic: had hardclear already", true);
            VoteHistoryDataBase.addNewVote(vote);
            event.getTextChannel().sendMessage(OnCommand.getEmbed("Update",
                    mentionedMember.getAsMention() + " has got the role but not the required rep. This has been fixed and has now got the required rep",
                    Color.RED)).queue();

        }
        event.getTextChannel().sendMessage(OnCommand.getEmbed("Update",
                "Nothing seems wrong",
                Color.green)).queue();
    }
}
