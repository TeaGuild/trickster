package dev.enjarai.trickster.cca;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

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
    private Optional<Destination> destination = Optional.empty();
    private Optional<Vec3d> initialPosition = Optional.empty();

    public DisplacementComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {}

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {}

    @Override
    public void serverTick() {
        teleportReferences.values().removeIf(r -> r.timeCreated + KEEP_TIME < entity.getWorld().getTime());

        initialPosition.ifPresent(initialPos -> {
            if (initialPos.squaredDistanceTo(entity.getPos()) > 0.5 * 0.5) {
                cancelTeleport();
            }
        });

        destination.ifPresent(xyz -> {
            if (!ModEntityComponents.GRACE.get(entity).isInGrace("displacement")) {
                destination = Optional.empty();

                var inVoid = xyz.position.y < xyz.world.getDimension().minY();
                if (inVoid || !xyz.world.isSpaceEmpty(entity,
                    entity.getDimensions(entity.getPose()).getBoxAt(xyz.position.x, xyz.position.y, xyz.position.z))) {

                    // Not a good place to teleport to
                    return;
                }

                entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                if (entity.getWorld() == xyz.world) {
                    entity.requestTeleport(xyz.position.x, xyz.position.y, xyz.position.z);
                } else {
                    entity.teleport((ServerWorld) xyz.world, xyz.position.x, xyz.position.y, xyz.position.z, Set.of(), entity.getHeadYaw(), entity.getPitch());
                }

                entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        });
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

    public void useReference(UUID uuid, World destinationWorld, Vector3d destination, boolean instant) {
        var reference = teleportReferences.get(uuid);
        if (reference != null) {
            reference.used = true;
        }

        this.destination.ifPresent(d -> cancelTeleport());

        this.destination = Optional.of(new Destination(destination, destinationWorld));
        this.initialPosition = Optional.of(entity.getPos());

        if (!instant) {
            ModEntityComponents.GRACE.get(entity).triggerGrace("displacement", CHARGE_TICKS);
            entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0F, 2.0F);
        }
    }

    public void cancelTeleport() {
        destination = Optional.empty();
        initialPosition = Optional.empty();
        ModEntityComponents.GRACE.get(entity).cancelGrace("displacement");

        var server = this.entity.getWorld().getServer();
        if (server != null) {
            var sound = SoundEvents.BLOCK_PORTAL_TRIGGER;
            var category = SoundCategory.PLAYERS;

            var packet = new StopSoundS2CPacket(sound.getId(), category);
            server.getPlayerManager().sendToAround(
                null, entity.getX(), entity.getY(), entity.getZ(),
                sound.getDistanceToTravel(1), entity.getWorld().getRegistryKey(),
                packet
            );
        }
    }

    public int getAvailableSlots() {
        return MAX_BURST - teleportReferences.size();
    }

    private static class TeleportReference {
        final long timeCreated;
        boolean used;

        TeleportReference(long timeCreated) {
            this.timeCreated = timeCreated;
        }
    }

    private record Destination(Vector3d position, World world) {
    }
}
