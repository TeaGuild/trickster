package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.net.StorageMoveParticlePacket;
import io.wispforest.owo.network.ClientAccess;
import net.minecraft.particle.ParticleTypes;
import org.joml.Vector3d;

public class StorageMoveParticles {
    public static final int PARTICLES_PER_BLOCK = 8;
    public static final float PARTICLE_SPACING = 1f / PARTICLES_PER_BLOCK;

    @SuppressWarnings({ "resource", "DataFlowIssue" })
    public static void drawLine(StorageMoveParticlePacket packet, ClientAccess access) {
        var from = packet.from();
        var to = packet.to();
        var target = packet.target();
        long amount = packet.amount();

        target = target.add(0, 0.75, 0, new Vector3d());

        var distance = from.distance(to);
        var direction = to.sub(from, new Vector3d()).normalize();
        var spacing = direction.mul(PARTICLE_SPACING, new Vector3d());

        for (int i = 0; i < distance * PARTICLES_PER_BLOCK; i++) {
            var position = from.add(spacing.mul(i, new Vector3d()), new Vector3d());
            var offset = position.sub(target);

            for (int j = 0; j < Math.max(1, Math.log(amount)); j++) {
                var randomizedOffset = offset.add(Math.random() - 0.5d, Math.random() - 0.5d, Math.random() - 0.5d);
                access.runtime().world.addParticle(
                    ParticleTypes.ENCHANT, true,
                    target.x(), target.y(), target.z(),
                    randomizedOffset.x(), randomizedOffset.y(), randomizedOffset.z()
                );
            }
        }
    }
}
