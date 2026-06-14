package dev.enjarai.trickster.render;

import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.List;

import static dev.enjarai.trickster.render.CircleRenderer.*;
import static dev.enjarai.trickster.screen.scribing.CircleWidget.isCircleClickable;

public class PatternRenderer {
    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float width, float height, boolean shouldRenderDots, float alpha, CircleRenderer delegator
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, width, height, shouldRenderDots, 1f, alpha, delegator, null);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, float width, float height, boolean renderDots,
        float dotScale, float alpha, CircleRenderer delegator, @Nullable List<Byte> drawingPattern
    ) {
        var pixelSize = size / PART_PIXEL_RADIUS;
        var isDrawing = drawingPattern != null;

        var r = delegator.getR();
        var g = delegator.getG();
        var b = delegator.getB();
        var mouseX = delegator.getMouseX();
        var mouseY = delegator.getMouseY();
        var inUI = delegator.inUI;

        if (renderDots) {
            renderDots(
                pattern, matrices, vertexConsumers, x, y, size, mouseX, mouseY, r, g, b, inUI,
                delegator.isInEditor(), dotScale, alpha, drawingPattern
            );
        }

        for (var line : pattern.entries()) {
            var first = getPatternDotPosition(x, y, line.p1(), size);
            var second = getPatternDotPosition(x, y, line.p2(), size);
            drawGlyphLine(
                matrices, vertexConsumers, first, second, pixelSize, width, height, isDrawing,
                1, r, g, b, alpha, inUI, delegator.getRenderTime()
            );
        }

        if (isDrawing && delegator.getLineToMouse()) {
            var last = getPatternDotPosition(x, y, drawingPattern.getLast(), size);
            var now = new Vector2f((float) mouseX, (float) mouseY);
            drawGlyphLine(
                matrices, vertexConsumers, last, now, pixelSize, true, 1, r, g, b,
                alpha, inUI, delegator.getRenderTime()
            );
        }

    }

    public static void renderDots(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, double mouseX, double mouseY, float r, float g, float b,
        boolean inUI, boolean inEditor, float dotScale, float alpha, @Nullable List<Byte> drawingPattern
    ) {
        var partSize = size * PATTERN_TO_PART_RATIO;
        var pixelSize = size / PART_PIXEL_RADIUS;
        var isDrawing = drawingPattern != null;

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x, y, i, size);
            var isLinked = isDrawing ? drawingPattern.contains((byte) i) : pattern.contains(i);
            var currentDotScale = dotScale;

            if (inEditor && isInsideHitbox(pos, pixelSize, mouseX, mouseY) && isCircleClickable(partSize)) {
                currentDotScale *= 1.6f;
            } else if (!isLinked) {
                if (inEditor && isCircleClickable(partSize)) {
                    var mouseDistance = new Vector2f((float) (mouseX - pos.x), (float) (mouseY - pos.y)).length();
                    currentDotScale *= Math.clamp(size / mouseDistance - 0.2f, 0, 1);
                } else {
                    // Skip the dot if it's too small to click
                    continue;
                }
            }

            var dotSize = pixelSize * currentDotScale;

            drawFlatPolygon(
                matrices, vertexConsumers,
                pos.x - dotSize, pos.y - dotSize,
                pos.x - dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y - dotSize,
                0, (isDrawing && isLinked ? 0.8f : 1) * r,
                (isDrawing && isLinked ? 0.5f : 1) * g,
                1 * b,
                alpha,
                inUI
            );
        }
    }
}
