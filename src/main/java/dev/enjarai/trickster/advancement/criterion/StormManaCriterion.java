package dev.enjarai.trickster.advancement.criterion;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class StormManaCriterion extends AbstractCriterion<StormManaCriterion.Conditions> {
    @Override
    public Codec<StormManaCriterion.Conditions> getConditionsCodec() {
        return StormManaCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, float stability) {
        super.trigger(player, conditions -> conditions.min.map(min -> min <= stability).orElse(true) && conditions.max.map(max -> stability <= max).orElse(true));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Float> min, Optional<Float> max) implements AbstractCriterion.Conditions {

        public static final Codec<StormManaCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(StormManaCriterion.Conditions::player),
            Codec.FLOAT.optionalFieldOf("min").forGetter(StormManaCriterion.Conditions::min),
            Codec.FLOAT.optionalFieldOf("max").forGetter(StormManaCriterion.Conditions::max)
        ).apply(instance, StormManaCriterion.Conditions::new));

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
