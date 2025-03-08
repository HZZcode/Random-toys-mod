package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class BeltBlock extends BlockWithEntity {
    public static final MapCodec<BeltBlock> CODEC = createCodec(BeltBlock::new);
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 15, 16);
    public static final BooleanProperty POWERED;
    public static final DirectionProperty DIRECTION;

    public BeltBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false).with(DIRECTION, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED, DIRECTION);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return true;
    }

    protected BlockState rotate(@NotNull BlockState state, @NotNull BlockRotation rotation) {
        return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
    }

    protected BlockState mirror(@NotNull BlockState state, @NotNull BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(DIRECTION)));
    }

    private static boolean hasEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity
                || entity instanceof TntEntity || entity instanceof BoatEntity || entity instanceof ItemEntity;
    }

    @Override
    protected void onEntityCollision(BlockState state, @NotNull World world, BlockPos pos, Entity entity) {
        if (world.getBlockState(pos).get(POWERED) && hasEffects(entity)) {
            entity.addVelocity(new Vec3d(state.get(DIRECTION).getUnitVector().mul(0.2f)));
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx == null || ctx.getPlayer() == null) return getDefaultState();
        BlockState state = getDefaultState().with(DIRECTION, ctx.getPlayer().getHorizontalFacing());
        boolean powered = isPowered(ctx.getWorld(), ctx.getBlockPos(), state);
        return state.with(POWERED, powered);
    }

    @Override
    protected void neighborUpdate(BlockState state, @NotNull World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean powered = isPowered(world, pos, state);
            if (powered != state.get(POWERED)) {
                world.setBlockState(pos, state.cycle(POWERED), NOTIFY_ALL);
            }
        }
    }

    private boolean isPowered(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockState[] nears = {
                world.getBlockState(pos.north()),
                world.getBlockState(pos.south()),
                world.getBlockState(pos.west()),
                world.getBlockState(pos.east()),
                world.getBlockState(pos.up()),
                world.getBlockState(pos.down()),
        };
        boolean nearPowered = Arrays.stream(nears)
                .anyMatch(blockState -> blockState.isOf(ModBlocks.BELT)
                        && blockState.get(DIRECTION) == state.get(DIRECTION)
                        && world.getBlockState(pos.subtract(blockState.get(DIRECTION).getVector())) == blockState
                        //TODO: save power source in block entity, and delete the condition above
                        && blockState.get(POWERED));
        return world.getReceivedRedstonePower(pos) != 0 || nearPowered;
    }

    @Override
    protected void scheduledTick(@NotNull BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED) && !isPowered(world, pos, state)) {
            world.setBlockState(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BELT,
                (world, pos, state, beltBlockEntity)
                        -> beltBlockEntity.tick(world, pos, state));
    }

    static {
        POWERED = Properties.POWERED;
        DIRECTION = DirectionProperty.of("horizontal_direction",
                Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
    }
}
