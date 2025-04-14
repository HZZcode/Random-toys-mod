package random_toys.zz_404.entity.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.entity.BedrockDemonEntity;
import random_toys.zz_404.registry.ModModelLayers;

public class BedrockDemonRenderer extends MobEntityRenderer<BedrockDemonEntity, BedrockDemonModel<BedrockDemonEntity>> {
    public static final Identifier TEXTURE = Identifier.of(RandomToys.MOD_ID, "textures/entity/bedrock_demon.png");

    public BedrockDemonRenderer(EntityRendererFactory.Context context) {
        super(context, new BedrockDemonModel<>(context.getPart(ModModelLayers.BEDROCK_DEMON)), 0.5f);
    }

    @Override
    public Identifier getTexture(BedrockDemonEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BedrockDemonEntity livingEntity, float f, float g, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(1.0F, 1.0F, 1.0F);
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}