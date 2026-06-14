package dev.enjarai.trickster.util;

import dev.enjarai.trickster.pond.ContextualKeyBinding;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class InputContext {
    private static final List<InputContext> CONTEXTS = new ReferenceArrayList<>();

    public static List<InputContext> getContexts() {
        return CONTEXTS;
    }

    public static boolean contextsContain(KeyBinding binding) {
        for (var context : InputContext.getContexts()) {
            if (context.getKeyBindings().contains(binding)) {
                return true;
            }
        }

        return false;
    }

    private final Identifier id;
    private final Supplier<Boolean> activeCondition;
    private final List<KeyBinding> keyBindings = new ReferenceArrayList<>();
    private final Map<InputUtil.Key, KeyBinding> bindingsByKey = new HashMap<>();
    private boolean active;

    public InputContext(Identifier id, Supplier<Boolean> activeCondition) {
        this.id = id;
        this.activeCondition = activeCondition;
        CONTEXTS.add(this);
    }

    public void tick() {
        boolean active = activeCondition.get();
        if (active != this.active) {
            this.active = active;
            KeyBinding.updatePressedStates();
        }
    }

    public Identifier getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void addKeyBinding(KeyBinding keyBinding) {
        Objects.requireNonNull(keyBinding);
        keyBindings.add(keyBinding);
        ((ContextualKeyBinding) keyBinding).trickster$addToContext(this);
    }

    public List<KeyBinding> getKeyBindings() {
        return keyBindings;
    }

    public KeyBinding getKeyBinding(InputUtil.Key key) {
        return bindingsByKey.get(key);
    }

    public void updateKeysByCode() {
        bindingsByKey.clear();
        for (KeyBinding keyBinding : keyBindings) {
            bindingsByKey.put(KeyBindingHelper.getBoundKeyOf(keyBinding), keyBinding);
        }
    }
}
