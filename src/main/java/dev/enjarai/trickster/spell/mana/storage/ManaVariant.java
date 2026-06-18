package dev.enjarai.trickster.spell.mana.storage;

import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.enjarai.trickster.spell.mana.type.Manae;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.MutableText;

import java.util.Objects;

public class ManaVariant implements TransferVariant<Mana> {
    public static final StructEndec<ManaVariant> ENDEC = StructEndecBuilder.of(
        Mana.ENDEC.fieldOf("mana", ManaVariant::getMana),
        CodecUtils.toEndec(ComponentChanges.CODEC).fieldOf("components", ManaVariant::getComponents),
        ManaVariant::new
    );
    public static final ManaVariant EMPTY = ManaVariant.of(Manae.EMPTY);

    private final Mana mana;
    private final ComponentChanges components;
    private final ComponentMap componentMap;
    private final int hashCode;

    private ManaVariant(Mana mana, ComponentChanges components) {
        this.mana = mana;
        this.components = components;
        this.componentMap = components == ComponentChanges.EMPTY ? mana.getDefaultComponents() : ComponentMapImpl.create(mana.getDefaultComponents(), components);
        this.hashCode = Objects.hash(mana, components);
    }

    public ParticleEffect getParticle() {
        return mana.getParticle(this);
    }

    public int getColor() {
        return mana.getColor(this);
    }

    public MutableText getName() {
        return mana.getName(this);
    }

    @Override
    public boolean isBlank() {
        return mana.isEmpty();
    }

    @Override
    public Mana getObject() {
        return mana;
    }

    public Mana getMana() {
        return getObject();
    }

    @Override
    public ComponentChanges getComponents() {
        return components;
    }

    @Override
    public ComponentMap getComponentMap() {
        return componentMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManaVariant withComponentChanges(ComponentChanges changes) {
        var builder = ComponentChanges.builder();

        for (var component : getComponents().entrySet()) {
            if (component.getValue().isPresent()) {
                builder.add((ComponentType<Object>) component.getKey(), component.getValue().get());
            } else {
                builder.remove(component.getKey());
            }
        }

        for (var component : changes.entrySet()) {
            if (component.getValue().isPresent()) {
                builder.add((ComponentType<Object>) component.getKey(), component.getValue().get());
            } else {
                builder.remove(component.getKey());
            }
        }

        return of(mana, builder.build());
    }

    public static ManaVariant of(Mana mana) {
        return of(mana, ComponentChanges.EMPTY);
    }

    public static ManaVariant of(Mana mana, ComponentChanges components) {
        return new ManaVariant(mana, components);
    }

    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManaVariant variant = (ManaVariant) o;
        // fail fast with hash code
        return hashCode == variant.hashCode && mana == variant.mana && componentsMatch(variant.components);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
