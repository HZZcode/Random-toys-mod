package random_toys.zz_404;

import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ModDimensions {
    public static void registerDimensions() {
        CustomPortalBuilder.beginPortal()
                .frameBlock(Blocks.GILDED_BLACKSTONE)
                .lightWithFluid(Fluids.LAVA)
                .destDimID(Identifier.of(RandomToys.MOD_ID, "the_gilded"))
                .tintColor(0x160f10)
                .onlyLightInOverworld()
                .flatPortal()
                .registerPortal();

        CustomPortalBuilder.beginPortal()
                .frameBlock(ModBlocks.BLACK_BEDROCK)
                .lightWithItem(Items.NETHER_STAR)
                .destDimID(Identifier.of(RandomToys.MOD_ID, "black_void"))
                .returnDim(Identifier.of(RandomToys.MOD_ID, "the_gilded"), true)
                .tintColor(0x000000)
                .flatPortal()
                .registerPortal();

        RandomToys.log("Registering Dimensions");
    }
}
