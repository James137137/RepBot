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
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class History {

    public static MessageEmbed History(MessageReceivedEvent event, boolean full) {
        Role role = event.getGuild().getRolesByName(Settings.HardClearName, false).get(0);
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        Member mentionedMember = null;
        String receiverID;
        if (mentionedMembers.isEmpty()) {
            String[] split = event.getMessage().getContentRaw().split(" ");
            if (split.length >= 2) {
                receiverID = split[1];
                boolean vaild = User.CheckIfVaildID(event, receiverID);
                if (!vaild) {
                    return OnCommand.getEmbed("History", "Sorry the number doesn't seem to be vaild. Please double check this",Color.GREEN);
                }
            } else {
               mentionedMember = event.getMember();
               receiverID = event.getMember().getId();
            }
        } else
        {
            mentionedMember = mentionedMembers.get(0);
            receiverID = mentionedMember.getId();
        }
        //User.checkHC(receiverID, event, UserRepDataBase.getRepNumber(receiverID));
        
        long repNumber = UserRepDataBase.getRepNumber(receiverID);
        //String output = Main.jda.retrieveUserById(receiverID).complete().getName() + " currently has " + repNumber + " rep points \n\n";
        String output = "";
        List<Vote> voteList = VoteHistoryDataBase.getVoteListReciver(receiverID);
        if (voteList.isEmpty())
        {
            return OnCommand.getEmbed("History", "No history available for this user",Color.white);
        }
        
        int j = 0;
        if (voteList.size() <= 5) full = true;
        if (!full) j= voteList.size() - 5;
        if (j < 0) j = 0;
        
        
        for (int i = voteList.size() -1 ; i >= j; i--) {
            Vote vote = voteList.get(i);
            if (vote.weight > 0)
            {
                output += "+";
            }
            //output += vote.weight + " " + vote.voterName + " Reason: " + vote.reason + "     #" + vote.timeVoted + "\n";
            output += vote.weight + " " + vote.voterName + " Reason: " + vote.reason + "\n";
            
        }
        MessageEmbed embedHistory = getEmbedHistory(event, receiverID, repNumber, User.isHardClear(receiverID), output, full);
        
        
        
        return embedHistory;
    }
    
    
    public static MessageEmbed getEmbedHistory(MessageReceivedEvent event, String userID, long repNumber, boolean isHardClear, String text, boolean fullHistory) {
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();
        String repNumberS = "" +repNumber;
        if (repNumber >= 1)
        {
            repNumberS = "+" + repNumber;
        }
        String hardclear = "Not yet";
        if (isHardClear)
        {
            hardclear = "Unlocked :)";
        }

        /*
    Set the title:
    1. Arg: title as string
    2. Arg: URL as string or could also be null
         */
        String title = Main.jda.retrieveUserById(userID).complete().getName();
        String url = Main.jda.retrieveUserById(userID).complete().getAvatarUrl();
        if (title.toLowerCase().charAt(title.length()-1) == 's')
        {
            title += "' Reputation Summary";
        }
        else
        {
            title += "'s Reputation Summary";
        }
        
        eb.setTitle(title , null);

        /*
    Set the color
         */
        eb.setColor(Color.green);

        /*
    Set the text of the Embed:
    Arg: text as string
         */
        //eb.setDescription("Text");

        /*
    Add fields to embed:
    1. Arg: title as string
    2. Arg: text as string
    3. Arg: inline mode true / false
         */
        eb.addField(":star2: Repuation", repNumberS, true);
        eb.addField(":trophy: Hard Clear", hardclear, true);
        eb.addField(":scales: Weight", "" + User.getWeight(null, userID), true);
        if (!fullHistory)
        {
            text += "\n\nFor full history please type $history full";
        }
        eb.addField("Repuation History :page_facing_up:", text, false);

        /*
    Add spacer like field
    Arg: inline mode true / false
         */
        eb.addBlankField(false);

        /*
    Add embed author:
    1. Arg: name as string
    2. Arg: url as string (can be null)
    3. Arg: icon url as string (can be null)
         */
        
        
        
        //eb.setAuthor(, null, event.getAuthor().getEffectiveAvatarUrl());

        /*
    Set footer:
    1. Arg: text as string
    2. icon url as string (can be null)
         */
        String footer = "Request by: " + event.getAuthor().getName();
        eb.setFooter(footer, event.getAuthor().getEffectiveAvatarUrl());

        /*
    Set image:
    Arg: image url as string
         */
        //eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");

        /*
    Set thumbnail image:
    Arg: image url as string
         */
        eb.setThumbnail(url);

        return eb.build();
    }
}
