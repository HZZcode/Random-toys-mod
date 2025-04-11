package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BUDBlock extends Block {
    public static final MapCodec<BUDBlock> CODEC = createCodec(BUDBlock::new);
    public static final BooleanProperty POWERED;

    public MapCodec<BUDBlock> getCodec() {
        return CODEC;
    }

    public BUDBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected void neighborUpdate(BlockState state, @NotNull World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            world.setBlockState(pos, state.with(POWERED, true));
            world.scheduleBlockTick(pos, this, 2);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected void scheduledTick(@NotNull BlockState state, @NotNull ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, false));
        this.updateNeighbors(world, pos);
    }

    protected void updateNeighbors(@NotNull World world, BlockPos pos) {
        world.updateNeighbors(pos, this);
    }

    @Override
    protected boolean emitsRedstonePower(@NotNull BlockState state) {
        return state.get(POWERED);
    }

    @Override
    protected int getStrongRedstonePower(@NotNull BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED)) {
            return 15;
        }
        return 0;
    }

    @Override
    protected int getWeakRedstonePower(@NotNull BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED)) {
            return 15;
        }
        return 0;
    }

    static {
        POWERED = Properties.POWERED;
    }
}
