package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.mana.type.component.StabilityComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

import java.util.Optional;

public class StormMana extends Mana {
    private final ComponentChanges REMOVE_STABILITY = ComponentChanges.builder().remove(ManaComponents.STABILITY).build();

    public StormMana(ComponentMap defaultComponents, int color) {
        super(defaultComponents, color);
    }

    @Override
    public ParticleEffect getParticle() {
        return ModParticles.STORM_WHITE;
    }

    @Override
    public Optional<ManaVariant> mix(SpellContext ctx, ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        if (left.withComponentChanges(REMOVE_STABILITY).equals(right.withComponentChanges(REMOVE_STABILITY))) {
            long currentTime = ctx.source().getWorld().getTime();

            long resetLeft = Math.max(currentTime - StabilityComponent.STABLE_TIME, left.getComponentMap().get(ManaComponents.STABILITY).resetTime());
            long resetRight = Math.max(currentTime - StabilityComponent.STABLE_TIME, right.getComponentMap().get(ManaComponents.STABILITY).resetTime());

            amountLeft = Math.max(1, amountLeft / 1000);
            amountRight = Math.max(1, amountRight / 1000);

            long weightedResetLeft = amountLeft * resetLeft;
            long weightedResetRight = amountRight * resetRight;

            long finalReset = (weightedResetLeft + weightedResetRight) / (amountLeft + amountRight);
            var finalComp = new StabilityComponent(finalReset);

            return Optional.of(left.withComponentChanges(ComponentChanges.builder().add(ManaComponents.STABILITY, finalComp).build()));
        }

        return Optional.empty();
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
