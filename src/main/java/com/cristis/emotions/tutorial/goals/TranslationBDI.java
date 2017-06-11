package com.cristis.emotions.tutorial.goals;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/11/2017.
 */
@Agent
public class TranslationBDI {
    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @Belief
    protected Map<String, String> wordtable;


    @AgentCreated
    public void init()
    {
        this.wordtable = new HashMap<String, String>();
        wordtable.put("coffee", "Kaffee");
        wordtable.put("milk", "Milch");
        wordtable.put("cow", "Kuh");
        wordtable.put("cat", "Katze");
        wordtable.put("dog", "Hund");
    }

    @AgentBody
    public void body()
    {
        String eword = "cat";
        String gword = (String) bdiFeature.dispatchTopLevelGoal(new TranslateGoal(eword, "stuff from outside")).get();
        System.out.println("Translated: "+eword+" "+ gword);
    }

    @Plan(trigger = @Trigger(goals = TranslateGoal.class))
    protected String translate(String eword, String someOtherStuff) {
        System.out.println("Got this stuff also: " + someOtherStuff);
        return wordtable.get(eword);
    }
}
