package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3dc;

public class VectorRenderer implements FragmentRenderer<VectorFragment> {

    public static final Identifier RING = Trickster.id("textures/fragment/vector_ring.png");

    public static final double angle = Math.PI * 11 / 12;

    @Override
    public void render(VectorFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        alpha = MathHelper.lerp(delegator.textFade, 1f, alpha);

        var vec = fragment.vector();

        renderVector(vec, matrices, vertexConsumers, x, y, radius, alpha, normal, tickDelta, delegator);

    }

    public static void renderVector(Vector3dc vec, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {

        var vs = new double[] { vec.x(), vec.y(), vec.z() };

        matrices.push();
        matrices.scale(1, 1, delegator.inUI ? 1 : -1);

        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, RING,
            x - radius * NumberRenderer.SPACING, x + radius * NumberRenderer.SPACING, y - radius * NumberRenderer.SPACING, y + radius * NumberRenderer.SPACING,
            0,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );

        for (int i = 0; i < 3; i++) {
            var angle = Math.PI * 11 / 12 + Math.PI / 1.5 * i;
            NumberRenderer.renderNumber(vs[i], matrices, vertexConsumers,
                x + (float) Math.cos(angle) * radius / 2.5f, y + (float) Math.sin(angle) * radius / 2.5f,
                radius / 2, alpha, normal, tickDelta, delegator);
        }
        matrices.pop();
    }
}
