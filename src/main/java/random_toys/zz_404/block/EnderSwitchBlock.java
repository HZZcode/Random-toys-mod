package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.block.block_entity.EnderSwitchBlockEntity;
import random_toys.zz_404.registry.ModItems;

public class EnderSwitchBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED;
    public static final MapCodec<EnderSwitchBlock> CODEC = createCodec(EnderSwitchBlock::new);

    public EnderSwitchBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnderSwitchBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(@NotNull BlockState state, World world, BlockPos pos) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected void neighborUpdate(BlockState state, @NotNull World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        boolean powered = state.get(POWERED);
        if (powered == world.isReceivingRedstonePower(pos)) return;
        if (powered) world.scheduleBlockTick(pos, this, 4);
        else cycleState(world, pos, state);
    }

    @Override
    protected void scheduledTick(@NotNull BlockState state, @NotNull ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED) && !world.isReceivingRedstonePower(pos)) cycleState(world, pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && !player.getMainHandStack().isOf(ModItems.ENDER_LINKER_CONFIGURATOR)) {
            state = cycleState(world, pos, state);
            playClickSound(player, world, pos, state);
            return ActionResult.CONSUME;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    private BlockState cycleState(@NotNull World world, BlockPos pos, BlockState state) {
        state = state.cycle(POWERED);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        if (world.getBlockEntity(pos) instanceof EnderSwitchBlockEntity entity)
            entity.syncState(world, state);
        return state;
    }

    protected static void playClickSound(PlayerEntity player, @NotNull WorldAccess world, BlockPos pos, @NotNull BlockState state) {
        float f = state.get(POWERED) ? 0.6F : 0.5F;
        world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
    }

    static {
        POWERED = Properties.POWERED;
    }
}
