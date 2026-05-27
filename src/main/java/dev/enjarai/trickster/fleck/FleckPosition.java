package dev.enjarai.trickster.fleck;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Map;

public interface FleckPosition {
    Map<Identifier, StructEndec<? extends FleckPosition>> ENDECS = Map.of(
        WorldPosition.ID, WorldPosition.ENDEC,
        EntityPosition.ID, EntityPosition.ENDEC,
        EntityHeadPosition.ID, EntityHeadPosition.ENDEC
    );
    Endec<FleckPosition> ENDEC = Endec.dispatchedStruct(
        ENDECS::get,
        FleckPosition::getId,
        MinecraftEndecs.IDENTIFIER
    );

    @Nullable
    default Vector3f getPos(World world) {
        return getPos(world, 0);
    }

    @Nullable
    Vector3f getPos(World world, float tickDelta);

    default Quaternionf transformFacing(Vector3fc facing, World world, float tickDelta) {
        return new Quaternionf().lookAlong(facing, new Vector3f(0, -1, 0)).invert();
    }

    Identifier getId();
}
