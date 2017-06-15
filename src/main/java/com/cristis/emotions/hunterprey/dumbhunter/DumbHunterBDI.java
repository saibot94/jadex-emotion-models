package com.cristis.emotions.hunterprey.dumbhunter;

import com.cristis.emotions.hunterprey.BaseAgentBDI;
import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/12/2017.
 */
@Agent
@Plans({
    @Plan(trigger = @Trigger(goals = DumbHunterBDI.Check.class), body = @Body(CheckPlan.class)),
    @Plan(trigger = @Trigger(goals = DumbHunterBDI.Move.class), body = @Body(MovePlan.class)),
    @Plan(trigger = @Trigger(goals = DumbHunterBDI.Eat.class), body = @Body(EatPlan.class))
})
public class DumbHunterBDI extends BaseAgentBDI {

    @Belief
    @Getter
    @Setter
    private String lastdir;

    @Belief
    @Getter
    @Setter
    private ISpaceObject nearestPrey;


    @AgentBody
    public void body() {
        System.out.println("Initing hunter...");
        agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new DumbHunterBDI.Check());
    }

    @Goal(excludemode = MProcessableElement.ExcludeMode.Never, orsuccess = false)
    public class Check {
    }

    @Goal(unique = true)
    public class Eat {
            public Eat(){
                System.out.println("HUNTER: Eat goal created");
            }
    }

    @Goal
    public class Move {
    }

}

