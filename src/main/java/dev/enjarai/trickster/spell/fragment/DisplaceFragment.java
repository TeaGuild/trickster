package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record DisplaceFragment(UUID referenceId, UUID entityId, RegistryKey<World> sourceWorld) implements Fragment {
    public static final StructEndec<DisplaceFragment> ENDEC = StructEndecBuilder.of(
        BuiltInEndecs.UUID.fieldOf("reference_id", DisplaceFragment::referenceId),
        BuiltInEndecs.UUID.fieldOf("entity_id", DisplaceFragment::entityId),
        CodecUtils.toEndec(RegistryKey.createCodec(RegistryKeys.WORLD)).fieldOf("source_world", DisplaceFragment::sourceWorld),
        DisplaceFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.DISPLACE;
    }

    @Override
    public Text asText() {
        return Text.translatable("trickster.displace.reference", referenceId);
    }

    @Override
    public int getWeight() {
        return 128;
    }

    public Optional<Entity> getEntity(SpellContext ctx) {
        //noinspection DataFlowIssue
        return Optional
            .ofNullable(ctx.source().getWorld().getServer().getWorld(sourceWorld).getEntity(entityId))
            .filter(EntityFragment::isValidEntity);
    }

    public static Optional<DisplaceFragment> fromEntity(Entity entity) {
        var component = entity.getComponent(ModEntityComponents.DISPLACEMENT);
        var referenceId = component.createReference();
        if (referenceId == null) {
            return Optional.empty();
        }

        return Optional.of(new DisplaceFragment(referenceId, entity.getUuid(), entity.getWorld().getRegistryKey()));
    }
}
