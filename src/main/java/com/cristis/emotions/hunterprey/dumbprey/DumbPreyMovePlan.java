package com.cristis.emotions.hunterprey.dumbprey;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/12/2017.
 */
@Plan
public class DumbPreyMovePlan {
    @PlanCapability
    protected DumbPreyBDI dumbPrey;

    @PlanAPI
    protected IPlan rplan;



    @PlanReason
    protected DumbPreyBDI.Go goal;


    @PlanBody
    public void perform(){
        // Perform move action.
        IInternalAccess agent = dumbPrey.getAgent();
        Grid2D environment = dumbPrey.getEnvironment();
        try
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ISpaceAction.ACTOR_ID, agent.getComponentDescription());
            params.put(MoveAction.PARAMETER_DIRECTION, goal.getDir());
            Future<Void> fut = new Future<Void>();
            environment.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
            fut.get();

        }
        catch(RuntimeException e)
        {
            // Move failed, forget about food and turn 90 degrees.
            dumbPrey.setNearest_food(null);
            //System.out.println("Move failed: "+e);
            if(MoveAction.DIRECTION_LEFT.equals(goal.getDir()) || MoveAction.DIRECTION_RIGHT.equals(goal.getDir()))
            {
                dumbPrey.setLastDirection(Math.random() > 0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN);
            }
            else
            {
                dumbPrey.setLastDirection(Math.random() > 0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT);
            }
        }
    }
}
