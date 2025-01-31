package random_toys.zz_404;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ZZ =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "zz"), "main");
    public static final EntityModelLayer GILDED_BLACKSTONE_SCRAP =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "gilded_blackstone_scrap"), "main");
}
