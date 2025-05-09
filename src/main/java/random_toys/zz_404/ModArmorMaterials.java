package random_toys.zz_404;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> GLASSES = register("glasses",
            Util.make(new EnumMap<>(ArmorItem.Type.class),
                    map -> map.put(ArmorItem.Type.HELMET, 1)),
            3, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.ofItems(Items.GLASS));
    public static final RegistryEntry<ArmorMaterial> JETPACK = register("jetpack",
            Util.make(new EnumMap<>(ArmorItem.Type.class),
                    map -> map.put(ArmorItem.Type.CHESTPLATE, 1)),
            3, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F,
            () -> Ingredient.ofItems(Items.GLASS));
    public static final RegistryEntry<ArmorMaterial> BLACK_BEDROCK = register("black_bedrock",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 7);
        map.put(ArmorItem.Type.LEGGINGS, 13);
        map.put(ArmorItem.Type.CHESTPLATE, 17);
        map.put(ArmorItem.Type.HELMET, 7);
        map.put(ArmorItem.Type.BODY, 23);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 7.0F, 0.5F,
            () -> Ingredient.ofItems(ModBlocks.BLACK_BEDROCK));

    private static RegistryEntry<ArmorMaterial> register(String id,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Identifier.ofVanilla(id)));
        return register(id, defense, enchantability, equipSound, toughness, knockbackResistance, repairIngredient, list);
    }

    private static RegistryEntry<ArmorMaterial> register(
            String id,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers
    ) {
        EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            enumMap.put(type, defense.get(type));
        }

        return Registry.registerReference(
                Registries.ARMOR_MATERIAL,
                Identifier.ofVanilla(id),
                new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }
}
