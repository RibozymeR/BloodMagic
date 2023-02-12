package WayofTime.alchemicalWizardry.common.block;

import java.util.Random;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.ModItems;
import WayofTime.alchemicalWizardry.api.items.IAltarManipulator;
import WayofTime.alchemicalWizardry.common.items.EnergyBattery;
import WayofTime.alchemicalWizardry.common.items.sigil.holding.SigilOfHolding;
import WayofTime.alchemicalWizardry.common.tileEntity.TEAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockAltar extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon2;
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;
	
	public BlockAltar() {
		super(Material.rock);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setCreativeTab(AlchemicalWizardry.tabBloodMagic);
		this.setBlockName("bloodAltar");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.topIcon = iconRegister.registerIcon("AlchemicalWizardry:BloodAltar_Top");
		this.sideIcon2 = iconRegister.registerIcon("AlchemicalWizardry:BloodAltar_SideType2");
		this.bottomIcon = iconRegister.registerIcon("AlchemicalWizardry:BloodAltar_Bottom");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch (side) {
			case 0:
				return this.bottomIcon;
			case 1:
				return this.topIcon;
			default:
				return this.sideIcon2;
		}
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		
		if (tile instanceof TEAltar) {
			final ItemStack stack = ((TEAltar) tile).getStackInSlot(0);
			
			if (stack != null && stack.getItem() instanceof EnergyBattery) {
				final EnergyBattery bloodOrb = (EnergyBattery) stack.getItem();
				final int maxEssence = bloodOrb.getMaxEssence();
				final int currentEssence = bloodOrb.getCurrentEssence(stack);
				final int level = currentEssence * 15 / maxEssence;
				return Math.min(15, level) % 16;
			}
		}
		
		return 0;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what,
			float these, float are) {
		final TEAltar tileEntity = (TEAltar) world.getTileEntity(x, y, z);
		
		if (tileEntity == null || player.isSneaking()) { return false; }
		
		final ItemStack playerItem = player.getCurrentEquippedItem();
		
		if (playerItem != null) {
			if (playerItem.getItem() instanceof tconstruct.library.tools.ToolCore) { return false; }
			if (playerItem.getItem().equals(ModItems.divinationSigil)) {
				if (player.worldObj.isRemote) {
					world.markBlockForUpdate(x, y, z);
				}
				else {
					tileEntity.sendChatInfoToPlayer(player);
				}
				
				return true;
			}
			if (playerItem.getItem().equals(ModItems.itemSeerSigil)) {
				if (player.worldObj.isRemote) {
					world.markBlockForUpdate(x, y, z);
				}
				else {
					tileEntity.sendMoreChatInfoToPlayer(player);
				}
				
				return true;
			}
			else if (playerItem.getItem() instanceof IAltarManipulator) {
				return false;
			}
			else if (playerItem.getItem().equals(ModItems.sigilOfHolding)) {
				final ItemStack item = SigilOfHolding.getCurrentSigil(playerItem);
				
				if (item != null && item.getItem().equals(ModItems.divinationSigil)) {
					if (player.worldObj.isRemote) {
						world.markBlockForUpdate(x, y, z);
					}
					else {
						tileEntity.sendChatInfoToPlayer(player);
					}
					
					return true;
				}
				else if (item != null && item.getItem().equals(ModItems.itemSeerSigil)) {
					if (player.worldObj.isRemote) {
						world.markBlockForUpdate(x, y, z);
					}
					else {
						tileEntity.sendMoreChatInfoToPlayer(player);
					}
					
					return true;
				}
			}
		}
		
		if (tileEntity.getStackInSlot(0) == null && playerItem != null) {
			final ItemStack newItem = playerItem.copy();
			newItem.stackSize = 1;
			--playerItem.stackSize;
			tileEntity.setInventorySlotContents(0, newItem);
			tileEntity.startCycle();
		}
		else if (tileEntity.getStackInSlot(0) != null && playerItem == null) {
			player.inventory.addItemStackToInventory(tileEntity.getStackInSlot(0));
			tileEntity.setInventorySlotContents(0, null);
			tileEntity.setActive();
		}
		world.markBlockForUpdate(x, y, z);
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		this.dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	private void dropItems(World world, int x, int y, int z) {
		final Random rand = new Random();
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		if (!(tileEntity instanceof IInventory)) { return; }
		
		final IInventory inventory = (IInventory) tileEntity;
		
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final ItemStack item = inventory.getStackInSlot(i);
			
			if (item != null && item.stackSize > 0) {
				final float rx = rand.nextFloat() * 0.8F + 0.1F;
				final float ry = rand.nextFloat() * 0.8F + 0.1F;
				final float rz = rand.nextFloat() * 0.8F + 0.1F;
				final EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz,
						new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));
				
				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}
				
				final float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
	
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	public int getRenderType() { return -1; }
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	@Override
	public boolean hasTileEntity() { return true; }
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		final TEAltar tileEntity = (TEAltar) world.getTileEntity(x, y, z);
		
		if (!tileEntity.isActive()) { return; }
		
		if (rand.nextInt(3) != 0) {}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) { return new TEAltar(); }
}
