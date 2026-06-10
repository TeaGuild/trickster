package dev.enjarai.trickster.data.mana;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.data.CompleteJsonDataLoader;
import dev.enjarai.trickster.spell.mana.type.Mana;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ManaConversionLoader extends CompleteJsonDataLoader implements IdentifiableResourceReloadListener {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final RegistryWrapper.WrapperLookup registryLookup;
    protected ImmutableMap<Mana, Map<Mana, ManaConversion>> conversions = ImmutableMap.of();

    public ManaConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super(GSON, "mana_conversion");
        this.registryLookup = registryLookup;
    }

    @Override
    public Identifier getFabricId() {
        return Trickster.id("mana_conversion");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> prepared, ResourceManager manager, Profiler profiler) {
        Map<Mana, Map<Mana, ManaConversion>> map = new HashMap<>();

        prepared.forEach((identifier, jsonElements) -> {
            for (JsonElement jsonElement : jsonElements) {
                ManaConversion conversion = ManaConversion.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();

                map.compute(conversion.from(), (mana, conversions) -> {
                    Map<Mana, ManaConversion> values = Objects.requireNonNullElseGet(conversions, HashMap::new);

                    values.put(conversion.to(), conversion);

                    return values;
                });
            }
        });

        ImmutableMap.Builder<Mana, Map<Mana, ManaConversion>> builder = ImmutableMap.builder();
        map.forEach((block, weightedValues) -> builder.put(block, Map.copyOf(weightedValues)));

        conversions = builder.build();
    }

    public @Nullable ManaConversion getManaConversion(Mana from, Mana to) {
        var toOptions = conversions.get(from);
        if (toOptions == null) {
            return null;
        }

        return toOptions.get(to);
    }

    public record ManaConversion(Mana from, Mana to, float multiplier) {
        public static final Codec<ManaConversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(Mana.ENDEC).fieldOf("from").forGetter(ManaConversion::from),
            CodecUtils.toCodec(Mana.ENDEC).fieldOf("to").forGetter(ManaConversion::to),
            Codec.FLOAT.fieldOf("multiplier").forGetter(ManaConversion::multiplier)
        ).apply(instance, ManaConversion::new));
    }
}
