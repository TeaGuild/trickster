package dev.enjarai.trickster.spell.mana.type;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ColorComponent;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
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
        return variant.getComponentMap().get(ManaComponents.COLOR).color();
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

            return Optional.of(left.withComponentChanges(ComponentChanges.builder()
                .add(ManaComponents.COLOR, new ColorComponent(getArgb((int) (a / m), (int) ((double) r * f / s), (int) ((double) g * f / s), (int) ((double) b * f / s)))).build()));
        }

        return Optional.empty();
    }

    public void appendItemTooltip(ManaVariant variant, Item.TooltipContext context, List<Text> tooltip, TooltipType type, World world) {
        //TODO: add color meow meow?
    }
}
