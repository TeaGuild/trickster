package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.net.ManaMoveParticlePacket;
import io.wispforest.owo.network.ClientAccess;
import org.joml.Vector3d;

public class ManaMoveParticles {
    public static final int PARTICLES_PER_BLOCK = 8;
    public static final float PARTICLE_SPACING = 1f / PARTICLES_PER_BLOCK;

    @SuppressWarnings({ "resource", "DataFlowIssue" })
    public static void drawLine(ManaMoveParticlePacket packet, ClientAccess access) {
        var to = packet.from();
        var from = packet.to();
        float amount = packet.amount();

        var distance = from.distance(to);
        if (distance < 0.5) {
            return;
        }

        access.runtime().execute(() -> {
            var logAmount = Math.log(amount) / 10;
            var particles = amount / 10;
            for (int j = 0; j < (int) particles + (Math.random() < particles % 1 ? 1 : 0); j++) {
                var position = from.lerp(to, Math.random(), new Vector3d());
                position.add((Math.random() - 0.5d) * logAmount, (Math.random() - 0.5d) * logAmount, (Math.random() - 0.5d) * logAmount);
                var offset = to.sub(position, new Vector3d());
                var dist = offset.length();
                offset.mul(1 / dist).mul(Math.sqrt(dist) / 20);
                access.runtime().world.addParticle(
                    ModParticles.SPELL_WHITE, true,
                    position.x(), position.y(), position.z(),
                    offset.x(), offset.y(), offset.z()
                );
            }
        });
    }
}
