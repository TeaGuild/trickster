package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.Trickster;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public class StructurePoolBasedGeneratorMixin {
    @Inject(
        method = "generatePiece", at = @At(
            "HEAD")
    )
    private void test(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig,
        StructurePoolAliasLookup aliasLookup, StructureLiquidSettings liquidSettings, CallbackInfo ci) {
        Trickster.LOGGER.warn("TEST!!!");
    }
}
