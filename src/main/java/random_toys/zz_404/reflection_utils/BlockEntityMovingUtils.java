package random_toys.zz_404.reflection_utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BlockEntityMovingUtils {
    public static boolean tryMoveBlockEntity(@NotNull World world, BlockPos from, BlockPos to) {
        try {
            moveBlockEntity(world, from, to);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            RandomToys.error(e.toString());
            return false;
        }
    }

    private static void moveBlockEntity(@NotNull World world, BlockPos from, BlockPos to)
            throws NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        BlockEntity entity = world.getBlockEntity(from);
        if (entity == null) return;
        setBlockEntityPos(entity, to);
        world.addBlockEntity(entity);
        removeBlockEntity(world, from);
    }

    private static void setBlockEntityPos(@NotNull BlockEntity entity, @NotNull BlockPos pos)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?> Vec3iClass = Vec3i.class;
        Field xField = Vec3iClass.getDeclaredField("x");
        xField.setAccessible(true);
        xField.set(entity.getPos(), pos.getX());
        Field yField = Vec3iClass.getDeclaredField("y");
        yField.setAccessible(true);
        yField.set(entity.getPos(), pos.getY());
        Field zField = Vec3iClass.getDeclaredField("z");
        zField.setAccessible(true);
        zField.set(entity.getPos(), pos.getZ());
    }

    @SuppressWarnings("unchecked")
    private static void removeBlockEntity(@NotNull World world, BlockPos pos)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        WorldChunk chunk = world.getWorldChunk(pos);
        Class<?> ChunkClass = Chunk.class;
        Class<?> WorldChunkClass = WorldChunk.class;
        Field blockEntitiesField = ChunkClass.getDeclaredField("blockEntities");
        blockEntitiesField.setAccessible(true);
        BlockEntity entity = ((Map<BlockPos, BlockEntity>) blockEntitiesField.get(chunk)).remove(pos);
        if (chunk.getWorld() instanceof ServerWorld server && entity != null) {
            Method removeGameEventListenerMethod = WorldChunkClass.getDeclaredMethod("removeGameEventListener",
                    BlockEntity.class, ServerWorld.class);
            removeGameEventListenerMethod.setAccessible(true);
            removeGameEventListenerMethod.invoke(chunk, entity, server);
        }
        Method removeBlockEntityTickerMethod = WorldChunkClass.getDeclaredMethod("removeBlockEntityTicker", BlockPos.class);
        removeBlockEntityTickerMethod.setAccessible(true);
        removeBlockEntityTickerMethod.invoke(chunk, pos);
    } //without marking as removed
}
