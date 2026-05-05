package dev.enjarai.trickster.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructurePool.Projection.class)
public enum StructurePool$ProjectionMixin {
    TRICKSTER$TERRAIN_MATCHING_OCEAN("trickster:terrain_matching_ocean", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.OCEAN_FLOOR_WG, -1)));

    @Shadow
    StructurePool$ProjectionMixin(final String id, final ImmutableList<StructureProcessor> processors) {}
}
