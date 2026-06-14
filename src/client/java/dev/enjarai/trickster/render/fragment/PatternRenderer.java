package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.List;

import static dev.enjarai.trickster.render.CircleRenderer.*;
import static dev.enjarai.trickster.screen.scribing.CircleWidget.isCircleClickable;

public class PatternRenderer implements FragmentRenderer<PatternGlyph> {
    @Override
    public void render(PatternGlyph fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, float forcedAlpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        renderPattern(fragment.pattern(), matrices, vertexConsumers, x, y, size / PATTERN_TO_PART_RATIO, alpha, delegator, null);
    }

    @Override
    public float getProportionalHeight(PatternGlyph fragment) {
        float height = 1f / PATTERN_TO_PART_RATIO;
        float dotHeight = 1f / PART_PIXEL_RADIUS / PATTERN_TO_PART_RATIO;
        return height + dotHeight;
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float alpha, CircleRenderer delegator
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, alpha, delegator, null);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float alpha, CircleRenderer delegator, @Nullable List<Byte> drawingPattern
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, 0.5f, 3f, true, alpha, delegator, drawingPattern);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float width, float height, boolean renderDots,
        float alpha, CircleRenderer delegator
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, width, height, renderDots, alpha, delegator, null);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float width, float height, boolean renderDots,
        float alpha, CircleRenderer delegator, @Nullable List<Byte> drawingPattern
    ) {
        var partSize = size * PATTERN_TO_PART_RATIO;
        var pixelSize = size / PART_PIXEL_RADIUS;
        var isDrawing = drawingPattern != null;

        var r = delegator.getR();
        var g = delegator.getG();
        var b = delegator.getB();
        var mouseX = delegator.getMouseX();
        var mouseY = delegator.getMouseY();
        var inUI = delegator.inUI;

        if (renderDots)
            for (int i = 0; i < 9; i++) {
                var pos = getPatternDotPosition(x, y, i, size);
                var isLinked = isDrawing ? drawingPattern.contains((byte) i) : pattern.contains(i);
                float dotScale = Trickster.CONFIG.dotSize();

                if (delegator.isInEditor() && isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(partSize)) {
                    dotScale *= 1.6f;
                } else if (!isLinked) {
                    if (delegator.isInEditor() && isCircleClickable(partSize)) {
                        var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                        dotScale *= Math.clamp(size / mouseDistance - 0.2f, 0, 1);
                    } else {
                        // Skip the dot if it's too small to click
                        continue;
                    }
                }

                var dotSize = pixelSize * dotScale;

                drawFlatPolygon(
                    matrices, vertexConsumers,
                    pos.x - dotSize, pos.y - dotSize,
                    pos.x - dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y - dotSize,
                    0, (isDrawing && isLinked ? 0.8f : 1) * r,
                    (isDrawing && isLinked ? 0.5f : 1) * g,
                    1 * b,
                    Trickster.CONFIG.patternOpacity() * alpha,
                    inUI
                );
            }

        for (var line : pattern.entries()) {
            var first = getPatternDotPosition(x, y, line.p1(), size);
            var second = getPatternDotPosition(x, y, line.p2(), size);
            drawGlyphLine(
                matrices, vertexConsumers, first, second, pixelSize, width, height, isDrawing,
                1, r, g, b, Trickster.CONFIG.patternOpacity() * alpha, inUI, delegator.getRenderTime()
            );
        }

        if (isDrawing && delegator.getLineToMouse()) {
            var last = getPatternDotPosition(x, y, drawingPattern.getLast(), size);
            var now = new Vector2f((float) mouseX, (float) mouseY);
            drawGlyphLine(
                matrices, vertexConsumers, last, now, pixelSize, true, 1, r, g, b,
                Trickster.CONFIG.patternOpacity() * alpha, inUI, delegator.getRenderTime()
            );
        }

    }

    @Override
    public boolean renderRedrawDots() {
        return false;
    }
}
