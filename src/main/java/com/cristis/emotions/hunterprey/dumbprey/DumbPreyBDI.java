package com.cristis.emotions.hunterprey.dumbprey;

import com.cristis.emotions.hunterprey.BaseAgentBDI;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
        @Plan(trigger = @Trigger(goals = DumbPreyBDI.TriggerFood.class), body = @Body(DumbPreyEatPlan.class)),
        @Plan(trigger = @Trigger(goals = DumbPreyBDI.Check.class), body = @Body(DumbPreyCheckPlan.class)),
        @Plan(trigger = @Trigger(goals = DumbPreyBDI.Go.class), body = @Body(DumbPreyMovePlan.class))
})
public class DumbPreyBDI extends BaseAgentBDI {

    // BELIEFS
    @Getter
    @Setter
    @Belief
    private ISpaceObject nearest_food;

    @Belief
    @Getter
    @Setter
    private boolean onFood;

    @Getter
    @Setter
    @Belief
    private String lastDirection;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @AgentBody
    public void body() {
        agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new Check());
    }

    // GOALS

    @Goal
    public class Go
    {
        /** The position. */
        @Getter
        protected String dir;

        /**
         *  Create a new Go.
         */
        public Go(String direction)
        {
            this.dir = direction;
        }

    }

    @Goal(excludemode= MProcessableElement.ExcludeMode.Never, orsuccess = false)
    public class Check
    {
    }


    @Goal(unique = true)
    public class Eat
    {

        /**
         *  Get the hashcode.
         */
        public int hashCode()
        {
            return 31;
        }

        /**
         *  Test if equal to other object.
         */
        public boolean equals(Object obj)
        {
            return obj instanceof Eat;
        }

        @GoalFinished
        public void finished() {
            System.out.println("Finished EAT");
        }
    }

    @Goal(deliberation = @Deliberation(inhibits ={Check.class, Go.class}))
    public class TriggerFood{
        @GoalDropCondition(beliefs = "onFood")
        public boolean checkDrop() {
            if(getNearest_food() == null) {
                return true;
            }
            IVector2 foodPos = (IVector2)getNearest_food().getProperty(Space2D.PROPERTY_POSITION);
            IVector2 pos = getPosition();
            System.out.println("Trying to invalidate goal");
            return !onFood || (!pos.equals(foodPos));
        }

        @GoalCreationCondition(beliefs = "onFood")
        public TriggerFood(boolean onFood) {

        }
    }
}
