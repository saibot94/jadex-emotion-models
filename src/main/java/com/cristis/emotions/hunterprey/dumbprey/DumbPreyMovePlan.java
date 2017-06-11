package com.cristis.emotions.hunterprey.dumbprey;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
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
public class DumbPreyMovePlan {

    @PlanCapability
    protected DumbPreyBDI dumbPrey;

    @PlanAPI
    protected IPlan rplan;

    @PlanBody
    public void perform() {
        ISpaceObject food = dumbPrey.getNearest_food();
        Grid2D env = dumbPrey.getEnvironment();
        IVector2 pos = dumbPrey.getPosition();
        String	lastdir	= dumbPrey.getLastDirection();

        System.out.println("Food: " + food);
        IInternalAccess agent = dumbPrey.getAgent();
        // Move towards the food, if any
        if(food!=null)
        {
            String	newdir	= MoveAction.getDirection(env, pos, (IVector2)food.getProperty(Space2D.PROPERTY_POSITION));
            if(!MoveAction.DIRECTION_NONE.equals(newdir))
            {
                lastdir	= newdir;
            }
            else
            {
                // Food unreachable.
                dumbPrey.setNearest_food(null);
            }
        }

        // When no food, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
        else if(lastdir==null || Math.random()>0.75)
        {
            if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
            {
                lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
            }
            else
            {
                lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
            }
        }

        dumbPrey.setLastDirection(lastdir);
        // Perform move action.
        try
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ISpaceAction.ACTOR_ID, agent.getComponentDescription());
            params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
            Future<Void> fut = new Future<Void>();
            env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
            fut.get();
        }
        catch(RuntimeException e)
        {
            // Move failed, forget about food and turn 90 degrees.
            dumbPrey.setNearest_food(null);
			System.out.println("Move failed: "+e);
            if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
            {
                lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
            }
            else
            {
                lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
            }
        }

    }
}
