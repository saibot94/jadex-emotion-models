package com.cristis.emotions.hunterprey.dumbprey;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/11/2017.
 */
@Plan
public class DumbPreyCheckPlan {

    @PlanCapability
    protected DumbPreyBDI dumbPrey;

    @PlanAPI
    protected IPlan rplan;

    @PlanBody
    public void perform() {
        Grid2D env = dumbPrey.getEnvironment();
        IVector2 pos = dumbPrey.getPosition();
        String lastdir = dumbPrey.getLastDirection();

        ISpaceObject food = dumbPrey.getNearest_food();
        IInternalAccess agent = dumbPrey.getAgent();
        // When no food, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
        if (food != null) {
            String newdir = MoveAction.getDirection(env, pos, (IVector2) food.getProperty(Space2D.PROPERTY_POSITION));
            if (!MoveAction.DIRECTION_NONE.equals(newdir)) {
                lastdir = newdir;
            } else {
                // Food unreachable.
                dumbPrey.setNearest_food(null);
            }
        } else if (lastdir == null || Math.random() > 0.75) {
            if (MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir)) {
                lastdir = Math.random() > 0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
            } else {
                lastdir = Math.random() > 0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
            }
        }
        dumbPrey.setLastDirection(lastdir);

        rplan.dispatchSubgoal(dumbPrey.new Go(lastdir)).get();
        if (dumbPrey.getNearest_food() != null) {
            System.out.println("There's some food close by or on me");
            IVector2 foodPos = (IVector2) dumbPrey.getNearest_food().getProperty(Space2D.PROPERTY_POSITION);
            IVector2 myPos = dumbPrey.getPosition();
            dumbPrey.setOnFood(myPos.equals(foodPos));
            if (dumbPrey.isOnFood()) {
                System.out.println("I'm on the food!");
                rplan.abort().get();
            }
        }
    }
}
