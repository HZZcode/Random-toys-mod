package random_toys.zz_404.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.registry.ModItems;
import random_toys.zz_404.registry.ModModelLayers;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {
    private final GrapplingHookModel model;
    public static final Identifier TEXTURE = Identifier.of(RandomToys.MOD_ID, "textures/entity/grappling_hook.png");

    public GrapplingHookRenderer(EntityRendererFactory.Context context) {
        super(context);
        model = new GrapplingHookModel(context.getPart(ModModelLayers.GRAPPLING_HOOK));
    }

    protected int getBlockLight(GrapplingHookEntity hook, BlockPos blockPos) {
        return 15;
    }

    public void render(@NotNull GrapplingHookEntity hook, float f, float g, @NotNull MatrixStack matrixStack, @NotNull VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0, -0.625, 0);
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        model.render(matrixStack, vertexConsumerProvider.getBuffer(getRenderLayer(hook)), i, 0, 0xFFFFFFFF);
        matrixStack.pop();

        matrixStack.push();
        Entity owner = hook.getOwner();
        if (owner instanceof PlayerEntity player) {
            Vec3d hookPos = hook.getPos();
            float h = player.getHandSwingProgress(g);
            float j = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
            Vec3d ownerPos = getHandPos(player, j, g);
            Vector3f vec = ownerPos.subtract(hookPos).toVector3f();
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
            MatrixStack.Entry entry2 = matrixStack.peek();
            for (int o = 0; o <= 16; o++)
                renderLine(vec.x, vec.y, vec.z, vertexConsumer2, entry2,
                        (float) o / 16.0f, (float) (o + 1) / 16.0f);
        }
        matrixStack.pop();

        super.render(hook, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Nullable
    protected RenderLayer getRenderLayer(GrapplingHookEntity entity) {
        Identifier identifier = this.getTexture(entity);
        return RenderLayer.getItemEntityTranslucentCull(identifier);
    }

    private static void renderLine(float x, float y, float z, @NotNull VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        i /= l;
        j /= l;
        k /= l;
        buffer.vertex(matrices, f, g, h).color(Colors.GRAY).normal(matrices, i, j, k);
    } //Copied from net.minecraft.client.render.entity.FishingBobberEntityRenderer.renderFishingLine

    private Vec3d getHandPos(@NotNull PlayerEntity player, float f, float tickDelta) {
        int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(ModItems.GRAPPLING_HOOK)) {
            i = -i;
        }

        if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = 960.0 / (double) this.dispatcher.gameOptions.getFov().getValue();
            Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition(i * 0.525F, -0.1F).multiply(m).rotateY(f * 0.5F).rotateX(-f * 0.7F);
            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float g = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
            double d = MathHelper.sin(g);
            double e = MathHelper.cos(g);
            float h = player.getScale();
            double j = i * 0.35 * h;
            double k = 0.8 * h;
            float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
            return player.getCameraPosVec(tickDelta).add(-e * j - d * k, l - 0.45 * h, -d * j + e * k);
        }
    } //Copied from net.minecraft.client.render.entity.FishingBobberEntityRenderer.getHandPos

    public Identifier getTexture(GrapplingHookEntity hook) {
        return TEXTURE;
    }
}

