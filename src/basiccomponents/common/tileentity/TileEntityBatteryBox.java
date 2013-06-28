package basiccomponents.common.tileentity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IEnergyStorage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.IItemElectric;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricalStorage;
import basiccomponents.common.BasicComponents;
import basiccomponents.common.block.BlockBasicMachine;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class TileEntityBatteryBox extends TileEntityElectricalStorage implements IElectrical, IEnergyStorage, IPacketReceiver, ISidedInventory
{
	private ItemStack[] containingItems = new ItemStack[2];

	public final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.isDisabled())
		{
			if (!this.worldObj.isRemote)
			{
				/**
				 * Recharges electric item.
				 */
				this.setEnergyStored(this.getEnergyStored() - ElectricItemHelper.chargeItem(this.containingItems[0], this.getEnergyStored(), this.getVoltage()));

				/**
				 * Decharge electric item.
				 */
				this.setEnergyStored(this.getEnergyStored() + ElectricItemHelper.dechargeItem(this.containingItems[1], this.getMaxEnergyStored() - this.getEnergyStored(), this.getVoltage()));
				
				ForgeDirection outputDirection = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockBasicMachine.BATTERY_BOX_METADATA + 2);
				TileEntity outputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), outputDirection);
				IElectricityNetwork outputNetwork = ElectricityNetworkHelper.getNetworkFromTileEntity(outputTile, outputDirection);

				if (outputNetwork != null)
				{
					float powerRequest = outputNetwork.getRequest(this);

					if (powerRequest > 0)
					{
						ElectricityPack sendPack = ElectricityPack.getFromWatts(Math.min(this.getEnergyStored(), Math.min(2500, powerRequest)), getVoltage());
						float producedPower = outputNetwork.produce(sendPack, this);
						this.setEnergyStored(this.getEnergyStored() - producedPower);
					}
				}
			}
		}

		/**
		 * Gradually lose energy.
		 */
		this.setEnergyStored(this.getEnergyStored() - 0.00005f);

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 3 == 0)
			{
				for (EntityPlayer player : this.playersUsing)
				{
					PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), (Player) player);
				}
			}
		}
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == ForgeDirection.getOrientation(this.getBlockMetadata() - BlockBasicMachine.BATTERY_BOX_METADATA + 2) || direction == ForgeDirection.getOrientation(this.getBlockMetadata() - BlockBasicMachine.BATTERY_BOX_METADATA + 2).getOpposite();
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(BasicComponents.CHANNEL, this, this.getEnergyStored(), this.disabledTicks);
	}

	@Override
	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			this.setEnergyStored(dataStream.readFloat());
			this.disabledTicks = dataStream.readInt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", var2);
	}

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName()
	{
		return LanguageRegistry.instance().getStringLocalization("tile." + BasicComponents.TEXTURE_NAME_PREFIX + "bcMachine.1.name");
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}

	@Override
	public boolean isStackValidForSlot(int slotID, ItemStack itemstack)
	{
		return itemstack.getItem() instanceof IItemElectric;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slotID)
	{
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
	{
		if (this.isStackValidForSlot(slotID, itemstack))
		{
			if (slotID == 0)
			{
				return ((IItemElectric) itemstack.getItem()).getTransfer(itemstack) > 0;
			}
			else if (slotID == 1)
			{
				return ((IItemElectric) itemstack.getItem()).getEnergyStored(itemstack) > 0;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if (this.isStackValidForSlot(slotID, itemstack))
		{
			if (slotID == 0)
			{
				return ((IItemElectric) itemstack.getItem()).getTransfer(itemstack) <= 0;
			}
			else if (slotID == 1)
			{
				return ((IItemElectric) itemstack.getItem()).getEnergyStored(itemstack) <= 0;
			}
		}

		return false;

	}

	@Override
	public float getMaxEnergyStored()
	{
		return 5000000;
	}

	@Override
	public float receiveElectricity(ElectricityPack electricityPack, boolean doReceive)
	{
		float newStoredEnergy = Math.min(this.getEnergyStored() + electricityPack.getWatts(), this.getMaxEnergyStored());

		if (doReceive)
		{
			this.setEnergyStored(newStoredEnergy);
		}

		return Math.max(electricityPack.getWatts() - newStoredEnergy, 0);
	}

	@Override
	public float getRequest(ForgeDirection direction)
	{
		return this.getMaxEnergyStored() - this.getEnergyStored();
	}

	@Override
	public float getVoltage()
	{
		return 120;
	}
}
