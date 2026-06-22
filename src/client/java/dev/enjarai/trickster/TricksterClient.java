package dev.enjarai.trickster;

import dev.enjarai.trickster.block.ColorBlock;
import dev.enjarai.trickster.block.ColorBlockEntity;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.coleus.ColeusIntegration;
import dev.enjarai.trickster.compat.ModCompat;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.compat.inline.InlineCompat;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.particle.LightningParticle;
import dev.enjarai.trickster.render.entity.LevitatingBlockEntityRenderer;
import dev.enjarai.trickster.render.fleck.FleckRenderer;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.net.IsEditingScrollPacket;
import dev.enjarai.trickster.net.ModClientNetworking;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.particle.ProtectedBlockParticle;
import dev.enjarai.trickster.particle.SpellParticle;
import dev.enjarai.trickster.render.*;
import dev.enjarai.trickster.render.fragment.FragmentRenderer;
import dev.enjarai.trickster.screen.ModHandledScreens;
import dev.enjarai.trickster.screen.ScrollAndQuillScreen;
import dev.enjarai.trickster.screen.SignScrollScreen;
import dev.enjarai.trickster.screen.md.ObfuscatedFeature;
import dev.enjarai.trickster.screen.owo.GlyphComponent;
import dev.enjarai.trickster.screen.owo.ItemTagComponent;
import dev.enjarai.trickster.screen.owo.SpellPreviewComponent;
import dev.enjarai.trickster.screen.owo.TrickOverviewComponent;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtIntArray;

import java.util.List;

public class TricksterClient implements ClientModInitializer {
    public static final MerlinKeeperTracker merlinKeeperTracker = new MerlinKeeperTracker(20);

    @Override
    public void onInitializeClient() {
        ScrollAndQuillItem.screenOpener = (text, hand) -> {
            MinecraftClient.getInstance().setScreen(new SignScrollScreen(text, hand));
        };

        ModContainer modContainer = FabricLoader.getInstance().getModContainer(Trickster.MOD_ID).orElseThrow(() -> new IllegalStateException("Trickster's ModContainer couldn't be found!"));
        ResourceManagerHelper.registerBuiltinResourcePack(Trickster.id("trickster_accents"), modContainer, ResourcePackActivationType.NORMAL);

        FleckRenderer.register();
        FragmentRenderer.register();

        ModHandledScreens.register();
        ModKeyBindings.register();
        ModClientNetworking.register();

        BlockEntityRendererFactories.register(ModBlocks.SPELL_CONSTRUCT_ENTITY, SpellConstructBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.MODULAR_SPELL_CONSTRUCT_ENTITY,
            ModularSpellConstructBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.SCROLL_SHELF_ENTITY, ScrollShelfBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.CHARGING_ARRAY_ENTITY, ChargingArrayBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.LEVITATING_BLOCK, LevitatingBlockEntityRenderer::new);

        UIParsing.registerFactory(Trickster.id("trick-overview"), TrickOverviewComponent::parse);
        UIParsing.registerFactory(Trickster.id("glyph"), GlyphComponent::parseTrick);
        UIParsing.registerFactory(Trickster.id("pattern"), GlyphComponent::parseList);
        UIParsing.registerFactory(Trickster.id("spell-preview"), SpellPreviewComponent::parse);
        UIParsing.registerFactory(Trickster.id("item-tag"), ItemTagComponent::parse);

        LavenderBookScreen.registerFeatureFactory(Trickster.id("guidebook"),
            componentSource -> List.of(new ObfuscatedFeature()));

        ParticleFactoryRegistry.getInstance().register(ModParticles.HIGHLIGHT_BLOCK, ProtectedBlockParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPELL, SpellParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.STORM, SpellParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING, LightningParticle.Factory::new);

        AccessoriesRendererRegistry.registerRenderer(ModItems.TOP_HAT, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.WITCH_HAT, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.FEZ, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.COLLAR, CollarRenderer::new);
        AccessoriesRendererRegistry.registerNoRenderer(ModItems.MACRO_RING);
        AccessoriesRendererRegistry.registerNoRenderer(ModItems.AMETHYST_WHORL);
        AccessoriesRendererRegistry.registerNoRenderer(ModItems.TORMENT_ON_A_CHAIN);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPELL_RESONATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INERT_SPAWNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LIGHT, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COLOR_BLOCK, RenderLayer.getTranslucent());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                var editing = client.currentScreen instanceof ScrollAndQuillScreen;
                var serverEditing = ModEntityComponents.IS_EDITING_SCROLL.get(client.player).isEditing();

                if (editing != serverEditing) {
                    ModNetworking.CHANNEL.clientHandle()
                        .send(new IsEditingScrollPacket(editing, editing
                            ? ((ScrollAndQuillScreen) client.currentScreen).getScreenHandler().isOffhand()
                            : false));
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(merlinKeeperTracker::tick);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.world.getTime() % 20 == 0
                && client.player != null
                && (client.player.getMainHandStack().isIn(ModItems.TICK_TOCK) || client.player.getOffHandStack().isIn(ModItems.TICK_TOCK))) {
                client.player.playSound(ModSounds.KNOT_TICK, 0.1f, client.world.getTime() % 40 == 0 ? 2 : 1.7f);
            }
        });

        Trickster.merlinTooltipAppender = merlinKeeperTracker;
        KnotItem.worldGetter = () -> MinecraftClient.getInstance().world;
        KnotItem.subscribeConsumer = ClientUtils::trySubscribe;

        WorldRenderEvents.AFTER_ENTITIES.register(FlecksRenderer::render);

        HudRenderCallback.EVENT.register(BarsRenderer::render);
        HudRenderCallback.EVENT.register(SpellConstructLookRenderer::render);

        EntityModelLayerRegistry.registerModelLayer(ScrollShelfBlockEntityRenderer.MODEL_LAYER,
            ScrollShelfBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModularSpellConstructBlockEntityRenderer.MODEL_LAYER,
            ModularSpellConstructBlockEntityRenderer::getTexturedModelData);

        if (ModCompat.INLINE_LOADED) InlineCompat.register();
        if (FabricLoader.getInstance().isModLoaded("coleus")) {
            ColeusIntegration.init();
        }

        ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> {
            if (world == null || pos == null) {
                return ColorBlockEntity.DEFAULT_COLOR;
            }
            if (world.getBlockEntityRenderData(pos) instanceof int[] colors) {
                var time = (pos.getX() + pos.getY() + pos.getZ()) / 20f;
                return ColorBlock.getColor(colors, time);
            }
            return ColorBlockEntity.DEFAULT_COLOR;
        }, ModBlocks.COLOR_BLOCK);
        ColorProviderRegistry.ITEM.register(
            (stack, tintIndex) -> {
                if (stack.getComponents().get(DataComponentTypes.BLOCK_ENTITY_DATA) instanceof NbtComponent nbt
                    && nbt.getNbt().get("colors") instanceof NbtIntArray colorNBT) {
                    return ColorBlock.getColor(colorNBT.getIntArray(), MinecraftClient.getInstance().world.getTime() / 50f);
                }

                return ColorBlockEntity.DEFAULT_COLOR;
            }, ModItems.COLOR_BLOCK);
    }
}
