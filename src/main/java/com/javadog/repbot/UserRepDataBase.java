package com.javadog.repbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author James Anderson
 */
public class UserRepDataBase {

    public static JSONObject UserDataBase = new JSONObject();

    public static void save() {

        Path path = Paths.get("user.json");
        String someString = UserDataBase.toJSONString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(someString);
        String prettyJsonString = gson.toJson(je);
        //System.out.println(prettyJsonString);
        byte[] bytes = prettyJsonString.getBytes();

        try {
            Files.write(path, bytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void load() throws Exception {
        String s = readAllBytesJava7("user.json");
        Object obj = JSONValue.parse(s);
        UserDataBase = (JSONObject) obj;
        if (UserDataBase == null)
        {
            UserDataBase = new JSONObject();
        }

    }

    private static String readAllBytesJava7(String filePath) {
        String content = "";

        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static long getRepNumber(String userID) {
        if (UserDataBase.get(userID) != null) {
        return (long) UserDataBase.get(userID);
        }
        //System.out.println(userID + "   GetRepNumber");
        UserDataBase.put(userID, 0L);
        save();
        return (long) UserDataBase.get(userID);
    }
    
    public void removeRankFromID(String userIaD) {
        
    }
    
    public static void setRepNumber(String userID, long amount)
    {
        UserDataBase.put(userID, amount);
        save();
    }
    
    public static int getMaxRep()
    {
        return 20;
    }
}
