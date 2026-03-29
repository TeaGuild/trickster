package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.DimensionFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class DimensionRenderer implements FragmentRenderer<DimensionFragment> {
    public static final String TEXTURE_PATTERN = "textures/fragment/dimension/%s/%s.png";
    public static final Identifier FALLBACK = Trickster.id("textures/fragment/dimension/missing.png");

    @Override
    public void render(DimensionFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var id = fragment.world().getValue();
        Identifier texture = Identifier.of(Trickster.MOD_ID, String.format(TEXTURE_PATTERN, id.getNamespace(), id.getPath()));
        var tex = MinecraftClient.getInstance().getTextureManager().getTexture(texture);
        if (tex instanceof NativeImageBackedTexture) {
            //Use mod icon in fallback?
            texture = FALLBACK;
            matrices.push();
            matrices.translate(0, 0, 0.002f * size);
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
            matrices.pop();
        }
        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, texture,
            x - size, x + size, y - size, y + size,
            0.001f * size,
            delegator.getR(), delegator.getG(), delegator.getB(),
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }
}
