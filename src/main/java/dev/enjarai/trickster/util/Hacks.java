package dev.enjarai.trickster.util;

import com.google.common.collect.ImmutableList;
import fuzs.extensibleenums.api.v2.core.UnsafeExtensibleEnum;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.Heightmap;

import java.lang.reflect.Field;

public class Hacks {
    public static void addStructPoolEnums() {
        try {
            Field idField = findFieldByType(StructurePool.Projection.class, String.class);
            Field processorsField = findFieldByType(StructurePool.Projection.class, ImmutableList.class);
            Field codecField = findFieldByType(StructurePool.Projection.class, StringIdentifiable.EnumCodec.class);

            var ProjectionEnumValue = UnsafeExtensibleEnum.invokeEnumConstructor(StructurePool.Projection.class, "TRICKSTER$TERRAIN_MATCHING_OCEAN");

            UnsafeExtensibleEnum.setObjectField(idField, ProjectionEnumValue, "trickster:terrain_matching_ocean"); //
            UnsafeExtensibleEnum.setObjectField(processorsField, ProjectionEnumValue, ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.OCEAN_FLOOR_WG, -1)));
            UnsafeExtensibleEnum.setStaticObjectField(codecField, StringIdentifiable.createCodec(StructurePool.Projection::values));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // needed because remapping
    private static Field findFieldByType(Class<?> clazz, Class<?> type) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == type) {
                return field;
            }
        }
        throw new NoSuchFieldError(type.getName());
    }
}
