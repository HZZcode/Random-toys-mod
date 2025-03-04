package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanishingDoorBlock extends BlockWithEntity {
    public static final MapCodec<VanishingDoorBlock> CODEC = createCodec(VanishingDoorBlock::new);
    public static final BooleanProperty POWERED;
    public static final BooleanProperty APPEAR;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public VanishingDoorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false).with(APPEAR, true));
    }

    @Override
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        builder.add(APPEAR);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VanishingDoorBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof VanishingDoorBlockEntity door) {
            door.open();
            return ActionResult.SUCCESS_NO_ITEM_USED;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.VANISHING_DOOR,
                (world, pos, state, vanishingDoorBlockEntity)
                        -> vanishingDoorBlockEntity.tick(world, pos, state));
    }

    @Override
    public void randomDisplayTick(@NotNull BlockState state, @NotNull World world, BlockPos pos, Random random) {
        int count = state.get(APPEAR) ? 3 : 1;
        for (int i = 0; i < count; i++) spawnEnderParticles(world, pos, random);
    }

    public static void spawnEnderParticles(@NotNull World world, @NotNull BlockPos pos, @NotNull Random random) {
        int j = random.nextInt(2) * 2 - 1;
        int k = random.nextInt(2) * 2 - 1;
        double d = pos.getX() + 0.5 + 0.25 * j;
        double e = pos.getY() + random.nextFloat();
        double f = pos.getZ() + 0.5 + 0.25 * k;
        double g = random.nextFloat() * j;
        double h = (random.nextFloat() - 0.5) * 0.125;
        double l = random.nextFloat() * k;
        world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, l);
    } //Copied from net.minecraft.block.EnderChestBlock.randomDisplayTick

    @Override
    protected VoxelShape getCollisionShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(APPEAR) ? VoxelShapes.fullCube() : VoxelShapes.empty();
    }

    @Override
    protected boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    @Override
    public boolean canMobSpawnInside(@NotNull BlockState state) {
        return !state.get(APPEAR);
    }

    static {
        POWERED = Properties.POWERED;
        APPEAR = BooleanProperty.of("appear");
    }
}
