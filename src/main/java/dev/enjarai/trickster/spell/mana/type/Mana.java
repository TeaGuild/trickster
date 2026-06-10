package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

import java.util.Optional;

public class Mana {
    public static final Endec<Mana> ENDEC = MinecraftEndecs.ofRegistry(Manae.REGISTRY);

    private final ComponentMap defaultComponents;
    private final int color;

    public Mana(ComponentMap defaultComponents, int color) {
        this.defaultComponents = defaultComponents;
        this.color = color;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
    }

    public ComponentMap getDefaultComponents() {
        return defaultComponents;
    }

    public int getColor() {
        return this.color;
    }

    public ParticleEffect getParticle() {
        return ModParticles.SPELL_WHITE;
    }

    public Optional<ManaVariant> mix(ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        return Optional.empty();
    }

    public void appendItemTooltip(ManaVariant variant, Item.TooltipContext context, List<Text> tooltip, TooltipType type, World world) {

    }

    public MutableText getName() {
        var id = Manae.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".mana." + id.getNamespace() + "." + id.getPath()).withColor(color);
    }
}
