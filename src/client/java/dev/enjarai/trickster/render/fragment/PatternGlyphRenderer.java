package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.render.PatternRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.enjarai.trickster.render.CircleRenderer.*;

public class PatternGlyphRenderer implements FragmentRenderer<PatternGlyph> {
    @Override
    public void render(PatternGlyph fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, float forcedAlpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        renderPatternGlyph(fragment.pattern(), matrices, vertexConsumers, x, y, size / PATTERN_TO_PART_RATIO, alpha, delegator, null);
    }

    @Override
    public float getProportionalHeight(PatternGlyph fragment) {
        float height = 1f / PATTERN_TO_PART_RATIO;
        float dotHeight = 1f / PART_PIXEL_RADIUS / PATTERN_TO_PART_RATIO;
        return height + dotHeight;
    }

    public static void renderPatternGlyph(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float alpha, CircleRenderer delegator, @Nullable List<Byte> drawingPattern
    ) {
        PatternRenderer.renderPattern(
            pattern, matrices, vertexConsumers, x, y, size, 0.5f, 3f, true,
            Trickster.CONFIG.dotSize(), alpha * Trickster.CONFIG.patternOpacity(), delegator, drawingPattern
        );
    }

    @Override
    public boolean renderRedrawDots() {
        return false;
    }
}
