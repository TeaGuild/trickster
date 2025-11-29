package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

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
        }
        matrices.multiply(new Quaternionf(new AxisAngle4f(time, 0, 1, 0)));

        if (Item.BLOCK_ITEMS.containsKey(block)) {

            var stack = Item.BLOCK_ITEMS.get(block).getDefaultStack();
            ItemTypeRenderer.renderItem(stack, ModelTransformationMode.NONE, matrices, vertexConsumers, x, y, size, delegator, 14, true);

        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
            matrices.scale(-1, 1, -1);
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
            matrices.scale(-1, 1, -1);
        }
        var buffer = vertexConsumers.getBuffer(RenderLayer.getLines());

        var mat = matrices.peek();
        var n = mat.getNormalMatrix().transform(normal.toVector3f());

        matrices.scale(0.41f, 0.41f, 0.41f);

        buffer.vertex(mat, 1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, 1, -1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, 1, -1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, 1).color(-1).normal(n.x, n.y, n.z);
        buffer.vertex(mat, -1, -1, -1).color(-1).normal(n.x, n.y, n.z);

        matrices.pop();
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
