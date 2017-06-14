package com.cristis.emotions.hunterprey.dumbhunter;

import com.cristis.emotions.hunterprey.dumbprey.DumbPreyBDI;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;

/**
 * Created by chris on 6/13/2017.
 */

@Plan
public class CheckPlan {

    @PlanCapability
    private DumbHunterBDI hunterBDI;

    @PlanAPI
    private IPlan plan;

    @PlanBody
    public void perform(){
        ISpaceObject myself = hunterBDI.getMyself();
        Grid2D grid2D = hunterBDI.getEnvironment();
        ISpaceObject food = hunterBDI.getNearestPrey();
        if (food != null && hunterBDI.getPosition().equals(food.getProperty(Space2D.PROPERTY_POSITION))) {
            plan.dispatchSubgoal(hunterBDI.new Eat()).get();
        } else {
            plan.dispatchSubgoal(hunterBDI.new Move()).get();
        }
    }
}
