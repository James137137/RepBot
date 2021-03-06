/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import event.listener;
import java.util.List;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 *
 * @author James Anderson
 */
public class test {
    public static void main(String[] args) throws LoginException, InterruptedException {
        String tok = "";
        
        JDA jda = JDABuilder.createDefault(tok)
            .addEventListeners(new listener())
            .build();

        // optionally block until JDA is ready
        jda.awaitReady();
        
        List<Guild> guilds = jda.getGuilds();
        for (Guild guild : guilds) {
            //System.out.println(guild.getName());
            if (guild.getName().equals("Proximity Gaming"))
            {
                List<TextChannel> textChannelsByName = guild.getTextChannelsByName("give-rep-here", true);
                textChannelsByName.get(0).sendMessage("I love you <@827665527786569758>!").queue();
            }
        }
        
    }
}
