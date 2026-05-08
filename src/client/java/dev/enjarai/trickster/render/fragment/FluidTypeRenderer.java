package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.slot.FluidTypeFragment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class FluidTypeRenderer implements FragmentRenderer<FluidTypeFragment> {

    public static final Identifier TEXTURE = Trickster.id("textures/fragment/fluid_type.png");

    @Override
    public void render(FluidTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var stack = fragment.fluid().getBucketItem().getDefaultStack();
        matrices.push();
        matrices.translate(x, y, 0.0 * size * (delegator.inUI ? 0.3 : -0.3));
        ItemTypeRenderer.renderItem(stack, ModelTransformationMode.GUI, matrices, vertexConsumers, x, y, size, delegator, 14, false);
        matrices.pop();
        CircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, TEXTURE,
            x - size / 2, x + size / 2, y - size / 2, y + size / 2,
            0,
            delegator.getR(), delegator.getG(), delegator.getB(),
            delegator.getCircleTransparency(), delegator.inUI
        );
    }

    @Override
    public float getProportionalHeight(FluidTypeFragment fragment) {
        return 0.4f;
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
