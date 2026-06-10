package dev.enjarai.trickster.net;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.type.Mana;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3dc;

public record ManaMoveParticlePacket(Vector3dc from, Vector3dc to, Mana mana, float amount) {
    public static StructEndec<ManaMoveParticlePacket> ENDEC = StructEndecBuilder.of(
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("from", ManaMoveParticlePacket::from),
        EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("to", ManaMoveParticlePacket::to),
        Mana.ENDEC.fieldOf("variant", ManaMoveParticlePacket::mana),
        Endec.FLOAT.fieldOf("amount", ManaMoveParticlePacket::amount),
        ManaMoveParticlePacket::new
    );
}
