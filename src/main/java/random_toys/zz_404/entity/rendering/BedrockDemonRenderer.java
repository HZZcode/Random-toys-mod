package random_toys.zz_404.entity.rendering;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.entity.BedrockDemonEntity;
import random_toys.zz_404.registry.ModModelLayers;

public class BedrockDemonRenderer extends MobEntityRenderer<BedrockDemonEntity, BedrockDemonModel<BedrockDemonEntity>> {
    public static final Identifier TEXTURE = Identifier.of(RandomToys.MOD_ID, "textures/entity/bedrock_demon.png");
    private final ItemRenderer itemRenderer;

    public BedrockDemonRenderer(EntityRendererFactory.Context context) {
        super(context, new BedrockDemonModel<>(context.getPart(ModModelLayers.BEDROCK_DEMON)), 0.5f);
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public Identifier getTexture(BedrockDemonEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull BedrockDemonEntity demon, float f, float g, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2 * demon.getHealthPercentage() + 1,
                2 * demon.getHealthPercentage() + 1,
                2 * demon.getHealthPercentage() + 1);
        super.render(demon, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
        renderRing(demon, matrixStack, vertexConsumerProvider, i);
    }

    private void renderRing(@NotNull BedrockDemonEntity demon, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double r = 2 * demon.getHealthPercentage() + 1;
        for (int j = 0; j < 10; j++) {
            int k = (int) (4 * r);
            double theta = Math.PI * j / 5 + demon.getWorld().getTime() % (2 * k) * Math.PI / k;
            matrixStack.push();
            matrixStack.translate(r * Math.cos(theta),
                    0.5 * (2 * demon.getHealthPercentage() + 1) - 0.75,
                    r * Math.sin(theta));
            matrixStack.scale(3, 3, 3);
            itemRenderer.renderItem(new ItemStack(Blocks.BEDROCK),
                    ModelTransformationMode.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack,
                    vertexConsumerProvider, demon.getWorld(), demon.getId());
            matrixStack.pop();
        }
    }
}