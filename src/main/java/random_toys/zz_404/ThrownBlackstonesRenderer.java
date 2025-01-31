package random_toys.zz_404;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class ThrownBlackstonesRenderer extends FlyingItemEntityRenderer<AbstractThrownBlackstoneEntity> {
    public ThrownBlackstonesRenderer(EntityRendererFactory.Context context) {
        super(context, 1.5f, true);
        this.shadowRadius = 0.5f;
    }
}
