package dev.enjarai.trickster.spell.mana.type.component;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public record StabilityComponent(long resetTime) {
    public static final long STABLE_TIME = 4800;
    public static final StructEndec<StabilityComponent> ENDEC = StructEndecBuilder.of(
        Endec.LONG.fieldOf("reset_time", StabilityComponent::resetTime),
        StabilityComponent::new
    );

    public float getStability(World world) {
        return (float) Math.min(STABLE_TIME, world.getTime() - resetTime) / STABLE_TIME;
    }
}
