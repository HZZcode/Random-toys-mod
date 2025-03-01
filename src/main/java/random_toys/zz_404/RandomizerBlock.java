package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RandomizerBlock extends AbstractChestBlock<RandomizerBlockEntity> {
    public static final MapCodec<RandomizerBlock> CODEC = createCodec(settings -> new RandomizerBlock(settings, () -> ModBlockEntities.RANDOMIZER));

    public static final EnumProperty<RandomizerItemType> ITEM_TYPE = EnumProperty.of("item_type", RandomizerItemType.class);

    public RandomizerBlock(Settings settings, Supplier<BlockEntityType<? extends RandomizerBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
        this.setDefaultState(this.getStateManager().getDefaultState().with(ITEM_TYPE, RandomizerItemType.EMPTY));
    }

    @Override
    protected MapCodec<? extends AbstractChestBlock<RandomizerBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ITEM_TYPE);
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RandomizerBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private boolean isRandomizerItem(@NotNull ItemStack itemStack) {
        return itemStack.isOf(ModItems.RANDOMIZER1)
                || itemStack.isOf(ModItems.RANDOMIZER2)
                || itemStack.isOf(ModItems.RANDOMIZER3);
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            RandomizerBlockEntity randomizerBlockEntity = (RandomizerBlockEntity) world.getBlockEntity(pos);
            if (randomizerBlockEntity != null) {
                ItemStack playerHandItem = player.getMainHandStack();
                ItemStack singleHandItem = playerHandItem.copyWithCount(1);
                if (!playerHandItem.isEmpty()) {
                    if (isRandomizerItem(playerHandItem)) {
                        if (!randomizerBlockEntity.addItem(singleHandItem)) {
                            return ActionResult.CONSUME;
                        }
                        playerHandItem.decrementUnlessCreative(1, player);
                        world.setBlockState(pos, state.with(ITEM_TYPE, randomizerBlockEntity.getItemType()));
                        return ActionResult.SUCCESS;
                    }
                }
                else {
                    ItemStack itemStackToGive = randomizerBlockEntity.removeItem();
                    if (!itemStackToGive.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStackToGive);
                        world.setBlockState(pos, state.with(ITEM_TYPE, randomizerBlockEntity.getItemType()));
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.CONSUME;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, @NotNull World world, BlockPos pos) {
        RandomizerBlockEntity randomizerBlockEntity = (RandomizerBlockEntity) world.getBlockEntity(pos);
        return randomizerBlockEntity != null ? randomizerBlockEntity.getRandomNumber() : 0;
    }

    @Override
    protected void onStateReplaced(@NotNull BlockState state, World world, BlockPos pos, @NotNull BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && !world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RandomizerBlockEntity randomizerBlockEntity) {
                randomizerBlockEntity.dropItem();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.RANDOMIZER,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
