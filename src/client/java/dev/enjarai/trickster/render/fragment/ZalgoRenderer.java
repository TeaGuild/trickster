package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class ZalgoRenderer implements FragmentRenderer<ZalgoFragment> {
    public static final String TEXTURE_PATTERN = "textures/fragment/zalgo/piece_%d.png";
    public static final Random rand = new Random();

    @Override
    public void render(ZalgoFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        rand.setSeed(MinecraftClient.getInstance().world.getTime() * 6767);
        for (int i = 0; i < 4; i++) {
            Identifier texture = Trickster.id(String.format(TEXTURE_PATTERN, i));
            var xo = x + (rand.nextFloat() - 0.5f) * size * 0.2f;
            var yo = y + (rand.nextFloat() - 0.5f) * size * 0.2f;
            CircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, texture,
                xo - size, xo + size, yo - size, yo + size,
                0.001f,
                delegator.getR(), delegator.getG(), delegator.getB(),
                alpha * delegator.getCircleTransparency(), delegator.inUI
            );
        }
        var matrix4f = matrices.peek();
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1));
        for (int i = 0; i < 64; i++) {
            var r = size * 0.5f * i / 64f;
            var th = rand.nextFloat() * (float) Math.PI * 2;
            var x1 = x + (float) Math.cos(th) * r;
            var y1 = y + (float) Math.sin(th) * r;
            vertexConsumer.vertex(matrix4f, x1, y1, 0.001f).color(0.7f, 0.7f, 0.7f, alpha);
        }
    }

    @Override
    public float getProportionalHeight(ZalgoFragment fragment) {
        return 1;
    }

    @Override
    public boolean renderCircle() {
        return false;
    }
}
