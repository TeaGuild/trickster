package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

import java.util.Optional;

public class StormMana extends Mana {
    public StormMana(ComponentMap defaultComponents, int color) {
        super(defaultComponents, color);
    }

    @Override
    public ParticleEffect getParticle() {
        return ModParticles.STORM_WHITE;
    }

    @Override
    public Optional<ManaVariant> mix(ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        return Optional.empty(); //TODO: impl mixing for storm mana
    }

    @Override
    public void appendItemTooltip(ManaVariant variant, Item.TooltipContext context, List<Text> tooltip, TooltipType type, World world) {
        super.appendItemTooltip(variant, context, tooltip, type, world);

        var component = variant.getComponentMap().get(ManaComponents.STABILITY);
        if (component != null) {
            tooltip.add(Text.translatable(
                Trickster.MOD_ID + ".mana.stability",
                "%.1f".formatted(component.getStability(world) * 100)
            ).styled(s -> s.withColor(0xaaaabb)));
        }
    }
}
