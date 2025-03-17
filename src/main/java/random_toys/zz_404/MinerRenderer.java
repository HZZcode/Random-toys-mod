package random_toys.zz_404;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MinerRenderer extends MobEntityRenderer<MinerEntity, MinerModel<MinerEntity>> {
    public static final Identifier TEXTURE = Identifier.of(RandomToys.MOD_ID, "textures/entity/miner.png");

    public MinerRenderer(EntityRendererFactory.Context context) {
        super(context, new MinerModel<>(context.getPart(ModModelLayers.MINER)), 0.5f);
    }

    @Override
    public Identifier getTexture(MinerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(MinerEntity livingEntity, float f, float g, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(1.0F, 1.0F, 1.0F);
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
