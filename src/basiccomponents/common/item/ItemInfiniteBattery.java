package basiccomponents.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.item.IItemElectric;

/**
 * An infinite battery used for players and modders to test things.
 * 
 * @author Calclavia
 * 
 */
public class ItemInfiniteBattery extends ItemBase implements IItemElectric
{
	public ItemInfiniteBattery(String name, int id)
	{
		super(name, id);
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("\u00a72Infinite");
	}

	@Override
	public float getEnergyStored(ItemStack itemStack)
	{
		return this.getMaxEnergyStored(itemStack);
	}

	@Override
	public void setEnergy(ItemStack itemStack, float joules)
	{

	}
	
	@Override
	public float getMaxEnergyStored(ItemStack itemStack)
	{
		return Float.POSITIVE_INFINITY;
	}

	@Override
	public float getVoltage(ItemStack itemStack)
	{
		return 25;
	}
	
	@Override
	public float getTransfer(ItemStack itemStack)
	{
		return getMaxEnergyStored(itemStack)*0.005F;
	}

	@Override
	public float receiveEnergy(ItemStack theItem, float energy, boolean doReceive)
	{
		return energy;
	}

	@Override
	public float transferEnergy(ItemStack theItem, float energy, boolean doTransfer) 
	{
		return energy;
	}
}