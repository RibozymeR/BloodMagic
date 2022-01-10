package WayofTime.alchemicalWizardry.common.book;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemBMBook extends Item {
	public ItemBMBook() {
		this.setMaxStackSize(1);
		this.setCreativeTab(AlchemicalWizardry.tabBloodMagic);
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		this.itemIcon = ir.registerIcon("AlchemicalWizardry:guide");
	}
}
