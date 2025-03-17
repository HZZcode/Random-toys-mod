package random_toys.zz_404;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class ZZModel<T extends ZZEntity> extends SinglePartEntityModel<T> {
    private final ModelPart bb_main;

    public ZZModel(@NotNull ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static @NotNull TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -46.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 32).cuboid(3.0F, -30.0F, 3.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 32).cuboid(-5.0F, -30.0F, 3.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 32).cuboid(3.0F, -30.0F, -5.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 32).cuboid(-5.0F, -30.0F, -5.0F, 2.0F, 30.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return null;
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        bb_main.render(matrices, vertexConsumer, light, overlay, color);
    }
}
