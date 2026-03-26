package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityRenderer implements FragmentRenderer<EntityFragment> {
    @Override
    public void render(EntityFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var entityOp = fragment.getEntity(MinecraftClient.getInstance().world, true);

        if (entityOp.isPresent()) {
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

            matrices.push();
            var entity = entityOp.get();

            matrices.scale(0.8f, 0.8f, 0.8f);
            var scale = Math.max(entity.getHeight(), entity.getWidth());
            scale = 1f / scale;
            matrices.scale(scale, scale, scale);
            matrices.translate(0, -entity.getHeight() / 2, 0);

            float time = System.currentTimeMillis() % 1048576 / 1000f;

            entity.setGlowing(true);
            var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            dispatcher.render(
                entity, 0, 0, 0, 0, tickDelta,
                matrices, vertexConsumers, LightmapTextureManager.pack(0, 15)
            );

            matrices.pop();
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
        }
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
