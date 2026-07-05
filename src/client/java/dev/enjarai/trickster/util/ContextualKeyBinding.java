package dev.enjarai.trickster.util;

import de.siphalor.amecs.api.PriorityKeyBinding;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Supplier;

public class ContextualKeyBinding extends KeyBinding implements PriorityKeyBinding {
    private final Supplier<Boolean> activeCondition;

    public ContextualKeyBinding(String translationKey, int code, String category, Supplier<Boolean> activeCondition) {
        super(translationKey, code, category);
        this.activeCondition = activeCondition;
    }

    @Override
    public boolean onPressedPriority() {
        return !activeCondition.get();
    }

    @Override
    public boolean onReleasedPriority() {
        return !activeCondition.get();
    }
}
