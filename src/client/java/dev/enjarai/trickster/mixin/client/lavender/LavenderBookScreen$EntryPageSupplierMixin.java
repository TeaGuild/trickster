package dev.enjarai.trickster.mixin.client.lavender;

import dev.enjarai.trickster.pond.TexturedLavenderThing;
import io.wispforest.lavender.book.Entry;
import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LavenderBookScreen.EntryPageSupplier.class)
public class LavenderBookScreen$EntryPageSupplierMixin extends LavenderBookScreen$PageSupplierMixin {
    @Shadow
    @Final
    private Entry entry;

    @Override
    public @Nullable Identifier trickster$getBookTexture() {
        return ((TexturedLavenderThing) (Object) entry).trickster$getTexture();
    }
}
