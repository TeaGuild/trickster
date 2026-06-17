package dev.enjarai.trickster.mixin.client.lavender;

import dev.enjarai.trickster.pond.TexturedLavenderEntry;
import io.wispforest.lavender.book.Entry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entry.class)
public class EntryMixin implements TexturedLavenderEntry {
    @Unique
    private Identifier texture;

    @Override
    public void trickster$setTexture(Identifier texture) {
        this.texture = texture;
    }

    @Override
    @Nullable
    public Identifier trickster$getTexture() {
        return texture;
    }
}
