package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Stack;

public class SlotRenderer implements FragmentRenderer<SlotFragment> {
    @Override
    public void render(SlotFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {

        renderSlot(fragment, matrices, vertexConsumers, x, y, radius, alpha, normal, tickDelta, delegator);

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
            matrices, vertexConsumers, Trickster.id("textures/fragment/slot.png"),
            x - radius, x + radius, y - radius, y + radius,
            0.001f,
            delegator.getR() * r, delegator.getG() * g, delegator.getB() * b,
            alpha * delegator.getCircleTransparency(), delegator.inUI
        );
    }

    public static void renderSlot(SlotFragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float radius, float alpha, Vec3d normal, float tickDelta,
        SpellCircleRenderer delegator) {

        var slotStack = new Stack<Integer>();

        StorageSource source = fragment.slot();
        while (source instanceof StorageSource.Slot(int idx,StorageSource next)) {
            slotStack.add(idx);
            source = next;
        }

        ContainerRenderer.renderStorage(new ContainerFragment(source, fragment.variantType()), matrices, vertexConsumers, x, y, radius, alpha, normal, tickDelta, delegator);

        int i = 0;
        var off = 0.7 * radius;
        var c = slotStack.size();
        for (var idx : slotStack.reversed()) {
            var angle = Math.PI * 2 * i / Math.max(1, c);
            i++;
            matrices.push();
            matrices.translate(off * Math.sin(angle), -off * Math.cos(angle), 0.55 * radius);
            NumberRenderer.renderNumber(idx, matrices, vertexConsumers, x, y, radius * Math.min(0.5f, 2f / c), alpha, normal, tickDelta, delegator);
            matrices.pop();
        }
    }
}
