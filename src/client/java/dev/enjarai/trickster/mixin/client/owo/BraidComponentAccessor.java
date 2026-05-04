package dev.enjarai.trickster.mixin.client.owo;

import io.wispforest.owo.braid.core.EventBinding;
import io.wispforest.owo.ui.component.BraidComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BraidComponent.class)
public interface BraidComponentAccessor {
    @Accessor(remap = false)
    EventBinding getEventBinding();
}
