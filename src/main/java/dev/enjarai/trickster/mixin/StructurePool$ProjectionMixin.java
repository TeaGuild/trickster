package dev.enjarai.trickster.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(StructurePool.Projection.class)
public class StructurePool$ProjectionMixin {

    @Unique
    private boolean matchingOcean = false;

    @Final
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable;createCodec(Ljava/util/function/Supplier;)Lnet/minecraft/util/StringIdentifiable$EnumCodec;"))
    private static StringIdentifiable.EnumCodec<StructurePool.Projection> replaceCodec(Supplier<StructurePool.Projection[]> enumValues,
        Operation<StringIdentifiable.EnumCodec<StructurePool.Projection>> original) {

        Function<String, StructurePool.Projection> toIdentifiable = (id) -> {
            if (id.equals("trickster:terrain_matching_ocean")) {
                var projection = StructurePool.Projection.TERRAIN_MATCHING;
                ((StructurePool$ProjectionMixin) (Object) projection).matchingOcean = true;
                return projection;
            } else {
                return original.call(enumValues).byId(id);
            }
        };

        return new StringIdentifiable.EnumCodec(StructurePool.Projection.values(), toIdentifiable);
    }

    @WrapMethod(method = "getId")
    private String getRealId(Operation<String> original) {
        return matchingOcean ? "trickster:terrain_matching_ocean" : original.call();
    }

    @WrapMethod(method = "getProcessors")
    private ImmutableList<StructureProcessor> getRealProcessors(Operation<ImmutableList<StructureProcessor>> original) {
        return matchingOcean ? ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.OCEAN_FLOOR_WG, -1)) : original.call();
    }

}
