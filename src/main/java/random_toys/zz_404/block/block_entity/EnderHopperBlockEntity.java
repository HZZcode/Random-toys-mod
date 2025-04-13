package random_toys.zz_404.block.block_entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModCriteria;
import random_toys.zz_404.registry.ModGamerules;
import random_toys.zz_404.block.BeltBlock;
import random_toys.zz_404.block.EnderHopperBlock;
import random_toys.zz_404.reflection_utils.BlockMovingUtils;

public class EnderHopperBlockEntity extends BlockEntity {
    public BlockPos linked;
    public RegistryKey<World> dimension;

    public EnderHopperBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        linked = null;
        dimension = null;
    }

    public EnderHopperBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.ENDER_HOPPER, blockPos, blockState);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Linked", NbtElement.INT_ARRAY_TYPE)) {
            int[] pos = nbt.getIntArray("Linked");
            if (pos.length == 3) linked = new BlockPos(pos[0], pos[1], pos[2]);
        }
        if (nbt.contains("Dim", NbtElement.STRING_TYPE)) {
            String dimName = nbt.getString("Dim");
            dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimName));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (linked != null) {
            int[] pos = {linked.getX(), linked.getY(), linked.getZ()};
            nbt.putIntArray("Linked", pos);
        }
        if (dimension != null) {
            nbt.putString("Dim", dimension.getValue().toString());
        }
    }

    public boolean nullCheck() {
        if (world instanceof ServerWorld server && dimension != null)
            world = server.getServer().getWorld(dimension);
        if (world == null) return true;
        if (world instanceof ServerWorld server && dimension == null)
            dimension = server.getRegistryKey();
        return linked == null;
    }

    public void tick(@NotNull World world, BlockPos pos, @NotNull BlockState state) {
        if (nullCheck()) return;
        world.setBlockState(pos, state.with(EnderHopperBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getBlockEntity(linked) instanceof EnderHopperBlockEntity hopper
                && pos.equals(hopper.linked) && state.get(BeltBlock.POWERED)
                && world instanceof ServerWorld) {
            if (world.getTime() % 4 == 0 && canMoveTo(pos.up(), linked.down())) {
                copyBlock(world, pos.up(), linked.down());
                moveBlock(world, pos.up(), linked.down());
            }
            if (world.getTime() % 4 == 0) {
                for (Entity entity : world.getEntitiesByClass(Entity.class, Box.from(Vec3d.of(pos)).expand(3),
                        entity -> BeltBlock.isStepping(pos, entity) && !entity.isSpectator())) {
                    Vec3d vec = linked.toBottomCenterPos().subtract(0, entity.getHeight(), 0);
                    if (entity.getPos().subtract(vec).length() >= 500
                            && entity instanceof ServerPlayerEntity player)
                        ModCriteria.ENDER_HOPPER_TELEPORT.trigger(player);
                    entity.requestTeleport(vec.x, vec.y, vec.z);
                }
            }
        }
    }

    private boolean canMoveTo(@NotNull BlockPos from, @NotNull BlockPos to) {
        return world != null && (world.getBlockState(to).isAir()
                || world.getBlockState(to).isReplaceable()
                || BlockMovingUtils.canMerge(world, from, to));
    }

    private void copyBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (!world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        BlockMovingUtils.copyBlock(world, from, to);
    }

    private void moveBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (!world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        boolean destroy = world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_DESTROY_BLOCK_ENTITY);
        BlockMovingUtils.moveBlock(world, from, to, pos, destroy, true);
    }
}
