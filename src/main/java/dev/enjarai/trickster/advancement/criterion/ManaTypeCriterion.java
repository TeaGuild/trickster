package dev.enjarai.trickster.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.mana.type.Mana;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ManaTypeCriterion extends AbstractCriterion<ManaTypeCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return ManaTypeCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Mana type) {
        super.trigger(player, conditions -> conditions.manaType() == type);
    }

    public record Conditions(Optional<LootContextPredicate> player, Mana manaType)
        implements
            AbstractCriterion.Conditions {

        public static final Codec<ManaTypeCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ManaTypeCriterion.Conditions::player),
            CodecUtils.toCodec(Mana.ENDEC).fieldOf("mana_type").forGetter(ManaTypeCriterion.Conditions::manaType)
        ).apply(instance, ManaTypeCriterion.Conditions::new)
        );

        @Override
        public void validate(LootContextPredicateValidator validator) {
            AbstractCriterion.Conditions.super.validate(validator);
        }
    }
}
