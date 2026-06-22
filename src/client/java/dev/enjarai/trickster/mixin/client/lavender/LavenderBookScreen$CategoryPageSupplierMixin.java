package dev.enjarai.trickster.mixin.client.lavender;

import dev.enjarai.trickster.pond.TexturedLavenderThing;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LavenderBookScreen.CategoryPageSupplier.class)
public class LavenderBookScreen$CategoryPageSupplierMixin extends LavenderBookScreen$PageSupplierMixin {
    @Shadow
    @Final
    private Category category;

    @Override
    public @Nullable Identifier trickster$getBookTexture() {
        return ((TexturedLavenderThing) (Object) category).trickster$getTexture();
    }
}
