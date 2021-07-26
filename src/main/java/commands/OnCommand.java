/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import com.javadog.repbot.Main;
import com.javadog.repbot.Settings;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author James Anderson
 */
public class OnCommand {

    public OnCommand(MessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw().toLowerCase();
        if (contentRaw.contains("\n")) {
            return;
        }
        String command = contentRaw.split(" ")[0];
        if (command.startsWith("$") || command.startsWith("-") || command.startsWith("+")) {
            boolean isVaild = Settings.checkIsVaildChannelForCommand(command, event.getTextChannel().getName());
            if (!isVaild) {
                return;
            }
        }

        String title = "";
        String output = null;
        MessageChannel channel = event.getChannel();
        if (contentRaw.startsWith("+rep")) {
            title = "Rep update";
            output = commands.PlusRep.PlusRep(event);
        } else if (contentRaw.startsWith("-rep")) {
            title = "Rep update";
            output = commands.MinusRep.MinusRep(event);
        } else if (contentRaw.startsWith("$history")) {
            title = "History";
            if (contentRaw.toLowerCase().contains("full")) {

                channel.sendMessage(commands.History.History(event, true)).queue();
            } else {
                channel.sendMessage(commands.History.History(event, false)).queue();
            }

        } else if (contentRaw.startsWith("$delete")) {
            title = "Delete rep";
            event.getChannel().sendMessage(getEmbed(title, commands.Delete.Delete(event), Color.white)).queue();
            //output = commands.Delete.Delete(event);
        } else if (contentRaw.startsWith("$setrep")) {
            title = "Set rep";
            event.getChannel().sendMessage(getEmbed(title, commands.SetRep.SetRep(event), Color.white)).queue();
        } else if (contentRaw.startsWith("$time")) {
            event.getChannel().sendMessage(commands.Time.Time(event)).queue();
        } else if (contentRaw.startsWith("$reset")) {
            title = "Reset";
            event.getChannel().sendMessage(getEmbed(title, commands.reset.Reset(event), Color.white)).queue();
        } else if (contentRaw.startsWith("$test")) {
            test(event);
        } else if (contentRaw.startsWith("$update")) {
            title = "Update";
            commands.Update.Update(event);
            return;
        }

        if (output != null) {
            if (title.length() > 0) {
                channel.sendMessage(getEmbed(title, output, Color.white)).queue();
            } else {
                channel.sendMessage(output).queue();
            }

        }

    }

    public static MessageEmbed getEmbed(String title, String text, Color color) {
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
        eb.setColor(color);
        //eb.setColor(new Color(0xF40C0C));
        //eb.setColor(new Color(255, 0, 54));

        /*
    Set the text of the Embed:
    Arg: text as string
         */
        eb.setDescription(text);

        //eb.setAuthor("RepBot", null, null);
        return eb.build();
    }

    public static MessageEmbed getEmbed(MessageReceivedEvent event) {
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        /*
    Set the title:
    1. Arg: title as string
    2. Arg: URL as string or could also be null
         */
        String userID = event.getMember().getId();
        String title = Main.jda.retrieveUserById(userID).complete().getName();
        if (title.toLowerCase().charAt(title.length() - 1) == 's') {
            title += "' Reputation Summary";
        } else {
            title += "'s Reputation Summary";
        }

        eb.setTitle(title, null);

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
        eb.addField(":star2: Repuation", "+21", true);
        eb.addField(":trophy: Hard Clear", "Unlocked :)", true);
        eb.addField(":scales: Weight", "2", true);
        eb.addField("Repuation History :page_facing_up:", "blah blah blah \n blah blah blah", false);

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
        eb.setFooter("Request by: " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl());

        /*
    Set image:
    Arg: image url as string
         */
        //eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");

        /*
    Set thumbnail image:
    Arg: image url as string
         */
        eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());

        return eb.build();
    }

    private void test(MessageReceivedEvent event) {

    }

}
