package dev.enjarai.trickster.spell.mana;

import java.util.UUID;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public record SharedManaPool(UUID uuid) implements MutableManaPool {
    public static final StructEndec<SharedManaPool> ENDEC = StructEndecBuilder.of(
        EndecTomfoolery.UUID.fieldOf("uuid", SharedManaPool::uuid),
        SharedManaPool::new
    );

    private static final SimpleManaPool THE_SKILL_ISSUE = new SimpleManaPool(0) {
        @Override
        public void set(long value, World world) {
            mana = 0;
        }

        @Override
        public void setMax(long value, World world) {
            maxMana = 0;
        }
    };

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SHARED;
    }

    @Override
    public ManaVariant getVariant(World world) {
        return getSelf(getComponent(world)).getVariant(world);
    }

    @Override
    public long get(World world) {
        return getSelf(getComponent(world)).get(world);
    }

    @Override
    public long getMax(World world) {
        return getSelf(getComponent(world)).getMax(world);
    }

    @Override
    public void setVariant(ManaVariant variant, World world) {
        var component = getComponent(world);
        var self = getSelf(component);
        self.setVariant(variant, world);
        component.markDirty(uuid);
    }

    @Override
    public void set(long value, World world) {
        var component = getComponent(world);
        var self = getSelf(component);
        self.set(value, world);
        component.markDirty(uuid);
    }

    @Override
    public void setMax(long value, World world) {
        var component = getComponent(world);
        var self = getSelf(component);
        self.setMax(value, world);
        component.markDirty(uuid);
    }

    @Override
    public long use(ManaVariant variant, long amount, World world) {
        var component = getComponent(world);
        var self = getSelf(component);
        var result = self.use(variant, amount, world);
        component.markDirty(uuid);
        return result;
    }

    @Override
    public long refill(ManaVariant variant, long amount, World world) {
        var component = getComponent(world);
        var self = getSelf(component);
        var result = self.refill(variant, amount, world);
        component.markDirty(uuid);
        return result;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return this;
    }

    private SharedManaComponent getComponent(World world) {
        return ModGlobalComponents.SHARED_MANA.get(world.getScoreboard());
    }

    private SimpleManaPool getSelf(SharedManaComponent component) {
        return component.get(uuid).orElse(THE_SKILL_ISSUE);
    }
}
