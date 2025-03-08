package random_toys.zz_404;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ArmorItem;

public class GlassesItem extends ArmorItem {
    public GlassesItem(Settings settings) {
        super(ModArmorMaterials.GLASSES, Type.HELMET, settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
}