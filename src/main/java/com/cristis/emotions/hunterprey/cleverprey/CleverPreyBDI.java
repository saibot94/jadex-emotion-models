package com.cristis.emotions.hunterprey.cleverprey;

import com.cristis.emotions.hunterprey.BaseAgentBDI;
import com.cristis.emotions.hunterprey.dumbprey.DumbPreyBDI;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chris on 6/14/2017.
 */
@Plans({
        @Plan(trigger = @Trigger(goals = CleverPreyBDI.FoodSeen.class), body = @Body(WanderPlan.class)),
        @Plan(trigger = @Trigger(goals = CleverPreyBDI.EatFood.class), body = @Body(EatPlan.class)),
        @Plan(trigger = @Trigger(goals = CleverPreyBDI.KeepAlone.class), body = @Body(EscapePlan.class))
})
@Agent
public class CleverPreyBDI extends BaseAgentBDI {

    // BELIEFS

    @Belief(dynamic = true)
    @Getter
    @Setter
    private List<ISpaceObject> knownFood;

    @Belief(dynamic = true)
    @Getter
    @Setter
    private List<ISpaceObject> seenHunters;

    @Belief(dynamic = true)
    @Getter
    @Setter
    private List<ISpaceObject> seenFood = new ArrayList<>();

    @Getter
    @Setter
    @Belief
    private String lastDirection;

    @Getter
    @Setter
    @Belief(dynamic = true)
    private boolean hasFood = !seenFood.isEmpty();

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @AgentCreated
    public void init() {
        knownFood = new ArrayList<>();
        seenHunters = new ArrayList<>();
        seenFood = new ArrayList<>();
    }

    @AgentBody
    public void body() {
        agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new FoodSeen());
        agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new KeepAlone());
    }


    // GOALS
    @Goal(orsuccess = false, excludemode = MProcessableElement.ExcludeMode.Never)
    public class FoodSeen{
        @GoalMaintainCondition
        public boolean maintain() {
            System.out.println("Maintain goal: " + (seenFood.size() > 0));
            return seenFood.size() > 0;
        }
    }

    @Goal(excludemode = MProcessableElement.ExcludeMode.Never, deliberation = @Deliberation(inhibits = {FoodSeen.class}))
    public class EatFood {
        @Getter
        @Setter
        private ISpaceObject food;

        @GoalCreationCondition(beliefs = "seenFood")
        public EatFood(ISpaceObject food) {
            this.food= food;
            System.out.println("Close foods: " + seenFood);
        }

        @GoalDropCondition
        public boolean dropCondition() {
            return !seenFood.contains(food);
        }
    }


    @Goal(deliberation = @Deliberation(inhibits = {FoodSeen.class, EatFood.class}), excludemode = MProcessableElement.ExcludeMode.Never)
    public class KeepAlone {
        @GoalMaintainCondition
        public boolean maintain() {
            return seenHunters.size() == 0;
        }

    }

}
