package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModBlocks;
import random_toys.zz_404.block.block_entity.ImitatorBlockEntity;
import random_toys.zz_404.registry.ModCriteria;

public class ImitatorBlock extends BlockWithEntity {
    public static final MapCodec<ImitatorBlock> CODEC = createCodec(ImitatorBlock::new);
    public static final BooleanProperty HAS_BLOCK;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public ImitatorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(HAS_BLOCK, false));
    }

    @Override
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HAS_BLOCK);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ImitatorBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(@NotNull BlockState state) {
        return state.get(HAS_BLOCK) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.fullCube();
            VoxelShape shape = block.getDefaultState().getOutlineShape(world, pos);
            if (shape.isEmpty()) return VoxelShapes.fullCube();
            return shape;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.fullCube();
            return block.getDefaultState().getCollisionShape(world, pos);
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, @NotNull BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.fullCube();
            return block.getDefaultState().getCullingShape(world, pos);
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getSidesShape(BlockState state, @NotNull BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.fullCube();
            return block.getDefaultState().getSidesShape(world, pos);
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, @NotNull BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.empty();
            return block.getDefaultState().getRaycastShape(world, pos);
        }
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            Block block = imitator.block;
            if (block == null || block == ModBlocks.IMITATOR) return VoxelShapes.empty();
            return block.getDefaultState().getCameraCollisionShape(world, pos, context);
        }
        return VoxelShapes.empty();
    }

    @Override
    protected ItemActionResult onUseWithItem(@NotNull ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world != null && !world.isClient && stack.getItem() instanceof BlockItem blockItem
                && world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator
                && imitator.block == null) {
            if (stack.isOf(ModBlocks.IMITATOR.asItem()) && player instanceof ServerPlayerEntity serverPlayer)
                ModCriteria.IMITATOR.trigger(serverPlayer);
            imitator.block = blockItem.getBlock();
            imitator.updateListeners();
            return ItemActionResult.CONSUME;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
        if (world != null && !world.isClient && player.isSneaking()
                && world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator) {
            imitator.block = null;
            imitator.updateListeners();
            return ActionResult.SUCCESS_NO_ITEM_USED;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world != null && world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator
                && imitator.block != null)
            imitator.block = null;
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void randomDisplayTick(@NotNull BlockState state, @NotNull World world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof ImitatorBlockEntity imitator
                && imitator.block != null && imitator.block != ModBlocks.IMITATOR)
            imitator.block.randomDisplayTick(imitator.block.getDefaultState(), world, pos, random);
        for (int i = 0; i < 3; i++) VanishingDoorBlock.spawnEnderParticles(world, pos, random);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.IMITATOR,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    static {
        HAS_BLOCK = BooleanProperty.of("has_block");
    }
}
