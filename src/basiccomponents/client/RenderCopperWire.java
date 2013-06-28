package basiccomponents.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.block.IConnector;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import basiccomponents.common.BasicComponents;
import basiccomponents.common.tileentity.TileEntityCopperWire;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCopperWire extends TileEntitySpecialRenderer
{
	public static final ModelCopperWire model = new ModelCopperWire();

	public void renderModelAt(TileEntityCopperWire tileEntity, double d, double d1, double d2, float f)
	{
		// Texture file
		this.bindTextureByName(BasicComponents.MODEL_TEXTURE_DIRECTORY + "copperWire.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);

		boolean[] connectable = new boolean[] { false, false, false, false, false, false };

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity sideTile = VectorHelper.getTileEntityFromSide(tileEntity.worldObj, new Vector3(tileEntity), side);

			if (sideTile instanceof IConnector)
			{
				if (((IConnector) sideTile).canConnect(side.getOpposite()))
				{
					connectable[side.ordinal()] = true;
				}
			}
		}

		if (connectable[0])
		{
			model.renderBottom();
		}

		if (connectable[1])
		{
			model.renderTop();
		}

		if (connectable[2])
		{
			model.renderBack();
		}

		if (connectable[3])
		{
			model.renderFront();
		}

		if (connectable[4])
		{
			model.renderLeft();
		}

		if (connectable[5])
		{
			model.renderRight();
		}

		model.renderMiddle();
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double var2, double var4, double var6, float var8)
	{
		this.renderModelAt((TileEntityCopperWire) tileEntity, var2, var4, var6, var8);
	}
}