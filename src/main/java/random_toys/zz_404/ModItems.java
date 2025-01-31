package random_toys.zz_404;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item DICE = registerItems("dice", new RandomizerItem(new Item.Settings()));
    public static final Item CALC_CORE = registerItems("calc_core", new Item(new Item.Settings()));
    public static final Item RANDOMIZER1 = registerItems("randomizer1", new RandomizerItem(new Item.Settings(), 1));
    public static final Item RANDOMIZER2 = registerItems("randomizer2", new RandomizerItem(new Item.Settings(), 2));
    public static final Item RANDOMIZER3 = registerItems("randomizer3", new RandomizerItem(new Item.Settings(), 3));
    public static final Item ZZ_SPAWN_EGG = registerItems("zz_spawn_egg", new SpawnEggItem(ModEntities.ZZ, 0x20131c, 0xfcee4b, new Item.Settings()));
    public static final Item BLACKSTONE_CRYSTAL = registerItems("blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_BLACKSTONE, new Item.Settings()));
    public static final Item GILDED_BLACKSTONE_CRYSTAL = registerItems("gilded_blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_GILDED_BLACKSTONE, new Item.Settings().fireproof()));
    public static final Item ENCHANTED_GILDED_BLACKSTONE_CRYSTAL = registerItems("enchanted_gilded_blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_ENCHANTED_GILDED_BLACKSTONE, new Item.Settings().fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final Item ZZ_CORE = registerItems("zz_core", new ZZCoreItem(new Item.Settings().fireproof()));

    private static Item registerItems(String id, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(RandomToys.MOD_ID, id), item);
    }

    public static void registerItems(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(ModItems::addRedstoneGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::addNaturalGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addCombatGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addIngredientsGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::addFunctionalGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addSpawnEggGroupItems);

        RandomToys.LOGGER.info("Registering Items");
    }

    private static void addRedstoneGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OBSERVER, ModBlocks.BUD);
        fabricItemGroupEntries.addAfter(Blocks.REDSTONE_BLOCK, ModBlocks.RANDOMIZER);
        fabricItemGroupEntries.addAfter(Items.REDSTONE, ModBlocks.COPPERED_REDSTONE_WIRE);
        fabricItemGroupEntries.addAfter(Items.REDSTONE_BLOCK, ModBlocks.COPPERED_REDSTONE_BLOCK);
    }

    private static void addNaturalGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OAK_LEAVES, ModBlocks.APPLE_LEAVES);
    }

    private static void addIngredientsGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Items.HEART_OF_THE_SEA, ModItems.ZZ_CORE);
    }

    private static void addCombatGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.BLACKSTONE_CRYSTAL);
    }

    private static void addFunctionalGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.SMITHING_TABLE, ModBlocks.BLACKSTONE_PROCESSING_TABLE);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.BLACKSTONE_CRYSTAL);
    }

    private static void addSpawnEggGroupItems(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.add(ModItems.ZZ_SPAWN_EGG);
        fabricItemGroupEntries.addAfter(Items.SPAWNER, ModBlocks.DISPOSABLE_SPAWNER);
    }

    public static final ItemGroup RandomToys_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(RandomToys.MOD_ID, "random-toys"),
            ItemGroup.create(null, -1).displayName(Text.translatable("itemGroup.random-toys"))
                    .icon(() -> new ItemStack(ModItems.DICE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.DICE);
                        entries.add(ModItems.CALC_CORE);
                        entries.add(ModItems.RANDOMIZER1);
                        entries.add(ModItems.RANDOMIZER2);
                        entries.add(ModItems.RANDOMIZER3);
                        entries.add(ModBlocks.BUD);
                        entries.add(ModBlocks.RANDOMIZER);
                        entries.add(ModBlocks.APPLE_LEAVES);
                        entries.add(ModBlocks.COPPERED_REDSTONE_WIRE);
                        entries.add(ModBlocks.COPPERED_REDSTONE_BLOCK);
                        entries.add(ModItems.ZZ_SPAWN_EGG);
                        entries.add(ModBlocks.DISPOSABLE_SPAWNER);
                        entries.add(ModItems.BLACKSTONE_CRYSTAL);
                        entries.add(ModItems.GILDED_BLACKSTONE_CRYSTAL);
                        entries.add(ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL);
                        entries.add(ModItems.ZZ_CORE);
                        entries.add(ModBlocks.BLACKSTONE_PROCESSING_TABLE);
                    }).build());
}
