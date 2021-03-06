package com.cristis.emotions.hunterprey.dumbhunter;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.dataview.LocalDataView2D;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chris on 6/13/2017.
 */

@Plan
public class MovePlan {

    @PlanCapability
    private DumbHunterBDI hunterBDI;

    @PlanAPI
    private IPlan plan;

    @PlanBody
    public void perform() {
        ISpaceObject prey = hunterBDI.getNearestPrey();
        Set<ISpaceObject> nearObstacles = hunterBDI.getEnvironment().getNearGridObjects(hunterBDI.getPosition(), 1, new String[]{"obstacle"});
        if (prey != null) {
            String newdir = MoveAction.getDirection(hunterBDI.getEnvironment(), hunterBDI.getPosition(), (IVector2) prey.getProperty(Space2D.PROPERTY_POSITION));
            if (!MoveAction.DIRECTION_NONE.equals(newdir)) {
                hunterBDI.setLastdir(newdir);
            } else {
                // Prey unreachable.
                hunterBDI.setNearestPrey(null);
            }
        }
        // When no prey, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
        else if (hunterBDI.getLastdir() == null || Math.random() > 0.85 || computeObstacleInWay(nearObstacles)) {
            if (MoveAction.DIRECTION_LEFT.equals(hunterBDI.getLastdir()) || MoveAction.DIRECTION_RIGHT.equals(hunterBDI.getLastdir())) {
                hunterBDI.setLastdir(Math.random() > 0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN);
            } else {
                hunterBDI.setLastdir(Math.random() > 0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT);
            }
        }
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ISpaceAction.ACTOR_ID, hunterBDI.getAgent().getComponentDescription());
            params.put(MoveAction.PARAMETER_DIRECTION, hunterBDI.getLastdir());
            Future<Void> fut = new Future<Void>();
            hunterBDI.getEnvironment().performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
            fut.get();
        } catch (RuntimeException e) {
            // Move failed, forget about prey and turn 90 degrees.
            hunterBDI.setNearestPrey(null);
            if (MoveAction.DIRECTION_LEFT.equals(hunterBDI.getLastdir()) || MoveAction.DIRECTION_RIGHT.equals(hunterBDI.getLastdir())) {
                hunterBDI.setLastdir(Math.random() > 0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN);
            } else {
                hunterBDI.setLastdir(Math.random() > 0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT);
            }
        }
    }

    private boolean computeObstacleInWay(Set<ISpaceObject> nearObjects) {
        IVector2 nextPos = computeNewPositionBasedOnDirection(hunterBDI.getLastdir());
        return nearObjects.parallelStream().anyMatch(obj -> ((IVector2) obj.getProperty(Space2D.PROPERTY_POSITION)).equals(nextPos));
    }

    private IVector2 computeNewPositionBasedOnDirection(String direction) {
        IVector2 position = hunterBDI.getPosition();
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
