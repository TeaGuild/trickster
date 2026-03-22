package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

public class BlockTypeRenderer implements FragmentRenderer<BlockTypeFragment> {
    @Override
    public void render(BlockTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        var block = fragment.block();

        matrices.push();

        float time = System.currentTimeMillis() % 1048576 / 1000f;

        matrices.translate(x, y, delegator.inUI ? size * 0.5 : 0);
        matrices.scale(size, delegator.inUI ? -size : size, size);
        if (delegator.inUI) {
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

        matrices.translate(-0.5, -0.5, -0.5);

        var state = block.getDefaultState();
        var manager = MinecraftClient.getInstance().getBlockRenderManager();
        manager.getModelRenderer().render(
            MinecraftClient.getInstance().world,
            manager.getModel(state),
            state,
            new BlockPos(0, 0, 0),
            matrices,
            vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state)),
            false,
            net.minecraft.util.math.random.Random.create(),
            state.getRenderingSeed(new BlockPos(0, 0, 0)),
            LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE
        );

        matrices.translate(0.5, 0.5, 0.5);

        for (var side : Direction.values()) {
            matrices.push();
            matrices.multiply(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), side.getUnitVector()));
            SpellCircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, Trickster.id("textures/gui/item_slot.png"),
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
}
