package dev.enjarai.trickster.mixin.client.lavender;

import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.pond.TexturedPageSupplier;
import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavenderBookScreen.class)
public abstract class LavenderBookScreenMixin extends BaseUIModelScreen<FlowLayout> {
    @Shadow
    protected abstract Identifier bookTexture();

    protected LavenderBookScreenMixin(Class<FlowLayout> rootComponentClass, DataSource source) {
        super(rootComponentClass, source);
    }

    @Inject(
        method = "rebuildContent", at = @At(
            value = "INVOKE", target = "Lio/wispforest/lavender/client/LavenderBookScreen$PageSupplier;searchable()Z"
        )
    )
    private void rebuildBackground(SoundEvent sound, CallbackInfo ci, @Local(name = "pageSupplier") LavenderBookScreen.PageSupplier pageSupplier) {
        var panel = this.component(FlowLayout.class, "primary-panel");
        var child = panel.children().getFirst();

        if (!(child instanceof TextureComponent textureComponent)) {
            return;
        }

        var targetTexture = bookTexture();
        var pageTexture = ((TexturedPageSupplier) pageSupplier).trickster$getBookTexture();
        if (pageTexture != null) {
            targetTexture = pageTexture;
        }

        ((TextureComponentAccessor) textureComponent).setTexture(targetTexture);
    }
}
