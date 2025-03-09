package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
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
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeltBlock extends BlockWithEntity {
    public static final MapCodec<BeltBlock> CODEC = createCodec(BeltBlock::new);
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 15, 16);
    public static final BooleanProperty POWERED;
    public static final DirectionProperty DIRECTION;
    public static final int speed = 5; //block per second

    public BeltBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false).with(DIRECTION, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeltBlockEntity(pos, state);
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
    protected void onEntityCollision(@NotNull BlockState state, @NotNull World world, BlockPos pos, Entity entity) {
        var direction = new Vec3d(state.get(DIRECTION).getUnitVector());
        if (world.getBlockState(pos).get(POWERED) && hasEffects(entity))
            if (entity.getVelocity().dotProduct(direction) < speed)
                entity.addVelocity(direction.multiply(0.2f));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx == null || ctx.getPlayer() == null) return getDefaultState();
        BlockState state = getDefaultState().with(DIRECTION, ctx.getPlayer().getHorizontalFacing());
        boolean powered = isPowered(ctx.getWorld(), ctx.getBlockPos(), state);
        return state.with(POWERED, powered);
    }

    public static boolean isPowered(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof BeltBlockEntity belt)) return false;
        BlockPos[] nears = {pos.north(), pos.south(), pos.west(), pos.east()};
        if (belt.powerSource != null) {
            if (world.getReceivedRedstonePower(belt.powerSource) != 0) return true;
            else belt.powerSource = null;
        }
        if (world.getReceivedRedstonePower(pos) != 0) {
            belt.powerSource = pos;
            return true;
        }
        ArrayList<BlockPos> nearPowered = Arrays.stream(nears)
                .filter(pos1 -> world.getBlockState(pos1).isOf(ModBlocks.BELT)
                        && world.getBlockState(pos1).get(DIRECTION) == state.get(DIRECTION)
                        && world.getBlockState(pos1).get(POWERED))
                .collect(Collectors.toCollection(ArrayList::new));
        if (!nearPowered.isEmpty()
                && world.getBlockEntity(nearPowered.getFirst()) instanceof BeltBlockEntity near
                && near.powerSource != null
                && world.getReceivedRedstonePower(near.powerSource) != 0) {
            belt.powerSource = near.powerSource.mutableCopy();
            return true;
        }
        return false;
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
