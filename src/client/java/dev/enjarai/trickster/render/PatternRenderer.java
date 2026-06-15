package dev.enjarai.trickster.render;

import dev.enjarai.trickster.Trickster;
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
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size,
        float width, float height, boolean shouldRenderDots, float alpha, float r, float g, float b, boolean inUI, float patternWidth
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, width, height, shouldRenderDots, false,
            1f, alpha, alpha, r, g, b, 0, 0, inUI, false, false, 0, patternWidth, null);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, boolean shouldRenderDots, boolean dotEmphasis,
        float dotScale, float alpha, float unlinkedAlpha, float r, float g, float b, double mouseX, double mouseY,
        boolean inUI, boolean inEditor, boolean lineToMouse, long renderTime, float patternWidth, @Nullable List<Byte> drawingPattern
    ) {
        renderPattern(pattern, matrices, vertexConsumers, x, y, size, 0.5f, 3f, shouldRenderDots, dotEmphasis, dotScale,
            alpha, unlinkedAlpha, r, g, b, mouseX, mouseY, inUI, inEditor, lineToMouse, renderTime, patternWidth, drawingPattern);
    }

    public static void renderPattern(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size,
        float width, float height, boolean shouldRenderDots, boolean dotEmphasis, float dotScale, float alpha, float unlinkedAlpha,
        float r, float g, float b, double mouseX, double mouseY, boolean inUI, boolean inEditor, boolean lineToMouse, long renderTime,
        float patternWidth, @Nullable List<Byte> drawingPattern

    ) {
        var pixelSize = size / PART_PIXEL_RADIUS;
        var isDrawing = drawingPattern != null;

        if (shouldRenderDots) {
            renderDots(
                pattern, matrices, vertexConsumers, x, y, size, mouseX, mouseY, r, g, b, inUI,
                inEditor, dotEmphasis, dotScale, alpha, unlinkedAlpha, drawingPattern
            );
        }

        for (var line : pattern.entries()) {
            var first = getPatternDotPosition(x, y, line.p1(), size);
            var second = getPatternDotPosition(x, y, line.p2(), size);
            drawGlyphLine(
                matrices, vertexConsumers, first, second, pixelSize, width, height, isDrawing,
                1, r, g, b, alpha, inUI, renderTime, patternWidth
            );
        }

        if (isDrawing && lineToMouse) {
            var last = getPatternDotPosition(x, y, drawingPattern.getLast(), size);
            var now = new Vector2f((float) mouseX, (float) mouseY);
            drawGlyphLine(
                matrices, vertexConsumers, last, now, pixelSize, true, 1, r, g, b,
                alpha, inUI, renderTime, patternWidth
            );
        }

    }

    public static void renderDots(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, double mouseX,
        double mouseY, float r, float g, float b, boolean inUI, boolean inEditor, float dotScale, float alpha
    ) {
        renderDots(Pattern.EMPTY, matrices, vertexConsumers, x, y, size, mouseX, mouseY, r, g, b, inUI, inEditor, false, dotScale, alpha, alpha, null);
    }

    public static void renderDots(
        Pattern pattern, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x, float y, float size, double mouseX, double mouseY, float r, float g, float b, boolean inUI, boolean inEditor,
        boolean dotEmphasis, float dotScale, float alpha, float unlinkedAlpha, @Nullable List<Byte> drawingPattern
    ) {
        var partSize = size * PATTERN_TO_PART_RATIO;
        var pixelSize = size / PART_PIXEL_RADIUS;
        boolean isDrawing = drawingPattern != null;
        boolean[] dotTerminalStatus = pattern.dotTerminalStatus();

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x, y, i, size);
            var isLinked = isDrawing ? drawingPattern.contains((byte) i) : pattern.contains(i);
            var currentDotScale = dotScale;
            var currentDotR = r;
            var currentDotG = g;
            var currentDotB = b;
            var currentDotAlpha = isLinked ? alpha : unlinkedAlpha;

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

            if (dotTerminalStatus[i] && dotEmphasis) {
                currentDotR = Trickster.CONFIG.dotEmphasisColor().red();
                currentDotG = Trickster.CONFIG.dotEmphasisColor().green();
                currentDotB = Trickster.CONFIG.dotEmphasisColor().blue();
            }

            drawFlatPolygon(
                matrices, vertexConsumers,
                pos.x - dotSize, pos.y - dotSize,
                pos.x - dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y - dotSize,
                0, (isDrawing && isLinked ? 0.8f : 1) * currentDotR,
                (isDrawing && isLinked ? 0.5f : 1) * currentDotG,
                1 * currentDotB,
                currentDotAlpha, inUI
            );
        }
    }

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, boolean isDrawing, float tone, float r, float g,
        float b, float opacity, boolean inUI, long renderTime, float patternWidth) {
        drawGlyphLine(matrices, vertexConsumers, last, now, pixelSize, 0.5f, 3, isDrawing, tone, r, g, b, opacity, inUI, renderTime, patternWidth);
    }

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, float width, float height, boolean isDrawing,
        float tone, float r, float g, float b, float opacity, boolean inUI, long renderTime, float patternWidth) {
        if (last.distance(now) < pixelSize * 6) {
            return;
        }

        pixelSize *= patternWidth;

        var parallelVec = new Vector2f(last.y - now.y, now.x - last.x).normalize().mul(pixelSize * width);
        var directionVec = new Vector2f(last.x - now.x, last.y - now.y).normalize().mul(pixelSize * height * Trickster.CONFIG.patternEdgeGap());

        drawFlatPolygon(
            matrices, vertexConsumers,
            last.x - parallelVec.x - directionVec.x, last.y - parallelVec.y - directionVec.y,
            last.x + parallelVec.x - directionVec.x, last.y + parallelVec.y - directionVec.y,
            now.x + parallelVec.x + directionVec.x, now.y + parallelVec.y + directionVec.y,
            now.x - parallelVec.x + directionVec.x, now.y - parallelVec.y + directionVec.y,
            0, (isDrawing ? 0.8f : tone) * r, (isDrawing ? 0.5f : tone) * g, tone * b,
            opacity, inUI
        );
    }
}
