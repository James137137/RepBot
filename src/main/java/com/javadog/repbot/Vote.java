/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javadog.repbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import org.json.simple.JSONObject;

/**
 *
 * @author James Anderson
 */
public class Vote {
    public String voter;
    public String voterName;
    public String receiver;
    public String receiverName;
    public boolean isHardClear;
    public long weight;
    public String reason;
    public long timeVoted;    

    public Vote(Member voterm, Member receiver, boolean isHardClear, long weight, String reason, boolean admin) {
        this.voter = voterm.getId();
        this.voterName = voterm.getUser().getName();
        if (admin)
        {
            this.voter = "0";
            this.voterName = "Admin:" + voterm.getEffectiveName();
        }
        this.receiver = receiver.getId();
        this.receiverName = receiver.getEffectiveName();
        this.isHardClear = isHardClear;
        this.weight = weight;
        this.reason = reason;
        this.timeVoted = System.currentTimeMillis();
        logIt(voterm);
    }
    
    public Vote(Member voterm, String receiverID, boolean isHardClear, long weight, String reason, boolean admin) {
        
        this.voter = voterm.getId();
        this.voterName = voterm.getUser().getName();
        if (admin)
        {
            this.voter = "0";
            this.voterName = "Admin:" + this.voterName;
        }
        this.receiverName = Main.jda.retrieveUserById(receiverID).complete().getName();
        this.receiver = receiverID;
        this.isHardClear = isHardClear;
        this.weight = weight;
        this.reason = reason;
        this.timeVoted = System.currentTimeMillis();
        logIt(voterm);
               
    }

    public Vote(JSONObject jSONObject) {
        this.voter = (String) jSONObject.get("voter");
        this.voterName = (String) jSONObject.get("voterName");
        this.receiver = (String) jSONObject.get("receiver");
        this.receiverName = (String) jSONObject.get("receiverName");
        this.isHardClear = (boolean) jSONObject.get("isHardClear");
        this.weight = (long) jSONObject.get("weight");
        this.reason = (String) jSONObject.get("reason");
        this.timeVoted = (long) jSONObject.get("timeVoted");
    }
    
    /*
    public void updateRecieverName()
    {
        RestAction<net.dv8tion.jda.api.entities.User> retrieveUserById = Main.jda.retrieveUserById(this.receiver);
        retrieveUserById.map(net.dv8tion.jda.api.entities.User::getName).queue((String name) -> {
            this.receiverName = name;
            VoteHistoryDataBase.reload(this);
        });
        
    } */
    
    
    
    
    
    
    
    
    public JSONObject toJson()
    {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("voter", voter);
        jSONObject.put("voterName", voterName);
        jSONObject.put("receiver", receiver);
        jSONObject.put("receiverName", receiverName);
        jSONObject.put("isHardClear", isHardClear);
        jSONObject.put("weight", weight);
        jSONObject.put("reason", reason);
        jSONObject.put("timeVoted", timeVoted);
        return jSONObject;
    }

    private void logIt(Member voterm) {
        String weightS = "" + this.weight;
        if (this.weight > 0 ) weightS = "+" + this.weight;
        
        
        EmbedBuilder eb = new EmbedBuilder();
        MessageEmbed build = eb.setDescription("<@" +voterm.getId()+ "> gave " + weightS + " to <@" + this.receiver +">")
                .addField(weightS + " [" + this.receiver + "] " + this.receiverName + " \"" + this.reason + "\"", 
                        "Remove this with $delete " + this.timeVoted, true).build();
        Logger.log(voterm.getGuild(),build);  
    }
}
