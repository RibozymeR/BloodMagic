package WayofTime.alchemicalWizardry.common.items;

import java.util.ArrayList;

import com.rwtema.extrautils.item.ItemAngelRing;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.items.interfaces.ArmourUpgrade;
import WayofTime.alchemicalWizardry.common.items.armour.BoundArmour;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class ItemBoundRing extends ItemAngelRing implements ArmourUpgrade {
	public static ArrayList<String> flyingPlayers = new ArrayList<String>();

	public ItemBoundRing() {
		this.setCreativeTab(AlchemicalWizardry.tabBloodMagic);
	}

	@Override
	public void onArmourUpdate(World world, EntityPlayer player, ItemStack thisItemStack) {
		player.capabilities.allowFlying = true;
	}

	@Override
	public boolean isUpgrade() { return true; }

	@Override
	public int getEnergyForTenSeconds() { return 1000; }

	@SubscribeEvent
	public void canFly(LivingUpdateEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) { return; }
		EntityPlayer player = (EntityPlayer) event.entityLiving;

		if (this.shouldFly(player)) {
			if (!(flyingPlayers.contains(player.getDisplayName()))) { flyingPlayers.add(player.getDisplayName()); }
			player.capabilities.allowFlying = true;
		}
		else {
			if (flyingPlayers.contains(player.getDisplayName())) {
				flyingPlayers.remove(player.getDisplayName());
				player.capabilities.allowFlying = false;
			}
		}
	}

	public boolean shouldFly(EntityPlayer player) {
		for (int i = 0; i < 4; i++) {
			if (player.getCurrentArmor(i).getItem() instanceof BoundArmour) {
				BoundArmour armor = (BoundArmour) player.getCurrentArmor(i).getItem();
				for (ItemStack item : armor.getInternalInventory(player.getCurrentArmor(i))) {
					if (item.getItem() instanceof ItemBoundRing) { return true; }
				}
			}
		}
		return false;
	}
}
