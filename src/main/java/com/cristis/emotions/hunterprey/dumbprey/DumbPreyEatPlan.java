package com.cristis.emotions.hunterprey.dumbprey;

import jadex.bdiv3.annotation.*;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;

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
    protected DumbPreyBDI.TriggerFood goal;

    @PlanBody
    public void perform() {
        // Perform eat action.
        IInternalAccess agent = dumbPrey.getAgent();
        IComponentDescription componentDescription = agent.getComponentDescription();
        ISpaceObject food = dumbPrey.getNearest_food();
        System.out.println("Agent: " + agent);
        System.out.println("Comp desc: " + componentDescription);
        System.out.println("Food: " + food);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ISpaceAction.ACTOR_ID, componentDescription);
        params.put(ISpaceAction.OBJECT_ID, food);
        Future<Void> fut = new Future<Void>();
        dumbPrey.getEnvironment().performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
        fut.get();
        dumbPrey.setNearest_food(null);
    }
}
