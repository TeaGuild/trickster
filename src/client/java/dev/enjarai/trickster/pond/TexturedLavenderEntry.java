package dev.enjarai.trickster.pond;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface TexturedLavenderEntry {
    void trickster$setTexture(Identifier texture);

    @Nullable
    Identifier trickster$getTexture();
}
