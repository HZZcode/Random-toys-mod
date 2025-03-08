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
import org.jetbrains.annotations.NotNull;

import static random_toys.zz_404.mixin_utils.MixinSets.EndermanAvoidStarringItems;

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
    public static final Item ENDER_LINKER_CONFIGURATOR = registerItems("ender_linker_configurator", new EnderLinkerConfiguratorItem(new Item.Settings()));
    public static final Item HELPER = registerItems("helper", new HelperItem(new Item.Settings()));
    public static final Item GLASSES = registerItems("glasses", new GlassesItem(new Item.Settings().maxCount(1)));
    public static final Item JETPACKS = registerItems("jetpacks", new JetpackItem(new Item.Settings().maxCount(1).component(ModDataComponents.GAS_REMAINING, 0)));

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

        EndermanAvoidStarringItems.add(GLASSES);

        RandomToys.log("Registering Items");
    }
    private static void addRedstoneGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OBSERVER, ModBlocks.BUD);
        fabricItemGroupEntries.addAfter(Blocks.REDSTONE_BLOCK, ModBlocks.RANDOMIZER);
        fabricItemGroupEntries.addAfter(Items.REDSTONE, ModBlocks.COPPERED_REDSTONE_WIRE);
        fabricItemGroupEntries.addAfter(Items.REDSTONE_BLOCK, ModBlocks.COPPERED_REDSTONE_BLOCK);
        fabricItemGroupEntries.addBefore(Blocks.CHEST, ModBlocks.COMPRESSOR);
        fabricItemGroupEntries.addAfter(Blocks.HOPPER, ModBlocks.TRANSFER);
        fabricItemGroupEntries.addAfter(Blocks.FURNACE, ModBlocks.DISENCHANTMENTOR);
        fabricItemGroupEntries.addBefore(Blocks.PISTON, ModBlocks.VANISHING_DOOR);
    }

    private static void addNaturalGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OAK_LEAVES, ModBlocks.APPLE_LEAVES);
        fabricItemGroupEntries.addAfter(Blocks.BEDROCK, ModBlocks.BLACK_BEDROCK);
    }

    private static void addIngredientsGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Items.HEART_OF_THE_SEA, ModItems.ZZ_CORE);
    }

    private static void addCombatGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.TURTLE_HELMET, ModItems.GLASSES);
        fabricItemGroupEntries.addAfter(Items.TURTLE_HELMET, ModItems.JETPACKS);
    }

    private static void addFunctionalGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.SMITHING_TABLE, ModBlocks.BLACKSTONE_PROCESSING_TABLE);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.GILDED_BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Items.END_CRYSTAL, ModItems.BLACKSTONE_CRYSTAL);
        fabricItemGroupEntries.addAfter(Blocks.BARREL, ModBlocks.EXPERIENCE_COLLECTOR);
        fabricItemGroupEntries.addAfter(Blocks.BARREL, ModBlocks.COMPRESSOR);
        fabricItemGroupEntries.addAfter(Blocks.GRINDSTONE, ModBlocks.DISENCHANTMENTOR);
        fabricItemGroupEntries.addBefore(Blocks.FURNACE, ModBlocks.OXIDIZER);
    }

    private static void addSpawnEggGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.add(ModItems.ZZ_SPAWN_EGG);
        fabricItemGroupEntries.addAfter(Items.SPAWNER, ModBlocks.DISPOSABLE_SPAWNER);
    }

    public static final ItemGroup RandomToys_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(RandomToys.MOD_ID, "random-toys"),
            ItemGroup.create(null, -1).displayName(Text.translatable("itemGroup.random-toys"))
                    .icon(() -> new ItemStack(ModItems.ZZ_CORE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.HELPER);
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
                        entries.add(ModBlocks.BLACK_BEDROCK);
                        entries.add(ModBlocks.COMPRESSOR);
                        entries.add(ModBlocks.EXPERIENCE_COLLECTOR);
                        entries.add(ModBlocks.TRANSFER);
                        entries.add(ModBlocks.DISENCHANTMENTOR);
                        entries.add(ModBlocks.ENDER_LINKER);
                        entries.add(ModItems.ENDER_LINKER_CONFIGURATOR);
                        entries.add(ModBlocks.OXIDIZER);
                        entries.add(ModBlocks.VANISHING_DOOR);
                        entries.add(ModItems.GLASSES);
                        entries.add(ModItems.JETPACKS);
                    }).build());
}
