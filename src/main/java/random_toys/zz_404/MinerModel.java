package random_toys.zz_404;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class MinerModel<T extends MinerEntity> extends EntityModel<T> {
    private final ModelPart bb_main;

    public MinerModel(@NotNull ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static @NotNull TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 16).cuboid(1.0F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(6, 16).cuboid(-2.0F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -8.0F, -2.0F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 9).cuboid(-2.0F, -11.0F, -2.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData hand2_r1 = bb_main.addChild("hand2_r1", ModelPartBuilder.create().uv(12, 16).cuboid(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -6.0F, -1.0F, 1.7925F, 0.2926F, 0.1109F));

        ModelPartData hand1_r1 = bb_main.addChild("hand1_r1", ModelPartBuilder.create().uv(16, 9).cuboid(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -6.0F, -1.0F, 1.7905F, -0.2615F, -0.0117F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        bb_main.render(matrices, vertexConsumer, light, overlay, color);
    }
}