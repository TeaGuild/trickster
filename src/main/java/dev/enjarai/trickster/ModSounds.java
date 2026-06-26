package dev.enjarai.trickster;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public class ModSounds {
    public static final SoundEvent THE_FUNNY = register("whats_this_doing_here");
    public static final SoundEvent DRAW = register("draw");
    public static final SoundEvent COMPLETE = register("complete");
    public static final SoundEvent CAST = register("cast");
    public static final SoundEvent COLLAR_BELL = register("collar_bell");
    public static final SoundEvent USE_ESOTERIC_MANA = register("use_esoteric_mana");
    public static final SoundEvent LEASH_LINK = register("leash_link");
    public static final SoundEvent KNOT_TICK = register("knot_tick");
    public static final SoundEvent PLACE_CONSTRUCT_KNOT = register("place_construct_knot");
    public static final SoundEvent REMOVE_CONSTRUCT_KNOT = register("remove_construct_knot");
    public static final SoundEvent PLACE_CONSTRUCT_CORE = register("place_construct_core");
    public static final SoundEvent REMOVE_CONSTRUCT_CORE = register("remove_construct_core");
    public static final SoundEvent PLACE_CHARGING_ARRAY_KNOT = register("place_charging_array_knot");
    public static final SoundEvent REMOVE_CHARGING_ARRAY_KNOT = register("remove_charging_array_knot");
    public static final SoundEvent PLACE_SCROLL = register("place_scroll");
    public static final SoundEvent REMOVE_SCROLL = register("remove_scroll");
    public static final SoundEvent DISPLACEMENT_TELEPORT = register("displacement_teleport");
    public static final SoundEvent DISPLACEMENT_TRIGGER = register("displacement_trigger");
    public static final SoundEvent USE_LUMINOUS_BOTTLE = register("use_luminous_bottle");
    public static final SoundEvent KNOT_CRACK = register("knot_crack");
    public static final SoundEvent TRIGGER_RESONATOR = register("trigger_resonator");

    private static SoundEvent register(String path) {
        var id = Trickster.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {

    }

    private static final Random PITCH_RANDOM = new LocalRandom(0xCAFEBABE);

    public static float randomPitch(float start, float range) {
        return start - range + PITCH_RANDOM.nextFloat() * range * 2;
    }
}
