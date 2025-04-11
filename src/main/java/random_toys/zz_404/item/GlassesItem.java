package random_toys.zz_404.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ArmorItem;
import random_toys.zz_404.registry.ModArmorMaterials;

public class GlassesItem extends ArmorItem {
    public GlassesItem(Settings settings) {
        super(ModArmorMaterials.GLASSES, Type.HELMET, settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
}