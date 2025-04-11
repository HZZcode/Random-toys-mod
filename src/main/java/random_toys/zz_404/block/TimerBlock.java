package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.block_entity.TimerBlockEntity;

import java.util.function.Supplier;

public class TimerBlock extends AbstractChestBlock<TimerBlockEntity> {
    public static final MapCodec<TimerBlock> CODEC = createCodec(settings -> new TimerBlock(settings, () -> ModBlockEntities.TIMER));

    @Override
    protected MapCodec<? extends AbstractChestBlock<TimerBlockEntity>> getCodec() {
        return CODEC;
    }

    public TimerBlock(Settings settings, Supplier<BlockEntityType<? extends TimerBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TimerBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = this.createScreenHandlerFactory(state, world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.TIMER,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, @NotNull World world, BlockPos pos) {
        int cycle = getCycle(world, pos) * 20;
        if (cycle == 0) return 0;
        int time = (int) world.getTime() % cycle;
        int stage = time / (cycle / 5);
        if (stage >= 5) stage = 5;
        if (world.getBlockEntity(pos) instanceof TimerBlockEntity timer) return getPower(timer.get(stage));
        return 0;
    }

    public static int getPower(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return getPower(stack.getMaxCount());
    }

    public static int getPower(int size) {
        if (size == 0) return 0;
        return Math.max(Math.min(size / 4, 15), 1);
    } //0 -> 0, 1 -> 1, 16 -> 4, 64 -> 15

    public static int getCycle(@NotNull World world, BlockPos pos) {
        return world.getReceivedRedstonePower(pos);
    }
}
