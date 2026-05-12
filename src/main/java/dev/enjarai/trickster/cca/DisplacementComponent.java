package dev.enjarai.trickster.cca;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;

public class DisplacementComponent implements ServerTickingComponent, ClientTickingComponent {
    public static final int CHARGE_TICKS = 40;
    public static final int KEEP_TIME = 5 * 20;
    public static final int MAX_BURST = 3;

    private final Entity entity;
    private final HashMap<UUID, TeleportReference> teleportReferences = new HashMap<>();

    public DisplacementComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }

    @Override
    public void serverTick() {
        teleportReferences.values().removeIf(r ->
            r.timeCreated + KEEP_TIME < entity.getWorld().getTime());
    }

    @Override
    public void clientTick() {
        if (ModEntityComponents.GRACE.get(entity).isInGrace("displacement")) {
            for (int i = 0; i < 2; i++) {
                entity.getWorld().addParticle(
                        ParticleTypes.PORTAL,
                        entity.getParticleX(0.5),
                        entity.getRandomBodyY() - 0.25,
                        entity.getParticleZ(0.5),
                        (entity.getRandom().nextDouble() - 0.5) * 2.0,
                        -entity.getRandom().nextDouble(),
                        (entity.getRandom().nextDouble() - 0.5) * 2.0
                );
            }
        }
    }

    public @Nullable UUID createReference() {
        if (teleportReferences.size() >= MAX_BURST) {
            return null;
        }

        var uuid = UUID.randomUUID();
        teleportReferences.put(uuid, new TeleportReference(entity.getWorld().getTime()));
        return uuid;
    }

    public boolean isValid(UUID uuid) {
        var reference = teleportReferences.get(uuid);
        return reference != null && !reference.used;
    }

    public void useReference(UUID uuid) {
        var reference = teleportReferences.get(uuid);
        if (reference != null) {
            reference.used = true;
        }
    }

    private static class TeleportReference {
        final long timeCreated;
        boolean used;

        TeleportReference(long timeCreated) {
            this.timeCreated = timeCreated;
        }
    }
}
