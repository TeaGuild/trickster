package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record WorldPosition(Vector3fc position) implements FleckPosition {
    public static final Identifier ID = Trickster.id("world");
    public static final StructEndec<WorldPosition> ENDEC = StructEndecBuilder.of(
        EndecTomfoolery.VECTOR_3F_ENDEC.fieldOf("position", WorldPosition::position),
        WorldPosition::new
    );

    @Override
    public Vector3f getPos(World world, float tickDelta) {
        return position.get(new Vector3f());
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
