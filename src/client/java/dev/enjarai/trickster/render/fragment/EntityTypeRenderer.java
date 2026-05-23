package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.fragment.EntityTypeFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityTypeRenderer implements FragmentRenderer<EntityTypeFragment> {

    public static ItemEntity dummyItem;

    @Override
    public void render(EntityTypeFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, float forcedAlpha, Vec3d normal,
        float tickDelta,
        CircleRenderer delegator) {
        var entity = fragment.entityType().create(MinecraftClient.getInstance().world);

        if (entity instanceof ItemEntity item) {
            if (dummyItem == null) {
                item.setStack(new ItemStack(Items.DIAMOND));
                dummyItem = item;
            }
            entity = dummyItem;
        }

        if (fragment.entityType() == EntityType.PLAYER) {
            entity = MinecraftClient.getInstance().player;
        }

        if (entity == null) {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, size, alpha);
            return;
        }

        matrices.translate(x, y, delegator.inUI ? 0.01f * size : 0);
        if (delegator.inUI) {
            matrices.scale(size, -size, 0.01f * size);
            matrices.multiply(new Quaternionf(new AxisAngle4f(0.5f, 1, 0, 0)));
        } else {
            matrices.scale(size, size, size);
            var mat = new Matrix4f(matrices.peek().getPositionMatrix());
            matrices.loadIdentity();

            matrices.multiplyPositionMatrix(new Matrix4f()
                .translate(mat.getTranslation(new Vector3f()))
                .scale(mat.getScale(new Vector3f()))
            );
        }

        matrices.push();

        matrices.scale(0.8f, 0.8f, 0.8f);
        var scale = Math.max(entity.getHeight(), entity.getWidth());
        scale = 1f / scale;
        matrices.scale(scale, scale, scale);
        matrices.translate(0, -entity.getHeight() / 2, 0);

        if (entity instanceof ItemEntity) {

            matrices.scale(0.5f, 0.5f, 0.5f);
        }

        float time = System.currentTimeMillis() % 1048576 / 1000f;
        matrices.multiply(new Quaternionf(new AxisAngle4f(time, 0, 1, 0)));

        entity.setGlowing(true);
        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var renderer = dispatcher.getRenderer(entity);
        var finalEntity = entity;
        dispatcher.render(
            entity, 0, 0, 0, 0, tickDelta,
            matrices,
            layer -> vertexConsumers.getBuffer(layer.getAffectedOutline().map(v -> RenderLayer.getDebugLineStrip(1)).orElse(RenderLayer.getDebugFilledBox())),
            //            entity instanceof PlayerEntity ? layer -> vertexConsumers.getBuffer(RenderLayer.getDebugQuads())
            //                : layer -> vertexConsumers.getBuffer(RenderLayer.getOutline(renderer.getTexture(finalEntity))),
            LightmapTextureManager.pack(0, 0)
        );

        matrices.pop();
    }

    @Override
    public float getProportionalHeight(EntityTypeFragment fragment) {
        return 1;
    }

    @Override
    public boolean doubleSided() {
        return false;
    }
}
