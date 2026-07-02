package dev.enjarai.trickster.util;

import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.world.World;

// Sable-aware distance functions
public class DistanceUtil {
    public static double distanceSquared(World world, Vector3dc a, Vector3dc b) {
        return SableCompanion.INSTANCE.distanceSquaredWithSubLevels(world, a, b);
    }

    public static double distance(World world, Vector3dc a, Vector3dc b) {
        return Math.sqrt(distanceSquared(world, a, b));
    }
}
