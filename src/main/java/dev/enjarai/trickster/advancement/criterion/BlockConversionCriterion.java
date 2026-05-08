package dev.enjarai.trickster.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class BlockConversionCriterion extends AbstractCriterion<BlockConversionCriterion.Conditions> {
    @Override
    public Codec<BlockConversionCriterion.Conditions> getConditionsCodec() {
        return BlockConversionCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Block input, Block output) {
        super.trigger(player, conditions -> conditions.input.orElse(input).equals(input) && conditions.output.orElse(output).equals(output));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Block> input, Optional<Block> output) implements AbstractCriterion.Conditions {

        public static final Codec<BlockConversionCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BlockConversionCriterion.Conditions::player),
                Registries.BLOCK.getCodec().optionalFieldOf("input").forGetter(BlockConversionCriterion.Conditions::input),
                Registries.BLOCK.getCodec().optionalFieldOf("output").forGetter(BlockConversionCriterion.Conditions::output)
        ).apply(instance, BlockConversionCriterion.Conditions::new)
        );

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
