package com.javadog.repbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author James Anderson
 */
public class VoteHistoryDataBase {

    public static JSONObject databaseJsonFile = new JSONObject();
    //static List<Vote> voteList = new ArrayList<>();

    static List<JSONObject> voteDatabaseJson = new ArrayList<>();

    private static void save() {
        VoteHistoryDataBase.databaseJsonFile.put("VoteDatabase", voteDatabaseJson);

        Path path = Paths.get("database.json");
        String someString = databaseJsonFile.toJSONString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(someString);
        String prettyJsonString = gson.toJson(je);
        //System.out.println(prettyJsonString);
        byte[] bytes = prettyJsonString.getBytes();

        try {
            Files.write(path, bytes);
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public static void requestSave() {
        save();
    }

    public static void load() throws FileNotFoundException, IOException {
        String s = readAllBytesJava7("database.json");
        Object obj = JSONValue.parse(s);
        databaseJsonFile = (JSONObject) obj;
        if (databaseJsonFile != null) {
            voteDatabaseJson = (List<JSONObject>) VoteHistoryDataBase.databaseJsonFile.get("VoteDatabase");
        } else {
            databaseJsonFile = new JSONObject();
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

    public static void addNewVote(Vote vote) {
        voteDatabaseJson.add(vote.toJson());
        save();
    }

    public static void removeVote(Vote vote, boolean save) {
        for (int i = 0; i < voteDatabaseJson.size(); i++) {
            JSONObject get = voteDatabaseJson.get(i);
            Vote vote1 = new Vote(get);
            if (vote1.timeVoted == vote.timeVoted && vote1.receiver.equals(vote.receiver)) {
                voteDatabaseJson.remove(i);
            }

        }
        if (save) {
            save();
        }
    }

    public static List<Vote> getVoteList() {
        List<Vote> voteList = new ArrayList<>();
        for (JSONObject jVote : voteDatabaseJson) {
            Vote vote = new Vote(jVote);
            voteList.add(vote);
        }

        return voteList;
    }

    public static List<Vote> getVoteListReciver(String receiverID) {
        List<Vote> voteReciverList = new ArrayList<>();
        List<Vote> voteList = getVoteList();
        for (Vote vote : voteList) {
            if (vote.receiver.equals(receiverID)) {
                voteReciverList.add(vote);
            }
        }
        return voteReciverList;
    }

    public static List<Vote> getVoteListVoter(String VoterID) {
        List<Vote> voteReciverList = new ArrayList<>();
        List<Vote> voteList = getVoteList();
        for (Vote vote : voteList) {
            if (vote.voter.equals(VoterID)) {
                voteReciverList.add(vote);
            }
        }
        return voteReciverList;
    }

    public static boolean hasPlusAlreadyVotedForUser(String voter, String receiver) {
        List<Vote> voteList = getVoteListReciver(receiver);
        for (Vote vote : voteList) {
            if (vote.voter.equals(voter) && vote.weight > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMinusAlreadyVotedForUser(String voter, String receiver) {
        List<Vote> voteList = getVoteListReciver(receiver);
        for (Vote vote : voteList) {
            if (vote.voter.equals(voter) && vote.weight < 0) {
                return true;
            }
        }
        return false;
    }

    public static Vote getVote(long id) {
        List<Vote> voteList = getVoteList();
        for (Vote vote : voteList) {
            if (vote.timeVoted == id) {
                return vote;
            }
        }
        return null;
    }

    static void saveFirstTime() {
        save();
    }

    static void reload(Vote aThis) {
        removeVote(aThis,false);
        addNewVote(aThis);
    }
}
