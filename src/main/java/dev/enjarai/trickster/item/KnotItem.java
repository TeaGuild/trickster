package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.TickTrackerComponent;
import dev.enjarai.trickster.net.EchoGrabClipboardPacket;
import dev.enjarai.trickster.net.EchoSetClipboardPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.mana.*;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class KnotItem extends Item {
    public static Supplier<World> worldGetter = () -> null;
    public static Consumer<ManaComponent> subscribeConsumer = component -> {};

    private final float creationCost;

    public KnotItem(Settings settings, float creationCost) {
        super(settings.maxCount(1));
        this.creationCost = creationCost;
    }

    public static class Amethyst extends KnotItem {
        public Amethyst() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(128 * ManaPool.MANA_SCALE), 0)),
                0);
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_AMETHYST_KNOT;
        }

        @Override
        public ItemStack transferPropertiesToCracked(World world, ItemStack self, ItemStack cracked) {
            // Skip transferring the mana state
            return cracked;
        }
    }

    public static class CrackedAmethyst extends KnotItem {
        public CrackedAmethyst() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(256 * ManaPool.MANA_SCALE), 0)),
                Float.MAX_VALUE);
        }
    }

    public static class Quartz extends KnotItem implements ChannelItem {
        public Quartz() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(128 * ManaPool.MANA_SCALE), 0))
                .component(ModComponents.TICK_CREATED, new TickTrackerComponent(0)),
                0);
        }

        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            stack.set(ModComponents.TICK_CREATED, new TickTrackerComponent(world.getTime()));
            return stack;
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            return new ListFragment(List.of(new NumberFragment(stack.get(ModComponents.TICK_CREATED).getTick(ctx.source().getWorld()))));
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            if (value instanceof NumberFragment number) {
                stack.set(ModComponents.TICK_CREATED, new TickTrackerComponent(stack.get(ModComponents.TICK_CREATED).tick() - number.asInt()));
            }
        }

        public int getRange() {
            return 16;
        }

        @Override
        public float getConstructExecutionLimitMultiplier(ItemStack stack) {
            return 1.25f;
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_QUARTZ_KNOT;
        }
    }

    public static class CrackedQuartz extends KnotItem implements ChannelItem {
        public CrackedQuartz() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(0), 0)),
                Float.MAX_VALUE);
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            return new ListFragment(List.of(new NumberFragment(ctx.source().getWorld().getTimeOfDay())));
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            // Can't change its time offset anymore
        }

        public int getRange() {
            return 16;
        }

        @Override
        public float getConstructExecutionLimitMultiplier(ItemStack stack) {
            return 1.5f;
        }
    }

    public static class Emerald extends KnotItem {
        public Emerald() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024 * ManaPool.MANA_SCALE), 1 / 12f)),
                512);
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_EMERALD_KNOT;
        }
    }

    public static class CrackedEmerald extends KnotItem {
        public CrackedEmerald() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024 * ManaPool.MANA_SCALE), 1 / 12f / 6f)),
                Float.MAX_VALUE);
        }
    }

    public static class Diamond extends KnotItem {
        public Diamond() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(16384 * ManaPool.MANA_SCALE), 4 / 12f)),
                8192);
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_DIAMOND_KNOT;
        }
    }

    public static class CrackedDiamond extends KnotItem {
        public CrackedDiamond() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(16384 * ManaPool.MANA_SCALE), 4 / 12f / 6f)),
                Float.MAX_VALUE);
        }
    }

    public static class Echo extends KnotItem implements ChannelItem {
        public Echo() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768 * ManaPool.MANA_SCALE), 1)),
                65536);
        }

        @Override
        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            var pool = new SimpleManaPool(32768 * ManaPool.MANA_SCALE);
            var uuid = ModGlobalComponents.SHARED_MANA.get(world.getScoreboard()).allocate(pool);
            stack.set(ModComponents.MANA, new ManaComponent(new SharedManaPool(uuid), 1));
            stack.increment(1);
            return stack;
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            var uuid = stack.get(ModComponents.MANA).pool() instanceof SharedManaPool pool ? pool.uuid() : UUID.randomUUID();
            return new MessageListenerSpellExecutor(ctx.state(), timeout, Optional.of(new Key.Channel(uuid)));
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            if (stack.get(ModComponents.MANA).pool() instanceof SharedManaPool pool) {
                ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(new Key.Channel(pool.uuid()), value);
            }
        }

        public int getRange() {
            return 16;
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_ECHO_KNOT;
        }
    }

    public static class CrackedEcho extends KnotItem implements ChannelItem {
        public CrackedEcho() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(32768 * ManaPool.MANA_SCALE), 1 / 6f)),
                Float.MAX_VALUE);
        }

        @Override
        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            var uuid = UUID.randomUUID();
            ctx.source().getPlayer().ifPresent(player -> ModNetworking.CHANNEL.serverHandle(player).send(new EchoGrabClipboardPacket(uuid)));
            return new MessageListenerSpellExecutor(ctx.state(), timeout, Optional.of(new Key.Channel(uuid)));
        }

        @Override
        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            ctx.source().getPlayer().ifPresent(player -> ModNetworking.CHANNEL.serverHandle(player).send(new EchoSetClipboardPacket(value)));
        }

        @Override
        public int getRange() {
            return 16;
        }
    }

    public static class Astral extends KnotItem {
        public Astral() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SavingsManaPool(1048576 * ManaPool.MANA_SCALE, (float) Math.pow(2, -20)), 0)),
                524288);
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_ASTRAL_KNOT;
        }
    }

    public static class CrackedAstral extends KnotItem {
        public CrackedAstral() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SavingsManaPool(16777216 * ManaPool.MANA_SCALE, (float) -Math.pow(2, -20)), 0)),
                Float.MAX_VALUE);
        }
    }

    public static class Command extends KnotItem {
        public Command() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(InfiniteManaPool.INSTANCE, 0)),
                Float.MAX_VALUE);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.contains(ModComponents.MANA);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        var world = worldGetter.get();
        var manaComponent = stack.get(ModComponents.MANA);
        if (manaComponent == null || world == null) {
            return 0xff00ff;
        }

        subscribeConsumer.accept(manaComponent);

        return manaComponent.pool().getVariant(world).getMana().getColor();
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var world = worldGetter.get();
        var manaComponent = stack.get(ModComponents.MANA);
        if (manaComponent == null || world == null) {
            return 0;
        }

        subscribeConsumer.accept(manaComponent);

        float poolMax = manaComponent.pool().getMax(world);
        return poolMax == 0 ? 0
            : MathHelper.clamp(
                Math.round(manaComponent.pool().get(world) * 13.0F / poolMax),
                0, 13);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && otherStack.isOf(ModItems.ESOTERIC_MANA)) {
            var component = stack.get(ModComponents.MANA);
            if (component == null) {
                return false;
            }

            var pool = component.pool().makeClone(player.getWorld());
            if (pool.refill(ManaVariant.of(Manae.TRADITIONAL), 512 * ManaPool.MANA_SCALE, player.getWorld()) < 512 * ManaPool.MANA_SCALE) {
                stack.set(ModComponents.MANA, component.with(pool));

                otherStack.decrement(1);
                cursorStackReference.set(otherStack);

                var outputStack = Items.GLASS_BOTTLE.getDefaultStack();
                if (!player.getInventory().insertStack(outputStack)) {
                    player.dropItem(outputStack, false);
                }

                player.getWorld().playSoundFromEntity(
                    player, player, SoundEvents.ITEM_HONEY_BOTTLE_DRINK,
                    SoundCategory.PLAYERS, 1.0F, 1.0F
                );

                return true;
            }
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public float getCreationCost() {
        return creationCost;
    }

    public ItemStack createStack(World world) {
        return getDefaultStack();
    }

    public @Nullable KnotItem getCrackedVersion() {
        return null;
    }

    public ItemStack transferPropertiesToCracked(World world, ItemStack self, ItemStack cracked) {
        if (self.contains(ModComponents.MANA) && cracked.contains(ModComponents.MANA)) {
            //noinspection DataFlowIssue
            var pool = self.get(ModComponents.MANA).pool();
            //noinspection DataFlowIssue
            var newPool = cracked.get(ModComponents.MANA).pool().makeClone(world);
            newPool.set(pool.get(world), world);
            newPool.setVariant(pool.get(world) > 0 ? pool.getVariant(world) : ManaVariant.EMPTY, world);
            cracked.set(ModComponents.MANA, new ManaComponent(newPool));
        }

        return cracked;
    }

    public float getConstructExecutionLimitMultiplier(ItemStack stack) {
        return 1;
    }
}
