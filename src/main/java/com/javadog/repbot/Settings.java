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
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author James Anderson
 */
public class Settings {

    public static String TOKEN;
    public static List<String> allowChannels;
    private static FileWriter file;
    public static String fileName = "settings.json";
    public static String HardClearName = "Hard Clear Member";
    
    
    private static JSONObject settingsJSONObject = new JSONObject();
    public static long requiredForHardClear = 10;
    
    

    public static JSONObject load(String s) {
        Object obj = JSONValue.parse(s);
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    static void startup() {
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
        settingsJSONObject.put("channel", allowChannels);
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
        allowChannels = (List<String>) settingsJSONObject.get("channel");
    }

    
    private static class DefaultSettings
    {

        public static void getDeaultSettings() {
        allowChannels = new ArrayList<>();
        allowChannels.add("general");
        allowChannels.add("Give-Rep-Here");
        requiredForHardClear = 10;
        TOKEN = "TOKENGOESHERE";
        }
        
    }
}
