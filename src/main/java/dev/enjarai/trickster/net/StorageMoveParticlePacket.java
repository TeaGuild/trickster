package dev.enjarai.trickster.net;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3dc;

public record StorageMoveParticlePacket(Vector3dc from, Vector3dc to, Vector3dc target, long amount) {
    public static StructEndec<StorageMoveParticlePacket> ENDEC = StructEndecBuilder.of(
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("from", StorageMoveParticlePacket::from),
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("to", StorageMoveParticlePacket::to),
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("target", StorageMoveParticlePacket::target),
        Endec.LONG.fieldOf("amount", StorageMoveParticlePacket::amount),
        StorageMoveParticlePacket::new
    );
}
