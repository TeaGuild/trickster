package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

public class BlockTypeRenderer implements FragmentRenderer<BlockTypeFragment> {
    static float HEIGHT = 0.4f;

    @Override
    public void render(BlockTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        CircleRenderer delegator) {
        var block = fragment.block();

        matrices.push();

        float time = System.currentTimeMillis() % 1048576 / 1000f;

        matrices.translate(x, y, delegator.inUI ? size * 0.5 : 0);
        matrices.scale(size, delegator.inUI ? -size : size, size);
        if (delegator.inUI) {
            matrices.scale(1f, 1f, 0.01f);
            matrices.multiply(new Quaternionf(new AxisAngle4f(0.5f, 1, 0, 0)));
        } else {
            var mat = new Matrix4f(matrices.peek().getPositionMatrix());
            matrices.loadIdentity();

            matrices.multiplyPositionMatrix(new Matrix4f()
                .translate(mat.getTranslation(new Vector3f()))
                .scale(mat.getScale(new Vector3f()))
            );
        }
        matrices.multiply(new Quaternionf(new AxisAngle4f(time, 0, 1, 0)));

        matrices.scale(0.8f, 0.8f, 0.8f);

        if (!renderBlock(block.getDefaultState(), matrices, vertexConsumers)) {
            matrices.pop();
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
            return;
        }

        for (var side : Direction.values()) {
            matrices.push();
            matrices.multiply(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), side.getUnitVector()));
            SpellCircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, ItemTypeRenderer.TEXTURE,
                -0.55f, 0.55f, -0.55f, 0.55f,
                -0.55f,
                delegator.getR(), delegator.getG(), delegator.getB(),
                delegator.getCircleTransparency(), delegator.inUI
            );

            matrices.pop();
        }

        matrices.pop();
    }

    @Override
    public boolean doubleSided() {
        return false;
    }

    @Override
    public float getProportionalHeight(BlockTypeFragment fragment) {
        var block = fragment.block();

        if (Item.BLOCK_ITEMS.containsKey(block)) {
            return HEIGHT;
        } else {
            return FragmentRenderer.getTextProportionalHeight(fragment.asFormattedText());
        }
    }

    @Override
    public float getProportionalWidth(BlockTypeFragment fragment) {
        var block = fragment.block();

        if (Item.BLOCK_ITEMS.containsKey(block)) {
            return HEIGHT;
        } else {
            return FragmentRenderer.getTextProportionalWidth();
        }
    }

    public static boolean renderBlock(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var block = state.getBlock();
        var manager = MinecraftClient.getInstance().getBlockRenderManager();
        var model = manager.getModel(state);
        var rand = net.minecraft.util.math.random.Random.create();
        var modelEmpty = model.getQuads(state, null, rand).isEmpty();
        for (var dir : Direction.values()) {
            if (!modelEmpty) break;
            modelEmpty = model.getQuads(state, dir, rand).isEmpty();
        }
        if (modelEmpty) {
            if (Item.BLOCK_ITEMS.containsKey(block)) {
                var stack = Item.BLOCK_ITEMS.get(block).getDefaultStack();
                var bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(
                    stack, MinecraftClient.getInstance().world,
                    null, 0
                );
                MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack, ModelTransformationMode.NONE,
                    false, matrices, vertexConsumers,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                    bakedModel
                );
            } else {
                return false;
            }
        } else {
            matrices.translate(-0.5, -0.5, -0.5);
            manager.getModelRenderer().render(
                MinecraftClient.getInstance().world,
                model,
                state,
                new BlockPos(0, 1000, 0),
                matrices,
                vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state)),
                false,
                rand,
                state.getRenderingSeed(new BlockPos(0, 0, 0)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            );
            matrices.translate(0.5, 0.5, 0.5);
        }
        return true;
    }
}
