package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.FaceHeightComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;

import java.util.List;

public class PositionableWearableItem extends Item {
    public PositionableWearableItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            var component = stack.get(ModComponents.FACE_HEIGHT);
            var height = 0;
            if (component != null) {
                height = component.height();
            }

            height = (height + 1) % 5;
            stack.set(ModComponents.FACE_HEIGHT, new FaceHeightComponent(height));

            return true;
        }

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var component = stack.get(ModComponents.FACE_HEIGHT);
        var height = 0;
        if (component != null) {
            height = component.height();
        }

        tooltip.add(Text.translatable("trickster.tooltip.face_height", height)
            .styled(s -> s.withColor(Formatting.GRAY)));

        super.appendTooltip(stack, context, tooltip, type);
    }
}
