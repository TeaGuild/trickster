package dev.enjarai.trickster.mixin.client.lavender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.pond.TexturedLavenderEntry;
import io.wispforest.lavender.book.Entry;
import io.wispforest.lavender.client.LavenderBookScreen;
import dev.enjarai.trickster.pond.TexturedPageSupplier;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LavenderBookScreen.PageSupplier.class)
public class LavenderBookScreen$PageSupplierMixin implements TexturedPageSupplier {
    @Override
    @Nullable
    public Identifier trickster$getBookTexture() {
        return null;
    }

    @ModifyExpressionValue(
        method = "lambda$buildEntryIndex$8", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/text/MutableText;styled(Ljava/util/function/UnaryOperator;)Lnet/minecraft/text/MutableText;"
        )
    )
    private static MutableText modifyStyle(MutableText original, @Local(argsOnly = true) Entry entry) {
        if (((TexturedLavenderEntry) (Object) entry).trickster$getTexture() != null) {
            return original.styled(s -> s.withItalic(true));
        }
        return original;
    }
}
