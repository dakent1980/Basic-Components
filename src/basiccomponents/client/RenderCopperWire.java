package basiccomponents.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

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

		TileEntity[] visuallyConnected = tileEntity.getAdjacentConnections();

		if (visuallyConnected[0] != null)
		{
			model.renderBottom();
		}

		if (visuallyConnected[1] != null)
		{
			model.renderTop();
		}

		if (visuallyConnected[2] != null)
		{
			model.renderBack();
		}

		if (visuallyConnected[3] != null)
		{
			model.renderFront();
		}

		if (visuallyConnected[4] != null)
		{
			model.renderLeft();
		}

		if (visuallyConnected[5] != null)
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