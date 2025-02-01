package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

public class AppleLeavesBlock extends LeavesBlock implements Fertilizable {
    public static final MapCodec<AppleLeavesBlock> CODEC = createCodec(AppleLeavesBlock::new);

    public static final BooleanProperty APPLES;

    public AppleLeavesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(DISTANCE, 7).with(PERSISTENT, false).with(WATERLOGGED, false).with(APPLES, false));
    }

    @Override
    protected void appendProperties(net.minecraft.state.StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(APPLES);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return super.hasRandomTicks(state)
                || (!(boolean) state.get(APPLES) && !(boolean) state.get(WATERLOGGED));
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if(random.nextInt(15) == 0) {
            BlockState blockState = state.with(APPLES, true);
            world.setBlockState(pos, blockState, 2);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
        }
    }

    private void dropApples(@NotNull BlockState state, @NotNull World world, BlockPos pos) {
        dropStack(world, pos, new ItemStack(Items.APPLE, 1 + world.random.nextInt(1)));
        world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
        BlockState blockState = state.with(APPLES, false);
        world.setBlockState(pos, blockState, 2);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient) {
            if(state.get(APPLES)) {
                dropApples(state, world, pos);
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.CONSUME;
    }

    public void useByDispenser(@NotNull World world, BlockPos pos) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);
            if (state.get(APPLES)) {
                dropApples(state, world, pos);
            }
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, @NotNull BlockState state) {
        return !(boolean) state.get(APPLES);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(@NotNull ServerWorld world, Random random, BlockPos pos, @NotNull BlockState state) {
        BlockState blockState = state.with(APPLES, true);
        world.setBlockState(pos, blockState, 2);
    }

    static {
        APPLES = BooleanProperty.of("apples");
    }
}
