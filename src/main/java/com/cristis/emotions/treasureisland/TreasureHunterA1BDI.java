package com.cristis.emotions.treasureisland;

import com.cristis.emotions.treasureisland.environment.TreasureHunterEnvironment;
import jadex.bdiv3.annotation.Belief;
import jadex.micro.annotation.Agent;

/**
 *  Basic treasure hunter agent with just the environment.
 */
@Agent
public class TreasureHunterA1BDI
{
	//-------- beliefs --------
	
	/** The treasure hunter world object. */
	@Belief
	protected TreasureHunterEnvironment env	= new TreasureHunterEnvironment();
}
