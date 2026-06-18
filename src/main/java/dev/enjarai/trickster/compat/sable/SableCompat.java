package dev.enjarai.trickster.compat.sable;

import dev.enjarai.trickster.spell.trick.Tricks;

public class SableCompat {
    public static final ProjectPositionTrick PROJECT_POSITION = Tricks.register("project_position", new ProjectPositionTrick());
    public static final ProjectDirectionTrick PROJECT_DIRECTION = Tricks.register("project_direction", new ProjectDirectionTrick());

    public static void init() {

    }
}
