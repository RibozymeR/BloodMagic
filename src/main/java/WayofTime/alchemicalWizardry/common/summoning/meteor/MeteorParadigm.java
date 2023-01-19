package WayofTime.alchemicalWizardry.common.summoning.meteor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MeteorParadigm {
	public List<MeteorParadigmComponent> componentList = new ArrayList();
	public ItemStack focusStack;
	public int radius;
	public static int maxChance = 1000;
	int totalChance = 0;
	
	public static Random rand = new Random();
	
	public MeteorParadigm(ItemStack focusStack, int radius) {
		this.focusStack = focusStack;
		this.radius = radius;
	}
	
	public void parseStringArray(String[] oreArray) {
		for (int i = 0; i + 1 < oreArray.length; i += 2) {
			final String oreName = oreArray[i];
			final int oreChance = Integer.parseInt(oreArray[i + 1]);
			this.totalChance += oreChance;
			final MeteorParadigmComponent mpc = new MeteorParadigmComponent(oreName, oreChance);
			this.componentList.add(mpc);
		}
	}
	
	public void createMeteorImpact(World world, int x, int y, int z, boolean[] flags) {
		boolean hasTerrae = false;
		boolean hasOrbisTerrae = false;
		boolean hasCrystallos = false;
		boolean hasIncendium = false;
		boolean hasTennebrae = false;
		
		if (flags != null && flags.length >= 5) {
			hasTerrae = flags[0];
			hasOrbisTerrae = flags[1];
			hasCrystallos = flags[2];
			hasIncendium = flags[3];
			hasTennebrae = flags[4];
		}
		
		int newRadius = this.radius;
		int chance = this.totalChance * 4;
		if (this.focusStack.getItem().equals(Items.nether_star)) {
			chance = 1000;
		}
		
		if (hasOrbisTerrae) {
			newRadius += 2;
			chance += chance / 5;
		}
		else if (hasTerrae) {
			newRadius += 1;
			chance += chance / 10;
		}
		
		world.createExplosion(null, x, y, z, newRadius * 4, AlchemicalWizardry.doMeteorsDestroyBlocks);
		
		final float iceChance = hasCrystallos ? 1 : 0;
		final float soulChance = hasIncendium ? 1 : 0;
		final float obsidChance = hasTennebrae ? 1 : 0;
		
		final float totalChance = iceChance + soulChance + obsidChance;
		
		for (int i = -newRadius; i <= newRadius; i++) {
			for (int j = -newRadius; j <= newRadius; j++) {
				for (int k = -newRadius; k <= newRadius; k++) {
					if (i * i + j * j + k * k >= (newRadius + 0.50f) * (newRadius + 0.50f)
							|| !world.isAirBlock(x + i, y + j, z + k)) {
						continue;
					}
					
					int randNum = world.rand.nextInt(chance);
					boolean hasPlacedBlock = false;
					
					for (final MeteorParadigmComponent mpc : this.componentList) {
						if (mpc == null || !mpc.isValidBlockParadigm()) {
							continue;
						}
						
						randNum -= mpc.getChance();
						
						if (randNum < 0) {
							final ItemStack blockStack = mpc.getValidBlockParadigm();
							if (blockStack != null && blockStack.getItem() instanceof ItemBlock) {
								((ItemBlock) blockStack.getItem()).placeBlockAt(blockStack, null, world, x + i, y + j,
										z + k, 0, 0, 0, 0, blockStack.getItemDamage());
								world.markBlockForUpdate(x + i, y + j, z + k);
								hasPlacedBlock = true;
								break;
							}
							// world.setBlock(x + i, y + j, z + k,
							// Block.getBlockById(Item.getIdFromItem(blockStack.getItem())),
							// blockStack.getItemDamage(), 3);
							// hasPlacedBlock = true;
							// break;
						}
					}
					
					if (!hasPlacedBlock) {
						float randChance = rand.nextFloat() * totalChance;
						
						if (randChance < iceChance) {
							world.setBlock(x + i, y + j, z + k, Blocks.ice, 0, 3);
						}
						else {
							randChance -= iceChance;
							
							if (randChance < soulChance) {
								switch (rand.nextInt(3)) {
									case 0:
										world.setBlock(x + i, y + j, z + k, Blocks.soul_sand, 0, 3);
										break;
									case 1:
										world.setBlock(x + i, y + j, z + k, Blocks.glowstone, 0, 3);
										break;
									case 2:
										world.setBlock(x + i, y + j, z + k, Blocks.netherrack, 0, 3);
										break;
								}
							}
							else {
								randChance -= soulChance;
								
								if (randChance < obsidChance) {
									world.setBlock(x + i, y + j, z + k, Blocks.obsidian, 0, 3);
								}
								else {
									randChance -= obsidChance;
									
									world.setBlock(x + i, y + j, z + k, Blocks.stone, 0, 3);
								}
							}
						}
					}
				}
			}
		}
	}
}
