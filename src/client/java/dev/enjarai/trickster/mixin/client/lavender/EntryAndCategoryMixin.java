package dev.enjarai.trickster.mixin.client.lavender;

import dev.enjarai.trickster.pond.TexturedLavenderThing;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.book.Entry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = { Entry.class, Category.class })
public class EntryAndCategoryMixin implements TexturedLavenderThing {
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
