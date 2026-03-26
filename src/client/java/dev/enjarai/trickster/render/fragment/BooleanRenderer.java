package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BooleanRenderer implements FragmentRenderer<BooleanFragment> {
    public static final Identifier TRUE = Trickster.id("textures/fragment/boolean/true.png");
    public static final Identifier FALSE = Trickster.id("textures/fragment/boolean/false.png");;

    @Override
    public void render(BooleanFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        Identifier texture = fragment.bool ? TRUE : FALSE;
        size *= 2f / 3f;
        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, texture,
            x - size, x + size, y - size, y + size,
            0.001f,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }
}
