package random_toys.zz_404;

import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
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

        RandomToys.log("Registering Dimensions");
    }
}
