package com.cristis.emotions.hunterprey.dumbprey;

import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/11/2017.
 */
@Plan
public class DumbPreyEatPlan {


    @PlanCapability
    protected DumbPreyBDI dumbPrey;

    @PlanCapability
    protected DumbPreyBDI collector;

    @PlanAPI
    protected IPlan rplan;

    @PlanReason
    protected DumbPreyBDI.Eat goal;

    @PlanBody
    public void perform() {
        // Perform eat action.
        System.out.println("Baaah, I'm eating");
        IInternalAccess agent = dumbPrey.getAgent();
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ISpaceAction.ACTOR_ID, agent.getComponentDescription());
            params.put(ISpaceAction.OBJECT_ID, dumbPrey.getNearest_food());
            Future<Void> fut = new Future<Void>();
            dumbPrey.getEnvironment().performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
            fut.get();
        } catch (RuntimeException e) {
            System.out.println("Eat failed: " + e);
        }
    }
}
