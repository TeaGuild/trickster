package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record EntityHeadPosition(int entityId, Vector3fc position) implements FleckPosition {
    public static final Identifier ID = Trickster.id("entity_head");
    public static final StructEndec<EntityHeadPosition> ENDEC = StructEndecBuilder.of(
        Endec.INT.fieldOf("entity_id", EntityHeadPosition::entityId),
        EndecTomfoolery.VECTOR_3F_ENDEC.fieldOf("position", EntityHeadPosition::position),
        EntityHeadPosition::new
    );

    @Override
    @Nullable
    public Vector3f getPos(World world, float tickDelta) {
        var entity = world.getEntityById(entityId);
        if (entity == null) {
            return null;
        }

        var newPos = new Vector3f(position);
        newPos.rotateX(entity.getPitch(tickDelta) * MathHelper.RADIANS_PER_DEGREE);
        newPos.rotateY(-entity.getYaw(tickDelta) * MathHelper.RADIANS_PER_DEGREE);
        newPos.add(entity.getLerpedPos(tickDelta).toVector3f().add(0, entity.getEyeHeight(entity.getPose()), 0));

        return newPos;
    }

    @Override
    public Quaternionf transformFacing(Vector3fc facing, World world, float tickDelta) {
        var entity = world.getEntityById(entityId);

        Quaternionf newFacing;
        if (facing.x() == 0 && facing.z() == 0) {
            newFacing = new Quaternionf().rotateLocalX((float) (Math.PI / 2 * facing.y()));
        } else {
            newFacing = new Quaternionf().lookAlong(facing, new Vector3f(0, -1, 0));
        }

        if (entity == null) {
            return newFacing;
        }

        newFacing.rotateX(-entity.getPitch(tickDelta) * MathHelper.RADIANS_PER_DEGREE);
        newFacing.rotateY(entity.getYaw(tickDelta) * MathHelper.RADIANS_PER_DEGREE);

        return newFacing.invert();
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
