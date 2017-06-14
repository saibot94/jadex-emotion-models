package com.cristis.emotions.hunterprey.cleverprey;

import com.cristis.emotions.hunterprey.MoveAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Try to run away from a hunter.
 */
@Plan
public class EscapePlan
{
	//-------- methods --------

	@PlanCapability
	private CleverPreyBDI preyBDI;

	@PlanAPI
	IPlan plan;

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Grid2D	env	= preyBDI.getEnvironment();
		ISpaceObject	myself	= preyBDI.getMyself();
		List<ISpaceObject> hunters	= preyBDI.getSeenHunters();

		String	move	= MoveAction.getAvoidanceDirection(env,
			(IVector2)myself.getProperty(Space2D.PROPERTY_POSITION), hunters.toArray(new ISpaceObject[0]));
		System.out.println(myself.getId() + " trying to escape in direction: " + move);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, preyBDI.getAgent().getComponentDescription());
		params.put(MoveAction.PARAMETER_DIRECTION, move);
		Future<Void> ret = new Future<Void>();
		env.performSpaceAction("move", params, new DelegationResultListener<Void>(ret));
		try
		{
			ret.get();
		}
		catch(RuntimeException e)
		{
			// When move fails ignore exception.
			throw new PlanFailureException();
		}
	}
}
