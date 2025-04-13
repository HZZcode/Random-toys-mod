package random_toys.zz_404.registry;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.criteria.*;

import java.util.function.Consumer;

import static random_toys.zz_404.mixin_utils.MixinSets.PlayerTickBehaviours;

public class ModCriteria {
    public static final GetRandomCriterion GET_RANDOM = register("get_random", new GetRandomCriterion());
    public static final StrobeCriterion BUD_STROBE = register("bud_strobe", new StrobeCriterion());
    public static final FindBlockCriterion FIND_APPLE_LEAVES = register("find_apple_leaves", new FindBlockCriterion());
    public static final ApplePickingCriterion APPLE_PICKING = register("apple_picking", new ApplePickingCriterion());
    public static final ShearsHarvestCriterion SHEARS_HARVEST = register("shears_harvest", new ShearsHarvestCriterion());
    public static final FindEntityCriterion FIND_ZZ = register("find_zz", new FindEntityCriterion());
    public static final UseItemOnBlockCriterion BLACKSTONE_PROCESSING = register("blackstone_processing", new UseItemOnBlockCriterion());
    public static final UseItemOnBlockCriterion BLACK_BEDROCK_PROCESSING = register("black_bedrock_processing", new UseItemOnBlockCriterion());
    public static final EnterMazeCriterion ENTER_MAZE = register("enter_maze", new EnterMazeCriterion());
    public static final SolveMazeCriterion SOLVE_MAZE = register("solve_maze", new SolveMazeCriterion());
    public static final DestroyBlockCriterion DESTROY_BLACK_BEDROCK = register("destroy_black_bedrock", new DestroyBlockCriterion());
    public static final BlockTransferringCriterion BLACK_BEDROCK_TRANSFER = register("black_bedrock_transfer", new BlockTransferringCriterion());
    public static final TransferAllXpCriterion TRANSFER_ALL_XP = register("transfer_all_xp", new TransferAllXpCriterion());
    public static final ImitatorCriterion IMITATOR = register("imitator", new ImitatorCriterion());
    //TODO: challenge for travelling long distance with ender hopper

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return Registry.register(Registries.CRITERION, Identifier.of(RandomToys.MOD_ID, id), criterion);
    }

    public static void registerCriteria() {
        PlayerTickBehaviours.add(player ->
                triggerFindBlock(player, FIND_APPLE_LEAVES, ModBlocks.APPLE_LEAVES));

        RandomToys.log("Registering Criteria");
    }

    public static void triggerPlayers(@NotNull World world, BlockPos pos, double range,
                                      Consumer<ServerPlayerEntity> consumer) {
        world.getEntitiesByClass(ServerPlayerEntity.class,
                        new Box(pos).expand(range), player -> true).forEach(consumer);
    }

    public static void triggerFindBlock(@NotNull PlayerEntity player, FindBlockCriterion criterion, Block block) {
        World world = player.getWorld();
        if (world == null) return;
        if (player instanceof ServerPlayerEntity serverPlayer)
            for (int x = player.getBlockX() - 5; x <= player.getBlockX() + 5; x++)
                for (int y = player.getBlockY() - 5; y <= player.getBlockY() + 5; y++)
                    for (int z = player.getBlockZ() - 5; z <= player.getBlockZ() + 5; z++)
                        if (world.getBlockState(new BlockPos(x, y, z)).isOf(block))
                            criterion.trigger(serverPlayer);
    }
}
