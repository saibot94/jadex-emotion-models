package com.cristis.emotions.tutorial.goals;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chris on 6/11/2017.
 */
@Goal
public class TranslateGoal {

    public TranslateGoal(String eword, String stuff)
    {
        this.eword = eword;
        this.someOtherStuff = stuff;
    }

    @Getter
    @Setter
    @GoalParameter
    protected String eword;

    @Getter
    @Setter
    @GoalParameter
    protected String someOtherStuff;

    @Getter
    @Setter
    @GoalResult
    protected String gword;

}
