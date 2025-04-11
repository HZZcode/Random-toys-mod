package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.block_entity.TransferringBlockEntity;

public class TransferringBlock extends BlockWithEntity {
    public static final MapCodec<TransferringBlock> CODEC = createCodec(TransferringBlock::new);
    public static final BooleanProperty POWERED;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public TransferringBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TransferringBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();
        if (world.getBlockEntity(pos) instanceof TransferringBlockEntity transfer) {
            if (stack.isEmpty() && !transfer.isEmpty()){
                transfer.clearItem();
                return ActionResult.SUCCESS;
            }
            if (!stack.isEmpty()){
                transfer.setItem(stack);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.TRANSFER,
                (world, pos, state, transferringBlockEntity)
                        -> transferringBlockEntity.tick(world, pos, state));
    }

    static {
        POWERED = Properties.POWERED;
    }
}
