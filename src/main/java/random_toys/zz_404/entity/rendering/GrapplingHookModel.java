package random_toys.zz_404.entity.rendering;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GrapplingHookModel extends EntityModel<Entity> {
	private final ModelPart bb_main;

	public GrapplingHookModel(@NotNull ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static @NotNull TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 8).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(16, 28).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.3562F));
		ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(24, 14).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		ModelPartData cube_r3 = bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));
		ModelPartData cube_r4 = bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(8, 22).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));
		ModelPartData cube_r5 = bb_main.addChild("cube_r5", ModelPartBuilder.create().uv(0, 22).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F, 3.1416F));
		ModelPartData cube_r6 = bb_main.addChild("cube_r6", ModelPartBuilder.create().uv(16, 14).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, -0.7854F, -3.1416F));
		ModelPartData cube_r7 = bb_main.addChild("cube_r7", ModelPartBuilder.create().uv(16, 0).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -1.5708F));
		ModelPartData cube_r8 = bb_main.addChild("cube_r8", ModelPartBuilder.create().uv(8, 8).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		bb_main.render(matrices, vertexConsumer, light, overlay, color);
	}
}