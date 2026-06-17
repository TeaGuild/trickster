package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class ColorMana extends Mana {
    private final ComponentChanges REMOVE_COLOR = ComponentChanges.builder().remove(ManaComponents.COLOR).build();

    public ColorMana(ComponentMap defaultComponents) {
        super(defaultComponents);
    }

    @Override
    public int getColor(ManaVariant variant) {
        var component = variant.getComponentMap().get(ManaComponents.COLOR);
        return component != null ? component.color() : 0xff0055;
    }

    @Override
    public Optional<ManaVariant> mix(World world, ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        if (left.withComponentChanges(REMOVE_COLOR).equals(right.withComponentChanges(REMOVE_COLOR))) {

            return Optional.empty(); // TODO
        }

        return Optional.empty();
    }

    @Override
    public void appendItemTooltip(ManaVariant variant, Item.TooltipContext context, List<Text> tooltip, TooltipType type, World world) {
        super.appendItemTooltip(variant, context, tooltip, type, world);

        var component = variant.getComponentMap().get(ManaComponents.COLOR);
        if (component != null) {
            var color = component.color();
            tooltip.add(Text.translatable(
                Trickster.MOD_ID + ".mana.color",
                Text.literal("#%02X%02X%02X".formatted(
                    ColorHelper.Argb.getRed(color),
                    ColorHelper.Argb.getGreen(color),
                    ColorHelper.Argb.getBlue(color)
                )).withColor(color)
            ).styled(s -> s.withColor(0xaaaabb)));
        }
    }
}
