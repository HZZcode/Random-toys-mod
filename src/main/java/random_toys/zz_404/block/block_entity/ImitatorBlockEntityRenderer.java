package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

public class ImitatorBlockEntityRenderer implements BlockEntityRenderer<ImitatorBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public ImitatorBlockEntityRenderer(BlockEntityRendererFactory.@NotNull Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(@NotNull ImitatorBlockEntity imitator, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (imitator.block != null) {
            BlockState state = imitator.block.getDefaultState();
            matrices.push();
            matrices.translate(0, 0, 0);
            blockRenderManager.getModelRenderer().render(
                    imitator.getWorld(),
                    blockRenderManager.getModel(state),
                    state,
                    imitator.getPos(),
                    matrices,
                    vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state, false)),
//                    vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state)),
                    false,
                    Random.create(),
                    state.getRenderingSeed(imitator.getPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }
    }
}
