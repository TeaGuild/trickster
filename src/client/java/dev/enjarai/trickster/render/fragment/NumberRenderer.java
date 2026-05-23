package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class NumberRenderer implements FragmentRenderer<NumberFragment> {

    public static final Identifier RING_0 = Trickster.id("textures/fragment/number/ring_0.png");
    public static final Identifier RING_45 = Trickster.id("textures/fragment/number/ring_45.png");
    public static final Identifier NEEDLE_0 = Trickster.id("textures/fragment/number/needle_0.png");
    public static final Identifier NEEDLE_45 = Trickster.id("textures/fragment/number/needle_45.png");
    public static final Identifier POINT = Trickster.id("textures/fragment/number/point.png");

    public static final float SPACING = 0.7f;

    public static final float MIN_SIZE = 4f;

    public static final double LOG_8 = Math.log(8);

    public static void renderNumber(double value, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        long digitCount = Math.max((int) (Math.log(Math.abs(value)) / LOG_8) + 1, 1);
        float size = (float) Math.pow(SPACING, digitCount) * radius;
        float rootSize = size;
        int lastRotation = value >= 0.0 ? 0 : 4;
        matrices.push();
        matrices.translate(x, y, 0);

        var minSize = delegator.inUI ? MIN_SIZE : 0.05;
        var POINT_RADIUS = 0.5;

        long i = (long) value;
        do {
            {
                var texture = (lastRotation & 1) == 1 ? RING_45 : RING_0;
                matrices.push();
                matrices.multiply(new Quaternionf(new AxisAngle4f((float) Math.PI / 2 * (lastRotation >> 1 & 3), 0, 0, 1)));

                CircleRenderer.drawTexturedQuad(
                    matrices, vertexConsumers, texture,
                    -size, size, -size, size,
                    0.001f,
                    delegator.getR(), delegator.getG(), delegator.getB(),
                    alpha * delegator.getCircleTransparency(), delegator.inUI
                );
                matrices.pop();
            }

            lastRotation += (int) Math.abs(i % 8);
            lastRotation %= 8;

            {
                var texture = (lastRotation & 1) == 1 ? NEEDLE_45 : NEEDLE_0;

                matrices.push();
                matrices.multiply(new Quaternionf(new AxisAngle4f((float) Math.PI / 2 * (lastRotation >> 1 & 3), 0, 0, 1)));

                CircleRenderer.drawTexturedQuad(
                    matrices, vertexConsumers, texture,
                    -size, size, -size, size,
                    0.001f,
                    delegator.getR(), delegator.getG(), delegator.getB(),
                    alpha * delegator.getCircleTransparency(), delegator.inUI
                );
                matrices.pop();
            }

            size /= SPACING;
            i /= 8;
        } while (i != 0);

        lastRotation = (value >= 0 ? 0 : 4) - (int) (value * 8);
        size = rootSize * SPACING;

        matrices.push();
        matrices.translate(size * POINT_RADIUS * Math.sin(Math.PI / 4 * lastRotation), -size * SPACING * Math.cos(Math.PI / 4 * lastRotation), 0);

        CircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, POINT,
            -size, size, -size, size,
            0.001f,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
        matrices.pop();

        lastRotation -= (int) (value * 64) % 8;
        size *= SPACING * SPACING;

        matrices.push();
        matrices.translate(size * POINT_RADIUS * Math.sin(Math.PI / 4 * lastRotation), -size * SPACING * Math.cos(Math.PI / 4 * lastRotation), 0);

        CircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, POINT,
            -size, size, -size, size,
            0.001f,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
        matrices.pop();

        matrices.pop();
    }

    @Override
    public void render(NumberFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, float forcedAlpha, Vec3d normal,
        float tickDelta,
        CircleRenderer delegator) {
        var value = fragment.number();
        renderNumber(value, matrices, vertexConsumers, x, y, radius, forcedAlpha, normal, tickDelta, delegator);
    }

    @Override
    public float getProportionalHeight(NumberFragment fragment) {
        return 0.5f;
    }
}
