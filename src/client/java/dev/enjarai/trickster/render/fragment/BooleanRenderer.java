package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BooleanRenderer implements FragmentRenderer<BooleanFragment> {
    public static final Identifier TRUE = Trickster.id("fragment/boolean/true");
    public static final Identifier FALSE = Trickster.id("fragment/boolean/false");

    @Override
    public void render(BooleanFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, float forcedAlpha, Vec3d normal,
        float tickDelta,
        CircleRenderer delegator) {
        Identifier texture = fragment.bool ? TRUE : FALSE;
        size *= 2f / 3f;
        CircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, texture,
            x - size, x + size, y - size, y + size,
            0.001f,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }

    @Override
    public float getProportionalHeight(BooleanFragment fragment) {
        return 0.6f;
    }
}
