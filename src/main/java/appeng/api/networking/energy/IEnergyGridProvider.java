package appeng.api.networking.energy;

import appeng.api.config.Actionable;

import java.util.Set;

/**
 * internal use only.
 */
public interface IEnergyGridProvider
{

	/**
	 * internal use only
	 */
	public double extractAEPower(double amt, Actionable mode, Set<IEnergyGrid> seen);

}
