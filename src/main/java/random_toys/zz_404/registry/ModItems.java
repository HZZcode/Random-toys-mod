package random_toys.zz_404.registry;

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
import random_toys.zz_404.*;
import random_toys.zz_404.entity.ZZCoreItem;
import random_toys.zz_404.item.*;

import java.util.ArrayList;

import static random_toys.zz_404.mixin_utils.MixinSets.EndermanAvoidStarringItems;
import static random_toys.zz_404.mixin_utils.MixinSets.PlayerTickBehaviours;

public class ModItems {
    public static final Item DICE = registerItem("dice", new RandomizerItem(new Item.Settings()));
    public static final Item CALC_CORE = registerItem("calc_core", new Item(new Item.Settings()));
    public static final Item RANDOMIZER1 = registerItem("randomizer1", new RandomizerItem(new Item.Settings(), 1));
    public static final Item RANDOMIZER2 = registerItem("randomizer2", new RandomizerItem(new Item.Settings(), 2));
    public static final Item RANDOMIZER3 = registerItem("randomizer3", new RandomizerItem(new Item.Settings(), 3));
    public static final Item ZZ_SPAWN_EGG = registerItem("zz_spawn_egg", new SpawnEggItem(ModEntities.ZZ, 0x20131c, 0xfcee4b, new Item.Settings()));
    public static final Item BLACKSTONE_CRYSTAL = registerItem("blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_BLACKSTONE, new Item.Settings()));
    public static final Item GILDED_BLACKSTONE_CRYSTAL = registerItem("gilded_blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_GILDED_BLACKSTONE, new Item.Settings().fireproof()));
    public static final Item ENCHANTED_GILDED_BLACKSTONE_CRYSTAL = registerItem("enchanted_gilded_blackstone_crystal", new ThrowableItem<>(ModEntities.THROWN_ENCHANTED_GILDED_BLACKSTONE, new Item.Settings().fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final Item ZZ_CORE = registerItem("zz_core", new ZZCoreItem(new Item.Settings().fireproof()));
    public static final Item ENDER_LINKER_CONFIGURATOR = registerItem("ender_linker_configurator", new EnderLinkerConfiguratorItem(new Item.Settings()));
    public static final Item HELPER = registerItem("helper", new HelperItem(new Item.Settings()));
    public static final Item GLASSES = registerItem("glasses", new GlassesItem(new Item.Settings().maxCount(1)));
    public static final Item JETPACKS = registerItem("jetpacks", new JetpackItem(new Item.Settings().maxCount(1).component(ModDataComponents.GAS_REMAINING, 0)));
    public static final Item MINER_SPAWN_EGG = registerItem("miner_spawn_egg", new SpawnEggItem(ModEntities.MINER, 0x00a4a4, 0x9b6349, new Item.Settings()));
    public static final Item REGEX_FILTER = registerItem("regex_filter", new RegexFilterItem(new Item.Settings().maxCount(1)));
    public static final Item BLACK_BEDROCK_HELMET = registerItem("black_bedrock_helmet", new BlackBedrockArmorItem(ArmorItem.Type.HELMET));
    public static final Item BLACK_BEDROCK_CHESTPLATE = registerItem("black_bedrock_chestplate", new BlackBedrockArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final Item BLACK_BEDROCK_LEGGINGS = registerItem("black_bedrock_leggings", new BlackBedrockArmorItem(ArmorItem.Type.LEGGINGS));
    public static final Item BLACK_BEDROCK_BOOTS = registerItem("black_bedrock_boots", new BlackBedrockArmorItem(ArmorItem.Type.BOOTS));
    public static final Item BLACK_BEDROCK_SWORD = registerItem("black_bedrock_sword", new BlackBedrockSwordItem(ModToolMaterials.BLACK_BEDROCK, new Item.Settings().fireproof().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.BLACK_BEDROCK, 3, -2.4f))));
    public static final Item GRAPPLING_HOOK = registerItem("grappling_hook", new GrapplingHookItem(new Item.Settings().maxDamage(256).component(ModDataComponents.HOOK_UUID, null)));

    private static Item registerItem(String id, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(RandomToys.MOD_ID, id), item);
    }

    private static void addRedstoneGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OBSERVER, ModBlocks.BUD);
        fabricItemGroupEntries.addAfter(Blocks.REDSTONE_BLOCK, ModBlocks.RANDOMIZER);
        fabricItemGroupEntries.addAfter(Items.REDSTONE, ModBlocks.COPPERED_REDSTONE_WIRE);
        fabricItemGroupEntries.addAfter(Items.REDSTONE_BLOCK, ModBlocks.COPPERED_REDSTONE_BLOCK);
        fabricItemGroupEntries.addBefore(Blocks.CHEST, ModBlocks.COMPRESSOR);
        fabricItemGroupEntries.addAfter(Blocks.HOPPER, ModBlocks.TRANSFER);
        fabricItemGroupEntries.addAfter(Blocks.FURNACE, ModBlocks.DISENCHANTMENTOR);
        fabricItemGroupEntries.addAfter(Blocks.COMPARATOR, ModBlocks.TIMER);
        fabricItemGroupEntries.addBefore(Blocks.PISTON, ModBlocks.VANISHING_DOOR);
        fabricItemGroupEntries.addBefore(Blocks.PISTON, ModBlocks.BELT);
        fabricItemGroupEntries.addBefore(Blocks.TNT, ModBlocks.CHUNK_DESTROYER);
        fabricItemGroupEntries.addBefore(Blocks.TNT, ModBlocks.DESTROYER);
    }

