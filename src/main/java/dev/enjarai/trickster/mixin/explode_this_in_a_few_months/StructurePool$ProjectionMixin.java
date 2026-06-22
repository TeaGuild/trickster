package dev.enjarai.trickster.mixin.explode_this_in_a_few_months;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(StructurePool.Projection.class)
public class StructurePool$ProjectionMixin {
    // we are absolutely NOT keeping this forever, this is just for a while so that worlds can migrate...
    @SuppressWarnings("deprecation")
    @Redirect(
        method = "<clinit>", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable;createCodec(Ljava/util/function/Supplier;)Lnet/minecraft/util/StringIdentifiable$EnumCodec;"
        )
    )
    private static StringIdentifiable.EnumCodec<StructurePool.Projection> fixBackwardsCompatTemporarily(Supplier<StructurePool.Projection[]> enumValues) {
        StructurePool.Projection[] enums = enumValues.get();
        Function<String, StructurePool.Projection> function = StringIdentifiable.createMapper(enums, id -> id);
        Function<String, StructurePool.Projection> betterFunction = string -> {
            var result = function.apply(string);
            if (result != null) {
                return result;
            }
            return StructurePool.Projection.RIGID;
        };
        return new StringIdentifiable.EnumCodec<>(enums, betterFunction);
    }
}
