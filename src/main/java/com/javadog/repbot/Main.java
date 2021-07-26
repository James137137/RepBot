package com.javadog.repbot;

import event.OnChat;
import event.listener;
import java.io.IOException;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;



/**
 *
 * @author James Anderson
 */
public class Main {
    
    
    public static boolean debug = false;
    
    public static JDA jda;
    public static void main(String[] args) throws LoginException, InterruptedException {
        System.out.println("DEBUG MODE IS ENABLE....PANIC!!!!!!!!!!");
        Scanner myScanner = new Scanner(System.in);
        System.out.println("type jack is 2nd kiwi to start");
        String nextLine = myScanner.nextLine();
        if (!nextLine.equalsIgnoreCase("jack is 2nd kiwi"))
        {
            System.exit(0);
        }
        
        Settings.startup();
        try {
            VoteHistoryDataBase.load();
        } catch (IOException ex) {
            VoteHistoryDataBase.saveFirstTime();
        }
        
        boolean loaded;
        try {
            UserRepDataBase.load();
        } catch (Exception ex) {
            UserRepDataBase.save();
        }
        
        jda = JDABuilder.createDefault(Settings.TOKEN)
            .addEventListeners(new listener())
            .build();

        // optionally block until JDA is ready
        jda.awaitReady();
        jda.addEventListener(new OnChat());
        
        
        
        
        
        
    }

    
}
