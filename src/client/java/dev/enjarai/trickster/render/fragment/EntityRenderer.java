package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityRenderer implements FragmentRenderer<EntityFragment> {

    public static int depth = 0;
    public static final int MAX_DEPTH = 4;

    @Override
    public void render(EntityFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var entityOp = fragment.getEntity(MinecraftClient.getInstance().world, true);

        if (entityOp.isPresent()) {
            renderEntity(entityOp.get(), matrices, vertexConsumers, x, y, size, tickDelta, delegator);
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
        }
    }

    public static void renderEntity(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float tickDelta, SpellCircleRenderer delegator) {
        if (depth >= MAX_DEPTH) return;
        depth++;
        matrices.push();
        matrices.translate(x, y, delegator.inUI ? 0.01f * size : 0);
        if (delegator.inUI) {
            matrices.scale(size, -size, 0.01f * size);
            matrices.multiply(new Quaternionf(new AxisAngle4f(0.5f, 1, 0, 0)));
        } else {
            matrices.scale(size, size, size);
            var mat = new Matrix4f(matrices.peek().getPositionMatrix());
            matrices.loadIdentity();

            matrices.multiplyPositionMatrix(new Matrix4f()
                .translate(mat.getTranslation(new Vector3f()))
                .scale(mat.getScale(new Vector3f()))
            );
        }

        matrices.scale(0.8f, 0.8f, 0.8f);
        var scale = Math.max(entity.getHeight(), entity.getWidth());
        scale = 1f / scale;
        matrices.scale(scale, scale, scale);
        matrices.translate(0, -entity.getHeight() / 2, 0);

        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        dispatcher.render(
            entity, 0, 0, 0, 0, tickDelta,
            matrices,
            layer -> vertexConsumers.getBuffer(RenderLayer.getGlint().equals(layer) ? RenderLayer.getEntityCutout(Identifier.ofVanilla("")) : layer), LightmapTextureManager.pack(0, 15)
        );

        matrices.pop();
        depth = Math.max(depth - 1, 0);
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
