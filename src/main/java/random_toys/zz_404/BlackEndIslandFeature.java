package random_toys.zz_404;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.NotNull;

public class BlackEndIslandFeature extends Feature<DefaultFeatureConfig> {
    public BlackEndIslandFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(@NotNull FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        int f = random.nextInt(2) + 3;
        for (int x = -f; x <= f; x++)
            for (int y = -f; y <= f; y++)
                for (int z = -f; z <= f; z++)
                    if (x * x + y * y + z * z <= (f + 0.5) * (f + 0.5))
                        setBlockState(structureWorldAccess, blockPos.add(x, y, z),
                                ModBlocks.BLACK_BEDROCK.getDefaultState());
        return true;
    }
}
