package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.Random;

import static net.minecraft.util.math.ColorHelper.Argb.*;

public class VoidRenderer implements FragmentRenderer<VoidFragment> {
    static final Identifier TEXTURE = Trickster.id("textures/fragment/void/square.png");
    static final Identifier CORE = Trickster.id("textures/fragment/void/core.png");

    static int tick = 0;
    static float lastDt = 0;

    @Override
    public void render(VoidFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        float time = System.currentTimeMillis() % 1048576 / 50f;
        float r = 0.3f, g = 0, b = 1, a = 1;
        size *= 0.5f;
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(size, size, delegator.inUI ? size : -size);

        var rand = new Random();

        for (int i = 0; i < 8; i++) {
            rand.setSeed(((int) (time / 5) + i) * 1000);
            var rotv = rand.nextFloat();
            var rot = rand.nextFloat() * (float) Math.PI * 2;
            rotv *= 10;
            var age = (7 - i + time / 5 % 1) / 8;
            var scale = 1 - (2 * age - 1) * (2 * age - 1);

            var fade = age * age;

            matrices.push();
            matrices.multiply(new Quaternionf(new AxisAngle4f(rot + age * rotv, 0, 0, 1)));
            matrices.scale(scale, scale, 1);
            SpellCircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, TEXTURE,
                -1, 1, -1, 1,
                age * 0.01f,
                MathHelper.lerp(fade, r, 1),
                MathHelper.lerp(fade, g, 1),
                MathHelper.lerp(fade, b, 1),
                alpha * delegator.getCircleTransparency() * a, delegator.inUI
            );
            matrices.pop();
        }

        for (int i = 0; i < 2; i++) {
            rand.setSeed(((int) (time / 20) - i) * 1000);
            var rotv = rand.nextFloat() * 0.5f;
            var rot = rand.nextFloat() * 0.5f * (float) Math.PI * 2;
            rotv *= 10;
            var age = (i + (time / 20) % 1) / 2;
            var scale = 1 - (2 * age - 1) * (2 * age - 1);

            matrices.push();
            matrices.multiply(new Quaternionf(new AxisAngle4f(rot + age * rotv, 0, 0, 1)));
            matrices.scale(scale, scale, 1);
            SpellCircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, Trickster.id("textures/fragment/void/spike.png"),
                -1, 1, -1, 1,
                age * 0.01f + 0.01f,
                1, 1, 1,
                alpha * delegator.getCircleTransparency() * a, delegator.inUI
            );
            matrices.pop();
        }

        matrices.multiply(new Quaternionf(new AxisAngle4f(time / 50, 0, 0, 1)));
        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, CORE,
            -1, 1, -1, 1,
            0.02f,
            delegator.getR(), delegator.getG(), delegator.getB(),
            1 * delegator.getCircleTransparency() * a, delegator.inUI
        );
        matrices.pop();
    }
}
