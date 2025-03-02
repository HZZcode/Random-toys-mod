package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ExperienceCollectorBlockEntity extends BlockEntity {
    public static final int max = 2000;
    public int experience = 0;

    public ExperienceCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ExperienceCollectorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.EXPERIENCE_COLLECTOR, pos, state);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("experience", experience);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        experience = nbt.getInt("experience");
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        world.setBlockState(pos, state.with(ExperienceCollectorBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getReceivedRedstonePower(pos) == 0) {
            Box box = new Box(pos).expand(10.0);
            for (ExperienceOrbEntity orb : world.getEntitiesByClass(ExperienceOrbEntity.class, box,
                    orbEntity -> true)) {
                if (experience == max) break;
                experience += orb.getExperienceAmount();
                orb.discard();
            }
        }
        if (experience < 0) experience = 0;
        if (experience > max) experience = max;
        world.setBlockState(pos, state.with(ExperienceCollectorBlock.LEVEL,
                min(ExperienceCollectorBlock.max,
                        (ExperienceCollectorBlock.Xp2Level(experience) + 1) / 3)));
        BlockEntity[] nears = {
                world.getBlockEntity(pos.up()),
                world.getBlockEntity(pos.down()),
                world.getBlockEntity(pos.north()),
                world.getBlockEntity(pos.south()),
                world.getBlockEntity(pos.west()),
                world.getBlockEntity(pos.east()),
        };
        BlockEntity near = nears[world.random.nextInt(6)];
        if (world.getReceivedRedstonePower(pos) == 0 && near instanceof TransferableBlockEntity entity)
            if (experience == max) {
                int before = experience;
                boolean success = entity
                        .transformSingle(Items.BOOK, ExperienceCollectorBlock.enchantBook(world, this));
                if (!success) experience = before;
            }
    }

    public void transform(PlayerEntity player, int amount) {
        if (amount > 0) {
            int xp = min(player.totalExperience, amount, max - experience);
            player.addExperience(-xp);
            experience += xp;
        }
        if (amount < 0) {
            int xp = min(-amount, experience);
            player.addExperience(xp);
            experience -= xp;
        }
    }

    public static int min(int... nums) {
        return Arrays.stream(nums).min().orElse(0);
    }

    public static int max(int... nums) {
        return Arrays.stream(nums).max().orElse(0);
    }
}
