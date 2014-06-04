package appeng.api.parts.layers;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.LayerBase;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class LayerIPowerReceptor extends LayerBase implements IPowerReceptor
{

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		IPart part = getPart( side );
		if ( part instanceof IPowerReceptor )
			return ((IPowerReceptor) part).getPowerReceiver( side );
		return null;
	}

	@Override
	public void doWork(PowerHandler workProvider)
	{
		// do nothing, this seems pointless.
	}

	@Override
	public World getWorld()
	{
		return ((IPartHost) this).getTile().getWorldObj();
	}

}
