package dev.enjarai.trickster.spell.mana.type;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.SpellParticleOptions;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ColorComponent;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.util.ColorUtil;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ColorMana extends Mana {
    private final ComponentChanges REMOVE_COLOR = ComponentChanges.builder().remove(ManaComponents.COLOR).build();

    public ColorMana() {
        super(ComponentMap.builder().add(ManaComponents.COLOR, new ColorComponent(0xff0055)).build());
    }

    @Override
    public int getColor(ManaVariant variant) {
        var component = variant.getComponentMap().get(ManaComponents.COLOR);
        return component != null ? component.color() : 0xff0055;
    }

    @Override
    public ParticleEffect getParticle(ManaVariant variant) {
        var component = variant.getComponentMap().get(ManaComponents.COLOR);
        var color = component != null ? component.color() : 0xff0055;
        return new SpellParticleOptions(color);
    }

    public Optional<ManaVariant> mix(World world, ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        if (left.withComponentChanges(REMOVE_COLOR).equals(right.withComponentChanges(REMOVE_COLOR))) {
            int colorLeft = left.getComponentMap().get(ManaComponents.COLOR).color();
            int colorRight = right.getComponentMap().get(ManaComponents.COLOR).color();

            var finalComp = new ColorComponent(ColorUtil.mixColors(colorLeft, amountLeft, colorRight, amountRight));
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
                ColorUtil.rgbAsText(color).withColor(color)
            ).styled(s -> s.withColor(0xaaaabb)));
        }
    }
}
