package dev.enjarai.trickster.pond;

import dev.enjarai.trickster.util.InputContext;

import java.util.List;

public interface ContextualKeyBinding {
    void trickster$addToContext(InputContext context);

    List<InputContext> trickster$getContexts();
}
