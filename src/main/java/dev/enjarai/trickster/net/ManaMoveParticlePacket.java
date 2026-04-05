package dev.enjarai.trickster.net;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3dc;

public record ManaMoveParticlePacket(Vector3dc from, Vector3dc to, float amount) {
    public static StructEndec<ManaMoveParticlePacket> ENDEC = StructEndecBuilder.of(
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("from", ManaMoveParticlePacket::from),
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("to", ManaMoveParticlePacket::to),
        Endec.FLOAT.fieldOf("amount", ManaMoveParticlePacket::amount),
        ManaMoveParticlePacket::new
    );
}
