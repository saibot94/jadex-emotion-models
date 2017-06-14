package com.cristis.emotions.hunterprey.cleverprey;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A plan to explore the map.
 */
@Plan
public class WanderPlan {

    @PlanCapability
    protected CleverPreyBDI prey;

    @PlanAPI
    protected IPlan rplan;

    boolean failed = false;

    /**
     * Plan body.
     */
    @PlanBody
    public void body() {
        Grid2D env = prey.getEnvironment();
        failed = false;
        // Turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
        Set<ISpaceObject> nearObstacles = prey.getEnvironment().getNearGridObjects(prey.getPosition(), 1, new String[]{"obstacle"});
        if (prey.getLastDirection() == null || computeObstacleInWay(nearObstacles) || failed || Math.random() > 0.8) {
            if (MoveAction.DIRECTION_LEFT.equals(prey.getLastDirection()) || MoveAction.DIRECTION_RIGHT.equals(prey.getLastDirection())) {
                prey.setLastDirection(Math.random() > 0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN);
            } else {
                prey.setLastDirection(Math.random() > 0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT);
            }
        }
        // Perform move action.
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ISpaceAction.ACTOR_ID, prey.getAgent().getComponentDescription());
            params.put(MoveAction.PARAMETER_DIRECTION, prey.getLastDirection());

            Future<Void> fut = new Future<Void>();
            env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
            fut.get();
//				System.out.println("Moved (wander): "+lastdir+", "+getAgentName());
            failed = false;
        } catch (RuntimeException e) {
//				System.err.println("Wander plan failed: "+e);
            // Move failed, turn 90 degrees on next move.
            failed = true;
        }
    }

    private boolean computeObstacleInWay(Set<ISpaceObject> nearObjects) {
        IVector2 nextPos = computeNewPositionBasedOnDirection(prey.getLastDirection());
        return nearObjects.parallelStream().anyMatch(obj -> ((IVector2) obj.getProperty(Space2D.PROPERTY_POSITION)).equals(nextPos));
    }

    private IVector2 computeNewPositionBasedOnDirection(String direction) {
        IVector2 position = prey.getPosition();
        if (MoveAction.DIRECTION_LEFT.equals(direction)) {
            return new Vector2Int( position.getXAsInteger() - 1,position.getYAsInteger());
        } else if (MoveAction.DIRECTION_RIGHT.equals(direction)) {
            return new Vector2Int( position.getXAsInteger() + 1,position.getYAsInteger());
        } else if (MoveAction.DIRECTION_UP.equals(direction)) {
            return new Vector2Int( position.getXAsInteger(),position.getYAsInteger() - 1);
        } else if (MoveAction.DIRECTION_DOWN.equals(direction)) {
            return new Vector2Int(position.getXAsInteger(), position.getYAsInteger() + 1);
        }  else {
            return position;
        }
    }

}
