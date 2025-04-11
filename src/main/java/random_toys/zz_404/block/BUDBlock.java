package random_toys.zz_404.block;

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
import random_toys.zz_404.registry.ModCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BUDBlock extends Block {
    public static final MapCodec<BUDBlock> CODEC = createCodec(BUDBlock::new);
    public static final BooleanProperty POWERED;
    private static final long StrobeTimeSpan = 60;
    private static List<StrobeData> strobes = new ArrayList<>();

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
            setPowered(world, pos, state, true);
            world.scheduleBlockTick(pos, this, 2);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected void scheduledTick(@NotNull BlockState state, @NotNull ServerWorld world, BlockPos pos, Random random) {
        setPowered(world, pos, state, false);
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

    private void setPowered(@NotNull World world, BlockPos pos, @NotNull BlockState state, boolean powered) {
        long time = world.getTime();
        if (powered) strobes.add(new StrobeData(pos, time));
        strobes = strobes.stream().filter(strobeData -> time - strobeData.time <= StrobeTimeSpan)
                .collect(Collectors.toCollection(ArrayList::new));
        if (strobes.stream().filter(strobeData -> strobeData.pos.equals(pos))
                .toList().size() >= StrobeTimeSpan / 2)
            ModCriteria.triggerPlayers(world, pos, 6, ModCriteria.BUD_STROBE::trigger);
        world.setBlockState(pos, state.with(POWERED, powered));
    }

    private record StrobeData(BlockPos pos, long time) {}

    static {
        POWERED = Properties.POWERED;
    }
}
