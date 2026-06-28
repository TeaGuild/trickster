package dev.enjarai.trickster.item;

import io.wispforest.accessories.api.client.Transformation;
import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryRenderTransformations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PositionableWearableItem extends Item {
    public static final int SCALE = 7;

    public PositionableWearableItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            var component = stack.get(AccessoriesDataComponents.RENDER_TRANSFORMATIONS);
            var height = 0f;
            if (component != null) {
                Transformation.Translation targetTransformation = null;
                for (var transformation : component.transformations()) {
                    if (transformation instanceof Transformation.Translation translation) {
                        height = translation.translation().y * SCALE;
                        targetTransformation = translation;
                        break;
                    }
                }

                height = (height + 1) % 5;

                var transformations = new ArrayList<Transformation>();
                for (var transformation : component.transformations()) {
                    if (transformation instanceof Transformation.Translation translation) {
                        if (translation == targetTransformation) {
                            transformations.add(new Transformation.Translation(
                                new Vector3f(translation.translation().x, height / SCALE, translation.translation().z)));
                            continue;
                        }
                    }

                    transformations.add(transformation);
                }

                stack.set(AccessoriesDataComponents.RENDER_TRANSFORMATIONS, new AccessoryRenderTransformations(
                    component.disableDefaultTranslations(),
                    transformations
                ));

                return true;
            }
        }

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var component = stack.get(AccessoriesDataComponents.RENDER_TRANSFORMATIONS);
        var height = 0;
        if (component != null) {
            for (var transformation : component.transformations()) {
                if (transformation instanceof Transformation.Translation translation) {
                    height = (int) (translation.translation().y * SCALE);
                    break;
                }
            }
        }

        tooltip.add(Text.translatable("trickster.tooltip.face_height", height)
            .styled(s -> s.withColor(Formatting.GRAY)));

        super.appendTooltip(stack, context, tooltip, type);
    }
}
