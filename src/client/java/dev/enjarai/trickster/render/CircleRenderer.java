package dev.enjarai.trickster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.ModAtlases;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.fragment.FragmentRenderer;
import dev.enjarai.trickster.render.fragment.PatternGlyphRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import io.wispforest.owo.ui.core.Color;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.client.render.RenderPhase.*;

public class CircleRenderer {
    public static final Identifier CIRCLE_TEXTURE = Trickster.id("fragment/spell/circle_48");
    public static final Identifier CIRCLE_TEXTURE_WORLD = Trickster.id("fragment/spell/circle_48_world");
    public static final Identifier CIRCLE_TEXTURE_HIGHLIGHT = Trickster.id("fragment/spell/circle_48_highlight");
    public static final Identifier FRAGMENT_ATLAS = Trickster.id("textures/atlas/fragment.png");
    public static final Identifier[] LOADING_TEXTURES = new Identifier[] {
        Trickster.id("fragment/spell/spinner_1"),
        Trickster.id("fragment/spell/spinner_2"),
    };
    public static final float PATTERN_TO_PART_RATIO = 2.5f;
    public static final int PART_PIXEL_RADIUS = 24;
    public static final int CLICK_HITBOX_SIZE = 5;

    public static final RenderLayer CIRCLE_TEXTURE_LAYER = RenderLayer.getEntityTranslucent(CIRCLE_TEXTURE);
    public static final RenderLayer CIRCLE_TEXTURE_WORLD_LAYER = RenderLayer.getEntityTranslucent(CIRCLE_TEXTURE_WORLD);
    public static final RenderLayer UI_GLYPH_LAYER = RenderLayer.of(
        "trickster:ui_glyph", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS,
        786432, RenderLayer.MultiPhaseParameters.builder()
            .program(GUI_PROGRAM)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .depthTest(LEQUAL_DEPTH_TEST)
            .cull(DISABLE_CULLING)
            .build(false)
    );
    public static final RenderLayer WORLD_GLYPH_LAYER = RenderLayer.of(
        "trickster:world_glyph", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS,
        1536, RenderLayer.MultiPhaseParameters.builder()
            .program(COLOR_PROGRAM)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .depthTest(LEQUAL_DEPTH_TEST)
            .cull(DISABLE_CULLING)
            .build(false)
    );

