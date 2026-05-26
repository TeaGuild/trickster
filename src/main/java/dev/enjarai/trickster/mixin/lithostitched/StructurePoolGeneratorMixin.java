package dev.enjarai.trickster.mixin.lithostitched;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    targets = "dev/worldgen/lithostitched/worldgen/structure/AlternateJigsawGenerator$StructurePoolGenerator", remap = false
)
public class StructurePoolGeneratorMixin {
    @ModifyArg(
        method = "findValidChildPiece", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getHeightOnGround(IILnet/minecraft/world/Heightmap$Type;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/world/gen/noise/NoiseConfig;)I"
        ), index = 2
    )
    private Heightmap.Type replaceHeightmap(Heightmap.Type original, @Local(argsOnly = true) PoolStructurePiece parentPiece) {
        var parentProjection = parentPiece.getPoolElement().getProjection();
        if (parentProjection.name().equals("TRICKSTER$TERRAIN_MATCHING_OCEAN")) {
            return Heightmap.Type.OCEAN_FLOOR_WG;
        }

        return original;
    }
}
