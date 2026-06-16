package dev.enjarai.trickster.mixin.lithostitched;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
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
        if (parentPiece.getPoolElement() instanceof DelegatingPoolElement delegator && delegator.config().terrainMatchingHeightmap() == Heightmap.Type.OCEAN_FLOOR_WG) {
            return Heightmap.Type.OCEAN_FLOOR_WG;
        }

        return original;
    }

    @Definition(id = "RIGID", field = "Lnet/minecraft/structure/pool/StructurePool$Projection;RIGID:Lnet/minecraft/structure/pool/StructurePool$Projection;")
    @Expression("? == RIGID")
    @ModifyExpressionValue(
        method = "findValidChildPiece", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0)
    )
    private boolean replaceProjection(boolean original, @Local(name = "parentPiece", argsOnly = true) PoolStructurePiece parentPiece) {
        if (parentPiece.getPoolElement() instanceof DelegatingPoolElement delegator && delegator.config().terrainMatchingHeightmap() == Heightmap.Type.OCEAN_FLOOR_WG) {
            return false;
        }

        return original;
    }

    @Definition(id = "RIGID", field = "Lnet/minecraft/structure/pool/StructurePool$Projection;RIGID:Lnet/minecraft/structure/pool/StructurePool$Projection;")
    @Expression("? == RIGID")
    @ModifyExpressionValue(
        method = "findValidChildPiece", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1)
    )
    private boolean replaceProjection(boolean original, @Local(name = "config") DelegatingConfig config) {
        if (config.terrainMatchingHeightmap() == Heightmap.Type.OCEAN_FLOOR_WG) {
            return false;
        }

        return original;
    }
}
