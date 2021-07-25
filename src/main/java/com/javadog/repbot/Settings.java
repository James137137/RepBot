/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author James Anderson
 */
public class Settings {

    public static String TOKEN;
    static HashMap<String, List<String>> allowedCommandsPerChannel = new HashMap<>();
    private static FileWriter file;
    public static String fileName = "settings.json";
    public static String HardClearName3 = "Hard Clear Member";

    private static JSONObject settingsJSONObject = new JSONObject();
    public static long requiredForHardClear = 10;

    public static JSONObject load(String s) {
        Object obj = JSONValue.parse(s);
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    static void startup() {
        List<String> commands = new ArrayList<String>();
        commands.add("+rep");
        commands.add("-rep");
        commands.add("$history");
        commands.add("$time");
        commands.add("$test");
        commands.add("$update");
        allowedCommandsPerChannel.put("give-rep-here", commands);
        List<String> commands2 = new ArrayList<String>();
        commands2.add("$time");
        commands2.add("$history");
        commands2.add("$update");
        allowedCommandsPerChannel.put("bot-commands", commands2);
        List<String> commands3 = new ArrayList<String>();
        commands3.add("$history");
        commands3.add("$delete");
        commands3.add("$reset");
        commands3.add("$setrep");
        commands3.add("$update");
        allowedCommandsPerChannel.put("commands-here", commands3);
        allowedCommandsPerChannel.put("staff-chat", commands3);

        try {
            load();
        } catch (IOException ex) {
            System.out.println("obtaining default Settings");
            DefaultSettings.getDeaultSettings();
        }
        save();
    }

    static void save() {
        settingsJSONObject.put("token", TOKEN);
        settingsJSONObject.put("requiredForHardClear", requiredForHardClear);

        Path path = Paths.get(fileName);
        String someString = settingsJSONObject.toJSONString();
        byte[] bytes = someString.getBytes();

        try {
            Files.write(path, bytes);
        } catch (IOException ex) {
            // Handle exception
        }
    }

    static void load() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s = br.readLine();
        Object obj = JSONValue.parse(s);
        settingsJSONObject = (JSONObject) obj;

        TOKEN = (String) settingsJSONObject.get("token");
        requiredForHardClear = (long) settingsJSONObject.get("requiredForHardClear");
    }

    public static boolean checkIsVaildChannelForCommand(String command, String messagedChannelName) {
        Set<String> keySet = allowedCommandsPerChannel.keySet();
        for (String channelName : keySet) {
            if (messagedChannelName.contains(channelName))
            {
                List<String> commandsAllowed = allowedCommandsPerChannel.get(channelName);
                for (String commandAllowed : commandsAllowed) {
                    if (commandAllowed.equals(command))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class DefaultSettings {

        public static void getDeaultSettings() {
            requiredForHardClear = 10;
            TOKEN = "TOKENGOESHERE";
        }

    }
}
