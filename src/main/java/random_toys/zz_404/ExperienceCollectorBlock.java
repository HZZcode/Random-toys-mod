package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExperienceCollectorBlock extends BlockWithEntity {
    public static final MapCodec<ExperienceCollectorBlock> CODEC = createCodec(ExperienceCollectorBlock::new);

    public static final IntProperty LEVEL;
    public static final int max = 12;

    public MapCodec<ExperienceCollectorBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ExperienceCollectorBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(LEVEL, 0));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExperienceCollectorBlockEntity(pos, state);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, @NotNull World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ExperienceCollectorBlockEntity entity)
            return ExperienceCollectorBlockEntity.min(15,
                    16 * entity.experience / ExperienceCollectorBlockEntity.max);
        return 0;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.EXPERIENCE_COLLECTOR,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    public static @Nullable ItemStack enchantBook(@NotNull World world, ExperienceCollectorBlockEntity entity) {
        Optional<RegistryEntryList.Named<Enchantment>> optional = world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isPresent()) {
            RegistryEntryList.Named<Enchantment> namedList = optional.get();
            int level = ExperienceCollectorBlockEntity.max(Xp2Level(entity.experience), 30);
            List<?> list = EnchantmentHelper.generateEnchantments(world.random,
                    new ItemStack(Items.BOOK), level, namedList.stream());
            ItemStack newStack = new ItemStack(Items.ENCHANTED_BOOK);
            for (Object object : list) if (object instanceof EnchantmentLevelEntry enchantmentLevelEntry)
                newStack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
            entity.experience -= (int) Level2Xp(level);
            return newStack;
        }
        return null;
    }

    @Override
    protected ItemActionResult onUseWithItem(@NotNull ItemStack stack, BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (world.getBlockEntity(pos) instanceof ExperienceCollectorBlockEntity entity && !world.isClient) {
            if (stack.isOf(Items.GLASS_BOTTLE) && entity.experience >= 8) {
                int i = 3 + world.random.nextInt(5) + world.random.nextInt(5);
                entity.experience -= i;
                stack.decrementUnlessCreative(1, player);
                player.giveItemStack(new ItemStack(Items.EXPERIENCE_BOTTLE));
                return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            else if (stack.isOf(Items.BOOK) && entity.experience >= 100) {
                ItemStack newStack = enchantBook(world, entity);
                if (newStack != null) {
                    stack.decrementUnlessCreative(1, player);
                    player.giveItemStack(newStack);
                    return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }
            }
            else {
                Optional<EnchantmentEffectContext> optional = EnchantmentHelper
                        .chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, player, ItemStack::isDamaged);
                if (optional.isPresent() && player instanceof ServerPlayerEntity serverPlayer) {
                    ItemStack itemStack = (optional.get()).stack();
                    int i = EnchantmentHelper.getRepairWithXp(serverPlayer.getServerWorld(), itemStack, entity.experience);
                    int j = Math.min(i, itemStack.getDamage());
                    itemStack.setDamage(itemStack.getDamage() - j);
                    entity.experience -= j;
                    return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
        if (!player.isSpectator() && !world.isClient
                && world.getBlockEntity(pos) instanceof ExperienceCollectorBlockEntity entity) {
            final int unitXp = 100;
            if (player.isSneaking()) entity.transform(player, unitXp);
            else entity.transform(player, -ExperienceCollectorBlockEntity.max(entity.experience / 2, unitXp));
            return ActionResult.CONSUME;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void onStateReplaced(@NotNull BlockState state, World world, BlockPos pos, @NotNull BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world instanceof ServerWorld server)
            onStacksDropped(state, server, pos, new ItemStack(Items.DIAMOND_PICKAXE, 1), true);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (dropExperience && !world.isClient
                && world.getBlockEntity(pos) instanceof ExperienceCollectorBlockEntity entity)
            dropExperience(world, pos, entity.experience);
        super.onStacksDropped(state, world, pos, tool, dropExperience);
    }

    public static double Level2Xp(int l) {
        if (l >= 0 && l <= 16) return l * l + 6 * l;
        if (l >= 17 && l <= 31) return 2.5 * l * l - 40.5 * l + 360;
        if (l >= 32) return 4.5 * l * l - 162.5 * l + 2220;
        return 0;
    }

    public static int Xp2Level(double xp) {
        for (int i = 0;;i++) if (Level2Xp(i) >= xp) return i;
    }

    static {
        LEVEL = IntProperty.of("level", 0, max);
    }
}
