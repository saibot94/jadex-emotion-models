package com.cristis.emotions.hunterprey.dumbhunter;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 6/13/2017.
 */

@Plan
public class EatPlan {

    @PlanCapability
    private DumbHunterBDI hunterBDI;

    @PlanAPI
    private IPlan plan;

    @PlanBody
    public void perform() {
        System.out.println("HUNTER: Performing eat action!");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ISpaceAction.ACTOR_ID, hunterBDI.getAgent().getComponentDescription());
        params.put(ISpaceAction.OBJECT_ID, hunterBDI.getNearestPrey());
        Future<Void> fut = new Future<Void>();
        hunterBDI.getEnvironment().performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
        fut.get();
    }
}


