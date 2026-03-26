package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.TypeFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TypeRenderer implements FragmentRenderer<TypeFragment> {
    public static final String TEXTURE_PATTERN = "textures/fragment/type/%s.png";

    @Override
    public void render(TypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var id = FragmentType.REGISTRY.getId(fragment.typeType());
        Identifier texture = Identifier.of(id.getNamespace(), String.format(TEXTURE_PATTERN, id.getPath()));
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
