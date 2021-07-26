/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Main;
import com.javadog.repbot.UserRepDataBase;
import com.javadog.repbot.Vote;
import com.javadog.repbot.VoteHistoryDataBase;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class Time {
    
    public static int cooldown = 20 * 60; //20 min cooldown

    public static MessageEmbed Time(MessageReceivedEvent event) {
        String userID = event.getMember().getId();
        return Time(userID);
        
    }
    
    public static MessageEmbed Time(String userID) {
        int seconds = getTimeLeft(userID);
        if (seconds <= 0)
        {
            return getEmbed("Time to rep", "You can give rep now",Color.GREEN);
        }
        int min = seconds / 60;
        int sec = seconds - min*60;
        String timeLeftToVote = "";
        if (min >= 1)
        {
            timeLeftToVote += min + " minute(s) and ";
        }
        timeLeftToVote += sec + " second(s)";
        Random random = new Random();
        List<String> funnyPuns = new ArrayList<>();
        funnyPuns.add("Hold your horses!");
        funnyPuns.add("All in good time!");
        funnyPuns.add("Not so fast bucko!");
        funnyPuns.add("Bear with us!");
        funnyPuns.add("Woah there big guy!");
        funnyPuns.add("Calm your farm!");
        funnyPuns.add("A watched pot never boils...");
        funnyPuns.add("Simmer down!");
        funnyPuns.add("Chillax!");
        funnyPuns.add("Get a hold of yourself!");
        funnyPuns.add("Contain yourself!");
        funnyPuns.add("Keep your shirt on!");
        funnyPuns.add("Don't get your knickers in a knot!");
        funnyPuns.add("Don't get your feathers ruffled!");
        
        
        
        
        
        
        
        
        return getEmbed("Time to rep", funnyPuns.get(random.nextInt(funnyPuns.size())) + " You can give rep in: " + timeLeftToVote, Color.RED);
    }

    private static long getLastVoteTime(String userID) {
        List<Vote> voteList = VoteHistoryDataBase.getVoteListVoter(userID);
        long result = 0;
        for (Vote vote : voteList) {
            if (vote.timeVoted > result) result = vote.timeVoted;
        }
        return result;
    }

    public static int getTimeLeft(String userID) {
        long timeLastVoted = getLastVoteTime(userID);
        long now = System.currentTimeMillis();
        long timeDiff = now - timeLastVoted;
        int seconds = (int) (timeDiff / 1000);
        seconds = cooldown - seconds;
        return seconds;
    }
    
    public static boolean canVoteNow(String userID)
    {
        if (Main.debug) return true;
        return getTimeLeft(userID) <= 0;
    }
    
    
    public static MessageEmbed getEmbed(String title, String text, Color colour) {
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        /*
    Set the title:
    1. Arg: title as string
    2. Arg: URL as string or could also be null
         */
        eb.setTitle(title, null);

        /*
    Set the color
         */
        eb.setColor(colour);

        /*
    Set the text of the Embed:
    Arg: text as string
         */
        eb.setDescription(text);

        //eb.setAuthor("RepBot", null, null);
        return eb.build();
    }
}
