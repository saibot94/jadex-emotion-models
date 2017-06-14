package com.cristis.emotions.hunterprey.cleverprey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

/**
 *  A plan to explore the map.
 */
@Plan
public class EatPlan
{
	@PlanCapability
	private CleverPreyBDI preyBDI;

	@PlanAPI
	IPlan plan;

	/**
	 *  Plan body.
	 */
	@PlanBody
	public void body(CleverPreyBDI.EatFood goal)
	{
		Grid2D	env	= preyBDI.getEnvironment();
		ISpaceObject	myself	= preyBDI.getMyself();
		System.out.println("Gonna eat some food: " + goal.getFood());
		List<ISpaceObject> closeFoods = preyBDI.getSeenFood().parallelStream().sorted((a, b) -> {
			boolean smaller = env.getDistance(preyBDI.getPosition(), ((IVector2) a.getProperty(Space2D.PROPERTY_POSITION))).getAsInteger() <
					env.getDistance(preyBDI.getPosition(), (IVector2) b.getProperty(Space2D.PROPERTY_POSITION)).getAsInteger();
			boolean equal = env.getDistance(preyBDI.getPosition(), ((IVector2) a.getProperty(Space2D.PROPERTY_POSITION))).getAsInteger() ==
					env.getDistance(preyBDI.getPosition(), (IVector2) b.getProperty(Space2D.PROPERTY_POSITION)).getAsInteger();
			if(smaller) {
				return -1;
			} else if (equal) {
				return 0;
			} else {
				return 1;
			}
		}).collect(Collectors.toList());

		closeFoods.forEach(food -> {
			try
			{
				// Move towards food until position reached.
				while(!myself.getProperty(Space2D.PROPERTY_POSITION).equals(food.getProperty(Space2D.PROPERTY_POSITION)))
				{
					String	move	= MoveAction.getDirection(env, (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION),
							(IVector2)food.getProperty(Space2D.PROPERTY_POSITION));
					if(MoveAction.DIRECTION_NONE.equals(move))
						throw new PlanFailureException();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, preyBDI.getAgent().getComponentDescription());
					params.put(MoveAction.PARAMETER_DIRECTION, move);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
					fut.get();
					System.out.println("CleverPrey: Moved towards food");
//				System.out.println("Moved (eat): "+move+", "+getAgentName());
				}

				// Eat food.
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ISpaceAction.ACTOR_ID, preyBDI.getAgent().getComponentDescription());
				params.put(ISpaceAction.OBJECT_ID, food);
				Future<Void> fut = new Future<Void>();
				env.performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
				fut.get();
				System.out.println("Ate: " + food + "; Food present: " + preyBDI.getKnownFood().contains(food));
				preyBDI.getSeenFood().remove(food);
				preyBDI.getKnownFood().remove(food);
//			System.out.println("Eaten (eat): "+food+", "+getAgentName());
			}
			catch(Exception e)
			{
//			System.err.println("Eat plan failed: "+e);

				// Move or eat failed, forget food until seen again.
				// todo:

				if(preyBDI.getKnownFood().contains(food))
					preyBDI.getKnownFood().remove(food);
				if(preyBDI.getSeenFood().contains(food))
					preyBDI.getSeenFood().remove(food);

				System.out.println("Failed eat: " + e);
				throw new PlanFailureException();
			}
		});
	}
}
