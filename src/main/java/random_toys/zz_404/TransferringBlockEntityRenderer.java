package random_toys.zz_404;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TransferringBlockEntityRenderer implements BlockEntityRenderer<TransferringBlockEntity> {
    private int ticks = 0;
    private final ItemRenderer itemRenderer;

    public TransferringBlockEntityRenderer(BlockEntityRendererFactory.@NotNull Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(@NotNull TransferringBlockEntity entity, float tickDelta, @NotNull MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ticks = (ticks + 1) % 360;
        Item item = entity.getItem();
        World world = entity.getWorld();
        if (world == null || !world.getBlockState(entity.getPos().up()).isAir()) return;
        matrices.push();
        matrices.translate(0.5, 1.25 + 0.05 * Math.sin(Math.toRadians(ticks * 2)), 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ticks));
        itemRenderer.renderItem(new ItemStack(item), ModelTransformationMode.GROUND, 150,
                overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
    }
}