    private static void addNaturalGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.addAfter(Blocks.OAK_LEAVES, ModBlocks.APPLE_LEAVES);
        fabricItemGroupEntries.addAfter(Blocks.BEDROCK, ModBlocks.BLACK_BEDROCK);
        fabricItemGroupEntries.addAfter(Blocks.MAGMA_BLOCK, ModBlocks.SOLID_LAVA);
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
        fabricItemGroupEntries.addAfter(Items.NETHERITE_BOOTS, ModItems.BLACK_BEDROCK_BOOTS);
        fabricItemGroupEntries.addAfter(Items.NETHERITE_BOOTS, ModItems.BLACK_BEDROCK_LEGGINGS);
        fabricItemGroupEntries.addAfter(Items.NETHERITE_BOOTS, ModItems.BLACK_BEDROCK_CHESTPLATE);
        fabricItemGroupEntries.addAfter(Items.NETHERITE_BOOTS, ModItems.BLACK_BEDROCK_HELMET);
        fabricItemGroupEntries.addAfter(Items.NETHERITE_SWORD, ModItems.BLACK_BEDROCK_SWORD);
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
        fabricItemGroupEntries.addAfter(Blocks.ENDER_CHEST, ModBlocks.ENDER_HOPPER);
        fabricItemGroupEntries.addAfter(Blocks.ENDER_CHEST, ModBlocks.ENDER_LINKER);
        fabricItemGroupEntries.addAfter(Items.FISHING_ROD, ModItems.GRAPPLING_HOOK);
    }

    private static void addSpawnEggGroupItems(@NotNull FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.add(ModItems.ZZ_SPAWN_EGG);
        fabricItemGroupEntries.addAfter(Items.SPAWNER, ModBlocks.DISPOSABLE_SPAWNER);
        fabricItemGroupEntries.add(ModItems.MINER_SPAWN_EGG);
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
                        entries.add(ModBlocks.IMITATOR);
                        entries.add(ModItems.GLASSES);
                        entries.add(ModItems.JETPACKS);
                        entries.add(ModBlocks.BELT);
                        entries.add(ModItems.MINER_SPAWN_EGG);
                        entries.add(ModItems.REGEX_FILTER);
                        entries.add(ModBlocks.SOLID_LAVA);
                        entries.add(ModBlocks.DESTROYER);
                        entries.add(ModItems.BLACK_BEDROCK_HELMET);
                        entries.add(ModItems.BLACK_BEDROCK_CHESTPLATE);
                        entries.add(ModItems.BLACK_BEDROCK_LEGGINGS);
                        entries.add(ModItems.BLACK_BEDROCK_BOOTS);
                        entries.add(ModBlocks.BLACK_BEDROCK_PROCESSING_TABLE);
                        entries.add(ModBlocks.TIMER);
                        entries.add(ModBlocks.ENDER_HOPPER);
                        entries.add(ModBlocks.CHUNK_DESTROYER);
                        entries.add(ModItems.BLACK_BEDROCK_SWORD);
                        entries.add(ModItems.GRAPPLING_HOOK);
                    }).build());

    public static void registerItems(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(ModItems::addRedstoneGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::addNaturalGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addCombatGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addIngredientsGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::addFunctionalGroupItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addSpawnEggGroupItems);

        EndermanAvoidStarringItems.add(GLASSES);

        PlayerTickBehaviours.add(player -> {
            if (!player.isSpectator() && !player.isCreative()) {
                ArrayList<ItemStack> stacks = JetpackItem.getRemainingWearingStacks(player);
                if (!stacks.isEmpty()) {
                    ItemStack stack = stacks.getFirst();
                    if (JetpackItem.getRemainingGas(stack) == 0 || player.isOnGround()
                            || !ModKeyBindings.JETPACK_ACTIVATE.isPressed()) {
                        player.getAbilities().flying = false;
                        return;
                    }
                    if (player.getWorld().getTime() % 20 == 0)
                        stack.set(ModDataComponents.GAS_REMAINING, Math.max(0, JetpackItem.getRemainingGas(stack) - 1));
                    if (player.getAbilities().flying) return;
                    player.getAbilities().setFlySpeed(player.getMovementSpeed());
                    player.getAbilities().flying = true;
                }
                else player.getAbilities().flying = false;
            }
        });

        RandomToys.log("Registering Items");
    }
}
