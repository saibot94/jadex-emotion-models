package com.cristis.emotions.hunterprey.dumbprey;

import com.cristis.emotions.hunterprey.BaseAgentBDI;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chris on 6/11/2017.
 */
@Agent
@Plans({
        @Plan(trigger = @Trigger(goals = DumbPreyBDI.Eat.class), body = @Body(DumbPreyEatPlan.class)),
        @Plan(trigger = @Trigger(goals = DumbPreyBDI.Move.class), body = @Body(DumbPreyMovePlan.class))
})
public class DumbPreyBDI extends BaseAgentBDI {

    // BELIEFS
    @Belief
    @Getter
    @Setter
    private ISpaceObject nearest_food;

    @Belief
    @Getter
    @Setter
    private String lastDirection;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @AgentBody
    public void body() {
        agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Move());
    }

    // GOALS

    @Goal(unique = true, excludemode = MProcessableElement.ExcludeMode.Never, deliberation = @Deliberation(inhibits = {Move.class}))
    public static class Eat {

        @GoalCreationCondition//(rawevents = @RawEvent(value = ChangeEvent.FACTADDED, second = "nearest_food"))
        public static boolean checkCreate(DumbPreyBDI outer, ISpaceObject garbage, ChangeEvent event) {
            ISpaceObject spaceObject = outer.getMyself();
            IVector2 pos = (IVector2) spaceObject.getProperty(Space2D.PROPERTY_POSITION);
            ISpaceObject food = outer.getNearest_food();
            IVector2 foodPos =  (IVector2)food.getProperty(Space2D.PROPERTY_POSITION);
            System.out.println("Gonna trigger the eat plan!!!");

            boolean onFood = pos.equals(foodPos);
            return outer.nearest_food != null && onFood;
        }
    }

    @Goal(excludemode = MProcessableElement.ExcludeMode.Never, orsuccess = false)
    public static class Move {
    }
}
