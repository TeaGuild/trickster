package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.slot.ItemTypeFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ItemTypeRenderer implements FragmentRenderer<ItemTypeFragment> {

    public static final Identifier TEXTURE = Trickster.id("textures/gui/item_slot.png");

    @Override
    public void render(ItemTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var stack = fragment.item().getDefaultStack();
        matrices.push();
        matrices.translate(x, y, 0.0 * size * (delegator.inUI ? 0.3 : -0.3));
        renderItem(stack, ModelTransformationMode.GUI, matrices, vertexConsumers, x, y, size, delegator, 14, false);
        matrices.pop();
        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, TEXTURE,
            x - size / 2, x + size / 2, y - size / 2, y + size / 2,
            0,
            delegator.getR(), delegator.getG(), delegator.getB(),
            delegator.getCircleTransparency(), delegator.inUI
        );
    }

    public static void renderItem(ItemStack stack, ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size,
        SpellCircleRenderer delegator, int light, boolean alwaysFlatLight) {
        var bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(
            stack, MinecraftClient.getInstance().world,
            null, 0
        );

        matrices.push();

        float time = System.currentTimeMillis() % 1048576 / 1000f;

        matrices.scale(size, delegator.inUI ? -size : size, size);
        matrices.scale(0.8f, 0.8f, 0.8f);
        if (!delegator.inUI) {
            var mat = new Matrix4f(matrices.peek().getPositionMatrix());
            matrices.loadIdentity();

            matrices.multiplyPositionMatrix(new Matrix4f()
                .translate(mat.getTranslation(new Vector3f()))
                .scale(mat.getScale(new Vector3f()))
                .rotateY(time)
            );

            //matrices.multiplyPositionMatrix(mat);
        } else {
            matrices.multiply(new Quaternionf(new AxisAngle4f(time, 0, 1, 0)));
        }

        boolean notSideLit = (alwaysFlatLight || !bakedModel.isSideLit()) && delegator.inUI;
        if (notSideLit) {
            if (vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
                immediate.draw();
            }

            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(
            stack, transformationMode,
            false, matrices, vertexConsumers,
            LightmapTextureManager.pack(0, light), OverlayTexture.DEFAULT_UV,
            bakedModel
        );
        if (delegator.inUI && vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }

        if (notSideLit) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();

    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
