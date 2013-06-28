package basiccomponents.common.tileentity;

import net.minecraft.block.Block;
import universalelectricity.prefab.tile.TileEntityConductor;
import basiccomponents.common.BasicComponents;

public class TileEntityCopperWire extends TileEntityConductor 
{
	/**
	 * Changed this if your mod wants to nerf Basic Component's copper wire.
	 */
	public static float RESISTANCE = 0.05F;
	public static float MAX_AMPS = 200;
	
	@Override
	public float getResistance()
	{
		return RESISTANCE;
	}

	@Override
	public float getCurrentCapacity()
	{
		return MAX_AMPS;
	}
}
