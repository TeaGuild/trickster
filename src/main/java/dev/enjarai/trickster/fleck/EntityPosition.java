package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record EntityPosition(int entityId, Vector3fc position) implements FleckPosition {
    public static final Identifier ID = Trickster.id("entity");
    public static final StructEndec<EntityPosition> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("entity_id", EntityPosition::entityId),
        EndecTomfoolery.VECTOR_3F_ENDEC.fieldOf("position", EntityPosition::position),
        EntityPosition::new
    );

    @Override
    @Nullable
    public Vector3f getPos(World world, float tickDelta) {
        var entity = world.getEntityById(entityId);
        if (entity == null) {
            return null;
        }

        return position.add(entity.getLerpedPos(tickDelta).toVector3f(), new Vector3f());
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
