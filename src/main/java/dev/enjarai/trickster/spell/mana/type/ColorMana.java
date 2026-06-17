package dev.enjarai.trickster.spell.mana.type;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ColorComponent;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

import static net.minecraft.util.math.ColorHelper.Argb.getAlpha;
import static net.minecraft.util.math.ColorHelper.Argb.getArgb;
import static net.minecraft.util.math.ColorHelper.Argb.getBlue;
import static net.minecraft.util.math.ColorHelper.Argb.getGreen;
import static net.minecraft.util.math.ColorHelper.Argb.getRed;

public class ColorMana extends Mana {
    private final ComponentChanges REMOVE_COLOR = ComponentChanges.builder().remove(ManaComponents.COLOR).build();

    public ColorMana() {
        super(ComponentMap.builder().add(ManaComponents.COLOR, new ColorComponent(0xffffffff)).build());
    }

    @Override
    public int getColor(ManaVariant variant) {
        var component = variant.getComponentMap().get(ManaComponents.COLOR);
        return component != null ? component.color() : 0xff0055;
    }

    public Optional<ManaVariant> mix(World world, ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        if (left.withComponentChanges(REMOVE_COLOR).equals(right.withComponentChanges(REMOVE_COLOR))) {
            int colorLeft = left.getComponentMap().get(ManaComponents.COLOR).color();
            int colorRight = right.getComponentMap().get(ManaComponents.COLOR).color();

            long m = amountLeft + amountRight;

            long r = amountLeft * getRed(colorLeft) + amountRight * getRed(colorRight);
            long g = amountLeft * getGreen(colorLeft) + amountRight * getGreen(colorRight);
            long b = amountLeft * getBlue(colorLeft) + amountRight * getBlue(colorRight);
            long a = amountLeft * getAlpha(colorLeft) + amountRight * getAlpha(colorRight);
            long l = amountLeft * Math.max(getRed(colorLeft), Math.max(getGreen(colorLeft), getBlue(colorLeft)))
                + amountRight * Math.max(getRed(colorRight), Math.max(getGreen(colorRight), getBlue(colorRight)));

            r /= m;
            g /= m;
            b /= m;

            double f = (double) l / (double) m;
            double s = (double) Math.max(r, Math.max(g, b));

            var finalComp = new ColorComponent(getArgb((int) (a / m), (int) ((double) r * f / s), (int) ((double) g * f / s), (int) ((double) b * f / s)));
            return Optional.of(left.withComponentChanges(ComponentChanges.builder().add(ManaComponents.COLOR, finalComp).build()));
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
