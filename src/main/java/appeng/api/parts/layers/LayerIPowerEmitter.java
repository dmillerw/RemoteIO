package appeng.api.parts.layers;

import appeng.api.parts.IPart;
import appeng.api.parts.LayerBase;
import buildcraft.api.power.IPowerEmitter;
import net.minecraftforge.common.util.ForgeDirection;

public class LayerIPowerEmitter extends LayerBase implements IPowerEmitter
{

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side)
	{
		IPart part = getPart( side );
		if ( part instanceof IPowerEmitter )
			return ((IPowerEmitter) part).canEmitPowerFrom( side );
		return false;
	}
}
