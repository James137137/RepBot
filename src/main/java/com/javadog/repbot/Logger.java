/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 *
 * @author James Anderson
 */


public class Logger {
    
    
    public static void log(Guild guild, String message)
    {
        List<TextChannel> textChannelsByName = guild.getTextChannelsByName("reputation-logs", true);
        for (TextChannel textChannel : textChannelsByName) {
            textChannel.sendMessage(message).queue();
        }
    }
    
    public static void log(Guild guild, MessageEmbed message)
    {
        List<TextChannel> textChannelsByName = guild.getTextChannelsByName("reputation-logs", true);
        for (TextChannel textChannel : textChannelsByName) {
            textChannel.sendMessage(message).queue();
        }
    }
}
