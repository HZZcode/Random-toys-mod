package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModCriteria;
import random_toys.zz_404.registry.ModGamerules;

import java.util.List;

public class MazeCoreBlockEntity extends BlockEntity {
    private boolean activated = false;

    public MazeCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MazeCoreBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.MAZE_CORE, pos, state);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("activated", activated);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("activated")) activated = nbt.getBoolean("activated");
    }

    public void tick(@NotNull World world, @NotNull BlockPos pos, BlockState ignored) {
        MazeGenerator generator = new MazeGenerator(world, pos);
        if (world.isClient) return;
        if (activated) {
            for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, generator.getRangeBox(),
                    player -> true)) {
                if (!player.isCreative() && !player.isSpectator() && world.getTime() % 240 == 0
                        && world.getGameRules().getBoolean(ModGamerules.MAZE_MINING_FATIGUE))
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE,
                            300, 2, false, false));
                if (player instanceof ServerPlayerEntity serverPlayer)
                    ModCriteria.ENTER_MAZE.trigger(serverPlayer);
            }
            List<ServerPlayerEntity> corePlayers = world.getEntitiesByClass(ServerPlayerEntity.class,
                    new Box(pos).expand(10, 4, 10), player -> !player.isSpectator());
            if (!corePlayers.isEmpty()) {
                corePlayers.forEach(ModCriteria.SOLVE_MAZE::trigger);
                world.setBlockState(pos, Blocks.BEACON.getDefaultState());
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        world.setBlockState(pos.add(i, -1, j), Blocks.NETHERITE_BLOCK.getDefaultState());
                world.spawnEntity(new EndCrystalEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
            }
        }
        else {
            generator.generate();
            activated = true;
        }
    }
}