    public static final VertexConsumerProvider.Immediate VERTEX_CONSUMERS = VertexConsumerProvider.immediate(
        Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
            map.put(CIRCLE_TEXTURE_LAYER, new BufferAllocator(CIRCLE_TEXTURE_LAYER.getExpectedBufferSize()));
            map.put(CIRCLE_TEXTURE_WORLD_LAYER, new BufferAllocator(CIRCLE_TEXTURE_WORLD_LAYER.getExpectedBufferSize()));
            map.put(UI_GLYPH_LAYER, new BufferAllocator(UI_GLYPH_LAYER.getExpectedBufferSize()));
            map.put(WORLD_GLYPH_LAYER, new BufferAllocator(WORLD_GLYPH_LAYER.getExpectedBufferSize()));
        }),
        new BufferAllocator(2048)
    );

    public static final float FADE_SPEED = 0.2f;

    public final boolean inUI;
    public final boolean inEditor;
    public final int recursions;

    private double mouseX;
    private double mouseY;
    private boolean lineToMouse;
    private long renderTime;

    private Identifier circleTexture;

    private float r = 1f, g = 1f, b = 1f;
    private float circleTransparency = 1f;
    private int loading = -1;
    private boolean considerInners = true;

    public double textFade = 0;

    public CircleRenderer(boolean inUI, boolean inEditor, int recursions) {
        this.inUI = inUI;
        this.inEditor = inEditor;
        this.recursions = recursions;

        this.circleTexture = inUI && inEditor ? CIRCLE_TEXTURE : CIRCLE_TEXTURE_WORLD;
    }

    public void setMousePosition(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void setLineToMouse(boolean lineToMouse) {
        this.lineToMouse = lineToMouse;
    }

    public boolean getLineToMouse() {
        return lineToMouse;
    }

    public void setRenderTime(long renderTime) {
        this.renderTime = renderTime;
    }

    public void setCircleTexture(@Nullable Identifier circleTexture) {
        if (circleTexture == null) {
            this.circleTexture = inUI && inEditor ? CIRCLE_TEXTURE : CIRCLE_TEXTURE_WORLD;
        } else {
            this.circleTexture = circleTexture;
        }
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setLoading(int loading) {
        this.loading = loading;
    }

    public void setConsiderInners(boolean considerInners) {
        this.considerInners = considerInners;
    }

    public boolean isInEditor() {
        return inEditor;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public long getRenderTime() {
        return renderTime;
    }

    public void setCircleTransparency(float circleTransparency) {
        this.circleTransparency = circleTransparency;
    }

    public float getCircleTransparency() {
        return circleTransparency;
    }

    public void renderCircle(
        MatrixStack matrices, SpellPart entry,
        double x, double y, double radius, double startingAngle, float delta,
        float alpha, float forcedAlpha, Vec3d normal, @Nullable List<Byte> drawingPattern
    ) {
        renderCircle(matrices, entry, x, y, radius, startingAngle, delta, alpha, forcedAlpha, normal, drawingPattern, 0);
    }

    private void renderCircle(
        MatrixStack matrices, SpellPart entry,
        double x, double y, double radius, double startingAngle, float delta,
        float alpha, float forcedAlpha, Vec3d normal, @Nullable List<Byte> drawingPattern, int recursions
    ) {
        var vertexConsumers = VERTEX_CONSUMERS;

        var renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(entry.glyph.type()));
        if (renderer == null || renderer.renderCircle() || drawingPattern != null) {
            drawTexturedQuad(
                matrices, vertexConsumers, circleTexture,
                (float) (x - radius), (float) (x + radius), (float) (y - radius), (float) (y + radius),
                0,
                r, g, b, alpha * circleTransparency, inUI
            );
        }
        if (considerInners && entry.glyph instanceof SpellPart glyph) {
            if (recursions < this.recursions) {
                renderCircle(
                    matrices, glyph, x, y, radius / 3,
                    startingAngle, delta, alpha / 2, forcedAlpha / 2, normal,
                    drawingPattern, recursions + 1
                );
            }
        } else {
            if (loading < 0) {
                matrices.push();
                drawSide(
                    matrices, vertexConsumers, (float) x, (float) y, (float) radius,
                    alpha, forcedAlpha, normal, delta, entry.glyph, drawingPattern
                );
                matrices.pop();
            } else {
                drawTexturedQuad(
                    matrices, vertexConsumers, LOADING_TEXTURES[loading],
                    (float) (x - radius), (float) (x + radius), (float) (y - radius), (float) (y + radius),
                    0,
                    r, g, b, alpha * circleTransparency, inUI
                );
            }
        }

        int partCount = entry.partCount();

        drawDivider(matrices, vertexConsumers, x, y, startingAngle, radius, partCount, alpha);

        matrices.push();
        if (!inUI) {
            matrices.translate(0, 0, -radius / 8f);
        }
        int i = 0;
        for (var child : entry.getSubParts()) {
            var angle = entry.subAngle(i, startingAngle);
            var subX = x + (radius * Math.cos(angle));
            var subY = y + (radius * Math.sin(angle));

            if (recursions < this.recursions) {
                renderCircle(
                    matrices, child, subX, subY, entry.subRadius(radius),
                    angle, delta, alpha / 3 * 2, forcedAlpha / 3 * 2, normal,
                    drawingPattern, recursions + 1
                );
            }

            i++;
        }
        matrices.pop();
    }

    protected void drawDivider(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        double x, double y, double startingAngle, double radius,
        int partCount, double alpha
    ) {
        if (partCount == 0) return;

        var pixelSize = radius / PART_PIXEL_RADIUS;
        var offset = getDividerOffset(radius, startingAngle, partCount);

        float lineX = (float) (x + offset.x);
        float lineY = (float) (y + offset.y);

        var toCenterVec = new Vector2f(offset.x, offset.y).normalize();
        var perpendicularVec = new Vector2f(toCenterVec).perpendicular();
        toCenterVec.mul((float) (pixelSize * 6));
        perpendicularVec.mul((float) (pixelSize * 0.5f));

        Color dividerColor = Trickster.CONFIG.subcircleDividerPinColor();

        drawFlatPolygon(
            matrices, vertexConsumers,
            lineX - perpendicularVec.x + toCenterVec.x * 0.5f, lineY - perpendicularVec.y + toCenterVec.y * 0.5f,
            lineX + perpendicularVec.x + toCenterVec.x * 0.5f, lineY + perpendicularVec.y + toCenterVec.y * 0.5f,
            lineX + perpendicularVec.x - toCenterVec.x, lineY + perpendicularVec.y - toCenterVec.y,
            lineX - perpendicularVec.x - toCenterVec.x, lineY - perpendicularVec.y - toCenterVec.y,
            0, dividerColor.red() * r, dividerColor.green() * g, dividerColor.blue() * b,
            dividerColor.alpha() * (float) alpha, inUI
        );
    }

    public Vector3f getDividerOffset(double distance, double startingAngle, int partCount) {
        var lineAngle = partCount == 0 ? startingAngle : startingAngle + (2 * Math.PI) / partCount * -0.5 - (Math.PI / 2);

        float lineX = (float) (distance * Math.cos(lineAngle));
        float lineY = (float) (distance * Math.sin(lineAngle));

        return new Vector3f(lineX, lineY, 0);
    }

    private void drawSide(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y,
        float radius, float alpha, float forcedAlpha, Vec3d normal, float delta,
        Fragment glyph, @Nullable List<Byte> drawingPattern
    ) {
        float patternRadius = radius / PATTERN_TO_PART_RATIO;

        alpha *= (float) (1 - textFade * 0.5f);
        forcedAlpha *= (float) (1 - textFade * 0.5f);
        var renderText = true;

        var isDrawing = drawingPattern != null;

        Pattern patternList = null;

        if (isDrawing) {
            patternList = Pattern.from(drawingPattern);
        } else if (glyph instanceof PatternGlyph pattern) {
            patternList = pattern.pattern();
        } else if (glyph instanceof SpellPart && !considerInners) {
            patternList = Pattern.EMPTY;
        }

        if (patternList != null) {
            if (!(glyph instanceof PatternGlyph pattern) || pattern.pattern().isEmpty()) {
                renderText = false;
            }

            PatternGlyphRenderer.renderPatternGlyph(patternList, matrices, vertexConsumers, x, y, patternRadius, alpha, this, drawingPattern);
        } else {
            //noinspection rawtypes
            FragmentRenderer renderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(glyph.type()));

            var renderDots = true;

            if (renderer != null) {
                //noinspection unchecked
                renderer.render(glyph, matrices, vertexConsumers, x, y, radius, alpha, forcedAlpha, normal, delta, this);
                renderDots = renderer.renderRedrawDots();
            } else {
                FragmentRenderer.renderAsText(glyph, matrices, vertexConsumers, x, y, radius, alpha);
            }

            if (inUI && renderDots) {
                PatternRenderer.renderDots(matrices, vertexConsumers, x, y, patternRadius, mouseX, mouseY, r, g, b, true, inEditor, Trickster.CONFIG.dotSize(), 0.25f);
            }
        }

        if (renderText && textFade > 0.05) {
            matrices.push();
            matrices.translate(0, 0, radius * 2f);
            FragmentRenderer.renderAsText(glyph, matrices, vertexConsumers, x, y, radius, (float) textFade);
            matrices.pop();
        }
    }

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, boolean isDrawing, float tone, float r, float g,
        float b, float opacity, boolean inUI, long renderTime) {
        drawGlyphLine(matrices, vertexConsumers, last, now, pixelSize, 0.5f, 3, isDrawing, tone, r, g, b, opacity, inUI, renderTime);
    }

    public static void drawGlyphLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vector2f last, Vector2f now, float pixelSize, float width, float height, boolean isDrawing,
        float tone, float r, float g, float b, float opacity, boolean inUI, long renderTime) {
        if (last.distance(now) < pixelSize * 6) {
            return;
        }

        pixelSize *= Trickster.CONFIG.patternSize();

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

    public static Vector2f getPatternDotPosition(float x, float y, int i, float radius) {
        float xSign = (float) (i % 3 - 1);
        float ySign = (float) (i / 3 - 1);

        if (xSign != 0 && ySign != 0) {
            xSign *= 0.7f;
            ySign *= 0.7f;
        }

        return new Vector2f(
            x + xSign * radius,
            y + ySign * radius
        );
    }

    public static boolean isInsideHitbox(Vector2f pos, float pixelSize, double mouseX, double mouseY) {
        var hitboxSize = CLICK_HITBOX_SIZE * pixelSize;
        return mouseX >= pos.x - hitboxSize && mouseX <= pos.x + hitboxSize &&
            mouseY >= pos.y - hitboxSize && mouseY <= pos.y + hitboxSize;
    }

    public static void drawTexturedQuad(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture,
        float x1, float x2, float y1, float y2, float z, float r, float g, float b,
        float alpha, boolean inGui
    ) {
        //        if (inUI) {
        //            RenderSystem.setShaderTexture(0, texture);
        //            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //            RenderSystem.enableBlend();
        //            RenderSystem.enableDepthTest();
        //            RenderSystem.setShaderColor(r, g, b, alpha);
        //            Matrix4f position = matrices.peek().getPositionMatrix();
        //            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        //            bufferBuilder.vertex(position, x1, y1, z).texture((float) 0, (float) 0);
        //            bufferBuilder.vertex(position, x1, y2, z).texture((float) 0, (float) 1);
        //            bufferBuilder.vertex(position, x2, y2, z).texture((float) 1, (float) 1);
        //            bufferBuilder.vertex(position, x2, y1, z).texture((float) 1, (float) 0);
        //            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        //            RenderSystem.setShaderColor(1, 1, 1, 1);
        //        } else {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        var matrixEntry = matrices.peek();
        var position = matrixEntry.getPositionMatrix();
        var sprite = ModAtlases.Fragment.INSTANCE.getSprite(texture);
        var vertexConsumer = sprite.getTextureSpecificVertexConsumer(
            vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(FRAGMENT_ATLAS)));

        var minV = 0;
        var minU = 0;
        var maxV = 1;
        var maxU = 1;

        if (!inGui) {
            // We have to do this weird non-chaining here due to https://bugs.mojang.com/browse/MC/issues/MC-263524.
            // Unlike some **other** mods (looking at you supplementaries) we wont just fix this globally using a mixin,
            // since others might be relying on this weird behaviour.
            vertexConsumer.vertex(position, x1, y1, z);
            vertexConsumer.texture(minU, minV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x1, y2, z);
            vertexConsumer.texture(minU, maxV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x2, y2, z);
            vertexConsumer.texture(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
            vertexConsumer.vertex(position, x2, y1, z);
            vertexConsumer.texture(maxU, minV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(0, 1, 0);
        } else {
            vertexConsumer.vertex(position, x1, y1, z);
            vertexConsumer.texture(minU, minV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x1, y2, z);
            vertexConsumer.texture(minU, maxV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x2, y2, z);
            vertexConsumer.texture(maxU, maxV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
            vertexConsumer.vertex(position, x2, y1, z);
            vertexConsumer.texture(maxU, minV).overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).color(r, g, b, alpha).normal(-1, 0, 0);
        }
    }

    public static void drawFlatPolygon(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
        float z, float r, float g, float b, float alpha, boolean inUI
    ) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer;
        vertexConsumer = vertexConsumers.getBuffer(getGlyphLayer(inUI));
        vertexConsumer.vertex(matrix4f, x1, y1, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x2, y2, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x3, y3, z).color(r, g, b, alpha);
        vertexConsumer.vertex(matrix4f, x4, y4, z).color(r, g, b, alpha);
        if (Trickster.CONFIG.outlinePatterns()) {
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1));
            vertexConsumer.vertex(matrix4f, x1, y1, z).color(r, g, b, alpha);
            vertexConsumer.vertex(matrix4f, x2, y2, z).color(r, g, b, alpha);
            vertexConsumer.vertex(matrix4f, x3, y3, z).color(r, g, b, alpha);
            vertexConsumer.vertex(matrix4f, x4, y4, z).color(r, g, b, alpha);
            vertexConsumer.vertex(matrix4f, x1, y1, z).color(r, g, b, alpha);
        }
    }

    public static RenderLayer getGlyphLayer(boolean inUI) {
        return inUI ? UI_GLYPH_LAYER : WORLD_GLYPH_LAYER;
    }
}
