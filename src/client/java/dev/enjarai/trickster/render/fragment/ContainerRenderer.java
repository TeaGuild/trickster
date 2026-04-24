package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ContainerRenderer implements FragmentRenderer<ContainerFragment> {
    @Override
    public void render(ContainerFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {
        renderStorage(fragment, matrices, vertexConsumers, x, y, radius, alpha, normal, tickDelta, delegator);

        var type = fragment.variantType();

        int color = -1;
        if (type == VariantType.ITEM) {
            color = FragmentType.ITEM_TYPE.color().orElse(-1);
        } else if (type == VariantType.FLUID) {
            color = FragmentType.FLUID_TYPE.color().orElse(-1);
        }
        var r = ColorHelper.Argb.getRed(color) / 255f;
        var g = ColorHelper.Argb.getGreen(color) / 255f;
        var b = ColorHelper.Argb.getBlue(color) / 255f;

        SpellCircleRenderer.drawTexturedQuad(
            matrices, vertexConsumers, Trickster.id("textures/fragment/container.png"),
            x - radius, x + radius, y - radius, y + radius,
            0.001f,
            delegator.getR() * r, delegator.getG() * g, delegator.getB() * b,
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }

    public static void renderStorage(ContainerFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal,
        float tickDelta,
        SpellCircleRenderer delegator) {

        var source = fragment.source();

        if (source instanceof StorageSource.Block(BlockPos pos)) {
            /*
            matrices.push();
            matrices.translate(x, y, delegator.inUI ? radius * 0.5 : 0);
            matrices.scale(radius, delegator.inUI ? -radius : radius, radius);
            if (delegator.inUI) {
                matrices.scale(1f, 1f, 0.01f);
                matrices.multiply(new Quaternionf(new AxisAngle4f(0.5f, 1, 0, 0)));
                matrices.multiply(new Quaternionf(new AxisAngle4f((float) Math.PI / 4, 0, 1, 0)));
            } else {
                var mat = new Matrix4f(matrices.peek().getPositionMatrix());
                matrices.loadIdentity();
            
                matrices.multiplyPositionMatrix(new Matrix4f()
                    .translate(mat.getTranslation(new Vector3f()))
                    .scale(mat.getScale(new Vector3f()))
                );
            }
            matrices.scale(0.8f, 0.8f, 0.8f);
            var block = MinecraftClient.getInstance().world.getBlockState(pos);
            BlockTypeRenderer.renderBlock(block, matrices, vertexConsumers);
            matrices.pop();
            matrices.push();
            matrices.translate(0, 0, delegator.inUI ? radius * 0.55f : radius * -0.55f);
             */
            VectorRenderer.renderVector(pos.toCenterPos().toVector3d(), matrices, vertexConsumers, x, y, radius * 0.6f, 1, normal, tickDelta, delegator);
            //matrices.pop();

        } else if (source instanceof StorageSource.Entity(UUID uuid)) {
            var entFrag = new EntityFragment(uuid, Text.empty());
            var entityOp = entFrag.getEntity(MinecraftClient.getInstance().world, true);
            entityOp.ifPresentOrElse(
                entity -> EntityRenderer.renderEntity(entity, matrices, vertexConsumers, x, y, radius, tickDelta, delegator),
                () -> FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, radius, alpha)
            );
        } else if (source instanceof StorageSource.Caster) {
            SpellCircleRenderer.drawTexturedQuad(
                matrices, vertexConsumers, Trickster.id("textures/fragment/caster_container.png"),
                x - radius, x + radius, y - radius, y + radius,
                0.001f,
                delegator.getR(), delegator.getG(), delegator.getB(),
                alpha * delegator.getCircleTransparency(), delegator.inUI
            );
        } else if (source instanceof StorageSource.Slot slot) {
            SlotRenderer.renderSlot(new SlotFragment(slot, fragment.variantType()), matrices, vertexConsumers, x, y, radius, alpha, normal, tickDelta, delegator);
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, vertexConsumers, x, y, radius, alpha);
        }
    }
}
