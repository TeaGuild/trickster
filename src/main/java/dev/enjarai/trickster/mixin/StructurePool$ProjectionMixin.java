package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.pond.ProjectionMatchingPoolDuck;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(StructurePool.Projection.class)
public class StructurePool$ProjectionMixin implements ProjectionMatchingPoolDuck {
    @Final
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable;createCodec(Ljava/util/function/Supplier;)Lnet/minecraft/util/StringIdentifiable$EnumCodec;"))
    private static StringIdentifiable.EnumCodec<StructurePool.Projection> replaceCodec(Supplier<StructurePool.Projection[]> enumValues,
        Operation<StringIdentifiable.EnumCodec<StructurePool.Projection>> original) {

        Function<String, StructurePool.Projection> toIdentifiable = (id) -> {
            if (id.equals("trickster:terrain_matching_ocean")) {
                var projection = StructurePool.Projection.TERRAIN_MATCHING;
                ((ProjectionMatchingPoolDuck) (Object) projection).setMatchingOcean(true);
                return projection;
            } else {
                return original.call(enumValues).byId(id);
            }
        };

        return new StringIdentifiable.EnumCodec(StructurePool.Projection.values(), toIdentifiable);
    }

    @Unique
    private boolean matchingOcean = false;

    @Override
    public boolean matchingOcean() {
        return matchingOcean;
    }

    @Override
    public void setMatchingOcean(boolean value) {
        matchingOcean = value;
    }
}
