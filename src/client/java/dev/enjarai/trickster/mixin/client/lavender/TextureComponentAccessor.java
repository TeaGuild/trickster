package dev.enjarai.trickster.mixin.client.lavender;

import io.wispforest.owo.ui.component.TextureComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureComponent.class)
public interface TextureComponentAccessor {
    @Accessor
    @Mutable
    void setTexture(Identifier texture);
}
