package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.SpellContext;
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

import org.joml.Vector3dc;

public abstract class Mana {
    public static final Endec<Mana> ENDEC = MinecraftEndecs.ofRegistry(Manae.REGISTRY);

    private final ComponentMap defaultComponents;

    public Mana(ComponentMap defaultComponents) {
        this.defaultComponents = defaultComponents;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
    }

    public ComponentMap getDefaultComponents() {
        return defaultComponents;
    }

    public abstract boolean permitsTransfer(ManaVariant variant);

    public abstract float modifyInterest(ManaVariant variant, float interest);

    protected float permitsPositiveInterest(float interest) {
        return interest > 0 ? interest : 0;
    }

    protected float permitsNegativeInterest(float interest) {
        return interest < 0 ? interest : 0;
    };

    public abstract int getColor(ManaVariant variant);

    public ParticleEffect getParticle(ManaVariant variant) {
        return ModParticles.SPELL_WHITE;
    }

    public double getManaTransferTaxMultiplier(ManaVariant variant, SpellContext ctx, Vector3dc pos) {
        var distance = ctx.source().getPos().distance(pos);
        float r = Trickster.CONFIG.manaTransferEfficiency();
        return 1 - Math.max(0, 1 - r / (distance + r - 16));
    }

    public Optional<ManaVariant> mix(World world, ManaVariant left, long amountLeft, ManaVariant right, long amountRight) {
        return Optional.empty();
    }

    public void appendItemTooltip(ManaVariant variant, Item.TooltipContext context, List<Text> tooltip, TooltipType type, World world) {

    }

    public MutableText getName(ManaVariant variant) {
        var id = Manae.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".mana." + id.getNamespace() + "." + id.getPath()).withColor(getColor(variant));
    }
}
