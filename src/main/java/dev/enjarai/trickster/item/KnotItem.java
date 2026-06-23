package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.DesiredColorComponent;
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
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.mana.*;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ColorComponent;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.mana.type.component.StabilityComponent;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.util.ColorUtil;
import dev.enjarai.trickster.util.StarUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ClickType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
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

    private static final ComponentChanges REMOVE_MANA = ComponentChanges.builder().remove(ModComponents.MANA).build();

    private final float creationCost;

    public KnotItem(Settings settings, float creationCost) {
        super(settings.maxCount(1));
        this.creationCost = creationCost;
    }

    public static class Amethyst extends KnotItem {
        public Amethyst() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(128 * ManaPool.MANA_SCALE))),
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
                .component(ModComponents.MANA, new ManaComponent(SimpleManaPool.getSingleUse(256 * ManaPool.MANA_SCALE))),
                Float.MAX_VALUE);
        }
    }

    public static class Quartz extends KnotItem implements ChannelItem {
        public Quartz() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(128 * ManaPool.MANA_SCALE), 1 / 96f))
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
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(0))),
                Float.MAX_VALUE);
        }

        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            return new ListFragment(List.of(new NumberFragment(ctx.source().getWorld().getTimeOfDay())));
        }

        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            // Can't change its time offset anymore
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
    }

    public static class Astral extends KnotItem implements ConverterItem {
        public Astral() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SavingsManaPool(1048576 * ManaPool.MANA_SCALE, (float) Math.pow(2, -20)))),
                524288);
        }

        @Override
        public @Nullable Mana convert(SpellContext ctx, ItemStack stack) {
            var world = ctx.source().getWorld();

            var comp = stack.get(ModComponents.MANA);
            if (comp == null) {
                return null;
            }

            var variant = comp.pool().getVariant(world);
            if (variant.isOf(Manae.ASTRAL)) {
                var pool = comp.pool().makeClone(world);
                pool.setVariant(ManaVariant.of(Manae.TRADITIONAL), world);
                pool.set((long) (0.8 * pool.get(world)), world);
                stack.set(ModComponents.MANA, comp.with(pool));
                return Manae.TRADITIONAL;
            } else if (variant.isOf(Manae.TRADITIONAL)) {
                var pool = comp.pool().makeClone(world);
                pool.setVariant(ManaVariant.of(Manae.ASTRAL, ComponentChanges.builder().add(ManaComponents.NAME, StarUtil.getNext()).build()), world);
                pool.set((long) (0.8 * pool.get(world)), world);
                stack.set(ModComponents.MANA, comp.with(pool));
                return Manae.ASTRAL;
            }

            return null;
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_ASTRAL_KNOT;
        }
    }

    public static class CrackedAstral extends KnotItem {
        public CrackedAstral() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SavingsManaPool(16777216 * ManaPool.MANA_SCALE, (float) -Math.pow(2, -20)))),
                Float.MAX_VALUE);
        }
    }

    public static class Prismatic extends KnotItem implements ChannelItem, ConverterItem {
        // Need to use a static list here for dye depot compat :sob:
        private static final int[] possibleColors = new int[] {
            DyeColor.RED.getEntityColor(),
            DyeColor.ORANGE.getEntityColor(),
            DyeColor.YELLOW.getEntityColor(),
            DyeColor.LIME.getEntityColor(),
            DyeColor.GREEN.getEntityColor(),
            DyeColor.CYAN.getEntityColor(),
            DyeColor.LIGHT_BLUE.getEntityColor(),
            DyeColor.BLUE.getEntityColor(),
            DyeColor.PURPLE.getEntityColor(),
            DyeColor.MAGENTA.getEntityColor(),
        };

        public Prismatic() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new SimpleManaPool(1024 * ManaPool.MANA_SCALE), 1 / 12f)),
                8192);
        }

        @Override
        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(computeDesired(world)));
            return stack;
        }

        @Override
        public @Nullable Mana convert(SpellContext ctx, ItemStack stack) {
            var world = ctx.source().getWorld();
            var comp = stack.get(ModComponents.MANA);
            var variant = comp.pool().getVariant(world);

            if (variant.isOf(Manae.COLOR)) {
                int desiredColor;
                var desiredComp = stack.get(ModComponents.DESIRED_COLOR);
                if (desiredComp == null) {
                    desiredColor = computeDesired(world);
                } else {
                    desiredColor = desiredComp.color();
                }

                var pool = comp.pool().makeClone(world);
                var amount = pool.get(world);

                double dist = ColorUtil.distance(variant.getComponentMap().get(ManaComponents.COLOR).color(), desiredColor);

                if (dist <= 0.1) {
                    long newAmount = (long) (amount * (1 - 10 * dist));

                    pool.setVariant(ManaVariant.of(Manae.TRADITIONAL), world);
                    pool.set(newAmount, world);

                    stack.set(ModComponents.MANA, comp.with(pool));
                    var chosenColor = computeDesired(ctx.source().getWorld());
                    stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));

                    return Manae.TRADITIONAL;
                } else {
                    pool.setVariant(ManaVariant.of(Manae.IMPOTENT), world);
                    pool.set(pool.getMax(world), world);

                    stack.set(ModComponents.MANA, comp.with(pool));
                    var chosenColor = computeDesired(ctx.source().getWorld());
                    stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));

                    return Manae.IMPOTENT;
                }
            }

            return null;
        }

        @Override
        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            var component = stack.get(ModComponents.DESIRED_COLOR);
            if (component != null) {
                return ColorFragment.fromRGB(component.color());
            }

            var chosenColor = computeDesired(ctx.source().getWorld());
            stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));
            return new ListFragment(List.of(ColorFragment.fromRGB(chosenColor)));
        }

        @Override
        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            // meows at you
        }

        @Override
        public @Nullable KnotItem getCrackedVersion() {
            return ModItems.CRACKED_PRISMATIC_KNOT;
        }

        private int computeDesired(World world) {
            return possibleColors[world.getRandom().nextInt(possibleColors.length)];
        }

        @Override
        public float tryRecharge(World world, Vec3d pos, ItemStack stack) {
            if (!world.isDay() || world.isRaining() || world.isThundering()) {
                return 0;
            }

            var timeOfDay = world.getTimeOfDay() % 24000;
            if (timeOfDay > 12000) {
                return 0;
            }

            var component = stack.get(ModComponents.MANA);
            if (component == null) {
                return 0;
            }

            var pool = component.pool();
            if (pool.get(world) >= pool.getMax(world)) {
                return 0;
            }

            // Require skylight access
            var noSky = world.getLightLevel(LightType.SKY, BlockPos.ofFloored(pos)) < 15;
            if (noSky) {
                return 0;
            }

            // Mana must already be color or blank
            var currentVariant = pool.getVariant(world);
            if (!currentVariant.isOf(Manae.COLOR) && !currentVariant.isBlank()) {
                return 0;
            }

            var chargeMultiplier = 8f;
            chargeMultiplier *= component.naturalRechargeMultiplier();

            var mana = Manae.COLOR;
            var newPool = pool.makeClone(world);

            var segmentSize = (12000 / (possibleColors.length - 1));
            int index = Math.toIntExact(timeOfDay / segmentSize);
            var firstLightColor = possibleColors[Math.min(index, possibleColors.length - 1)];
            var secondLightColor = possibleColors[Math.min(index + 1, possibleColors.length - 1)];

            var segmentProgress = timeOfDay % segmentSize;
            var currentLightColor = ColorUtil.mixColors(firstLightColor, segmentSize - segmentProgress, secondLightColor, segmentProgress);

            var insertingVariant = ManaVariant.of(mana).withComponentChanges(
                ComponentChanges.builder().add(ManaComponents.COLOR, ColorComponent.fromInt(currentLightColor)).build());
            var targetVariant = insertingVariant;
            var amountToInsert = (long) (chargeMultiplier * ManaPool.MANA_SCALE);

            if (!currentVariant.isBlank()) {
                var mixed = Manae.COLOR.mix(world, targetVariant, amountToInsert, currentVariant, newPool.get(world));
                if (mixed.isEmpty()) {
                    return 0;
                }
                targetVariant = mixed.get();

                newPool.setVariant(targetVariant, world);
            }

            var shouldMakeParticles = 0 == newPool.refill(targetVariant, amountToInsert, world);
            stack.set(ModComponents.MANA, component.with(newPool));

            if (shouldMakeParticles) {
                var particleVelocity = Math.ceil(chargeMultiplier / 2f);

                var recharged = chargeMultiplier - world.random.nextFloat();
                while (recharged > 0) {
                    world.addParticle(
                        insertingVariant.getParticle(),
                        pos.getX(), pos.getY(), pos.getZ(),
                        (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity,
                        (world.random.nextFloat() * 0.02f + 0.01f) * particleVelocity,
                        (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity
                    );
                    recharged -= world.random.nextFloat();
                }
            }

            return chargeMultiplier;
        }
    }

    public static class CrackedPrismatic extends KnotItem implements ChannelItem, ConverterItem {
        // Oh and another static list here cause this ones different :3
        private static final int[] possibleColors = new int[] {
            DyeColor.WHITE.getEntityColor(),
            DyeColor.ORANGE.getEntityColor(),
            DyeColor.MAGENTA.getEntityColor(),
            DyeColor.LIGHT_BLUE.getEntityColor(),
            DyeColor.YELLOW.getEntityColor(),
            DyeColor.LIME.getEntityColor(),
            DyeColor.PINK.getEntityColor(),
            DyeColor.GRAY.getEntityColor(),
            DyeColor.LIGHT_GRAY.getEntityColor(),
            DyeColor.CYAN.getEntityColor(),
            DyeColor.PURPLE.getEntityColor(),
            DyeColor.BLUE.getEntityColor(),
            DyeColor.BROWN.getEntityColor(),
            DyeColor.GREEN.getEntityColor(),
            DyeColor.RED.getEntityColor(),
            DyeColor.BLACK.getEntityColor()
        };

        public CrackedPrismatic() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(new OverfillingManaPool(1024 * ManaPool.MANA_SCALE))),
                Float.MAX_VALUE);
        }

        @Override
        public ItemStack createStack(World world) {
            var stack = getDefaultStack();
            stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(computeDesired(world)));
            return stack;
        }

        @Override
        public @Nullable Mana convert(SpellContext ctx, ItemStack stack) {
            var world = ctx.source().getWorld();
            var comp = stack.get(ModComponents.MANA);
            var variant = comp.pool().getVariant(world);

            if (variant.isOf(Manae.COLOR)) {
                int desiredColor;
                var desiredComp = stack.get(ModComponents.DESIRED_COLOR);
                if (desiredComp == null) {
                    desiredColor = computeDesired(world);
                } else {
                    desiredColor = desiredComp.color();
                }

                var pool = comp.pool().makeClone(world);
                var amount = pool.get(world);

                double dist = ColorUtil.distance(variant.getComponentMap().get(ManaComponents.COLOR).color(), desiredColor);

                if (dist <= 0.1) {
                    long newAmount = (long) (amount * 16.0 * (1 - 10 * dist));

                    pool.setVariant(ManaVariant.of(Manae.TRADITIONAL), world);
                    pool.set(newAmount, world);

                    stack.set(ModComponents.MANA, comp.with(pool));
                    var chosenColor = computeDesired(ctx.source().getWorld());
                    stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));

                    return Manae.TRADITIONAL;
                } else {
                    pool.setVariant(ManaVariant.of(Manae.IMPOTENT), world);
                    pool.set(pool.getMax(world), world);

                    stack.set(ModComponents.MANA, comp.with(pool));
                    var chosenColor = computeDesired(ctx.source().getWorld());
                    stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));

                    return Manae.IMPOTENT;
                }
            }

            return null;
        }

        @Override
        public EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Optional<Integer> timeout) {
            var component = stack.get(ModComponents.DESIRED_COLOR);
            if (component != null) {
                return ColorFragment.fromRGB(component.color());
            }

            var chosenColor = computeDesired(ctx.source().getWorld());
            stack.set(ModComponents.DESIRED_COLOR, new DesiredColorComponent(chosenColor));
            return new ListFragment(List.of(ColorFragment.fromRGB(chosenColor)));
        }

        @Override
        public void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value) {
            // meows at you meows at you waffs at you
        }

        private int computeDesired(World world) {
            var random = world.getRandom();
            var chosenColor = possibleColors[random.nextInt(possibleColors.length)];
            var iterations = random.nextInt(16) + 1;

            for (int i = 0; i < iterations; i++) {
                chosenColor = ColorUtil.mixColors(chosenColor, 1, possibleColors[random.nextInt(possibleColors.length)], 1);
            }

            return chosenColor;
        }
    }

    public static class Command extends KnotItem {
        public Command() {
            super(new Settings()
                .component(ModComponents.MANA, new ManaComponent(InfiniteManaPool.INSTANCE)),
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

        return manaComponent.pool().getVariant(world).getColor();
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
                    player, player, ModSounds.USE_ESOTERIC_MANA,
                    SoundCategory.PLAYERS, 1.0F, 1.0F
                );

                return true;
            }
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public float tryRecharge(World world, Vec3d pos, ItemStack stack) {
        if (world.isDay()) {
            return 0;
        }

        if (world.isRaining() && !world.isThundering()) {
            return 0;
        }

        var component = stack.get(ModComponents.MANA);
        if (component == null) {
            return 0;
        }

        var pool = component.pool();
        if (pool.get(world) >= pool.getMax(world) && !world.isThundering()) {
            return 0;
        }

        // Require skylight access
        var noSky = world.getLightLevel(LightType.SKY, BlockPos.ofFloored(pos)) < 15;
        if (noSky) {
            return 0;
        }

        var moonSize = world.getMoonSize();
        // Recharge most strongly at new and full moons, with no recharging halfway inbetween
        // 20x and storm mana when thundering
        var chargeMultiplier = world.isThundering() ? 25 : Math.max(Math.max(moonSize * 2 - 1, 1 - moonSize * 2), 0);
        chargeMultiplier *= component.naturalRechargeMultiplier();

        var mana = world.isThundering() ? Manae.STORM : Manae.TRADITIONAL;
        var newPool = pool.makeClone(world);

        var variant = ManaVariant.of(mana);
        if (mana == Manae.STORM) {
            variant = variant.withComponentChanges(ComponentChanges.builder()
                .add(ManaComponents.STABILITY, new StabilityComponent(world.getTime())).build());

            if (newPool.getVariant(world).getMana() == Manae.STORM) {
                newPool.setVariant(variant, world);
            }
        }

        var shouldMakeParticles = 0 == newPool.refill(variant, (long) (chargeMultiplier * ManaPool.MANA_SCALE), world);
        stack.set(ModComponents.MANA, component.with(newPool));

        if (shouldMakeParticles) {
            var particleVelocity = Math.ceil(chargeMultiplier / 2f);
            var horizontalMultiplier = world.isThundering() ? 6 : 1;

            var recharged = chargeMultiplier - world.random.nextFloat();
            while (recharged > 0) {
                world.addParticle(
                    variant.getParticle(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity * horizontalMultiplier,
                    (world.random.nextFloat() * 0.02f + 0.01f) * particleVelocity,
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity * horizontalMultiplier
                );
                recharged -= world.random.nextFloat();
            }
        }

        return chargeMultiplier;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof ServerPlayerEntity player) {
            var comp = stack.get(ModComponents.MANA);
            if (comp == null) {
                return;
            }

            var variant = comp.pool().getVariant(world);
            if (!variant.isBlank()) {
                ModCriteria.MANA_TYPE.trigger(player, variant.getMana());

                if (!variant.isOf(Manae.TRADITIONAL)) {
                    ModCriteria.UNUSUAL_MANA.trigger(player);
                }

                if (variant.isOf(Manae.STORM)) {
                    ModCriteria.STORM_MANA.trigger(player, variant.getComponentMap().get(ManaComponents.STABILITY).getStability(world));
                }
            }
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        var oldVariant = ItemVariant.of(oldStack).withComponentChanges(REMOVE_MANA);
        var newVariant = ItemVariant.of(newStack).withComponentChanges(REMOVE_MANA);

        return !oldVariant.equals(newVariant);
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
