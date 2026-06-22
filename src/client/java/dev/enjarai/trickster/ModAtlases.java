package dev.enjarai.trickster;

import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.render.ModularSpellConstructBlockEntityRenderer;
import dev.enjarai.trickster.render.ScrollShelfBlockEntityRenderer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ModAtlases {
    public static void register() {
        var helper = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
        helper.registerReloadListener(ScrollShelf.INSTANCE);
        helper.registerReloadListener(ModularSpellConstruct.INSTANCE);
        helper.registerReloadListener(Fragment.INSTANCE);
    }

    public static class ScrollShelf extends SpriteAtlasHolder implements IdentifiableResourceReloadListener {
        public static final Identifier ID = Trickster.id("scroll_shelf");
        public static final ScrollShelf INSTANCE = new ScrollShelf();

        public ScrollShelf() {
            super(
                MinecraftClient.getInstance().getTextureManager(),
                ScrollShelfBlockEntityRenderer.ATLAS_ID, ID
            );
        }

        @Override
        public Sprite getSprite(Identifier objectId) {
            return super.getSprite(objectId);
        }

        @Override
        public Identifier getFabricId() {
            return ID;
        }
    }

    public static class ModularSpellConstruct extends SpriteAtlasHolder implements IdentifiableResourceReloadListener {
        public static final Identifier ID = Trickster.id("modular_spell_construct");
        public static final ModularSpellConstruct INSTANCE = new ModularSpellConstruct();

        public ModularSpellConstruct() {
            super(
                MinecraftClient.getInstance().getTextureManager(),
                ModularSpellConstructBlockEntityRenderer.ATLAS_ID, ID
            );
        }

        @Override
        public Sprite getSprite(Identifier objectId) {
            return super.getSprite(objectId);
        }

        @Override
        public Identifier getFabricId() {
            return ID;
        }
    }

    public static class Fragment extends SpriteAtlasHolder implements IdentifiableResourceReloadListener {
        public static final Identifier ID = Trickster.id("fragment");
        public static final Fragment INSTANCE = new Fragment();

        public Fragment() {
            super(
                MinecraftClient.getInstance().getTextureManager(),
                CircleRenderer.FRAGMENT_ATLAS, ID
            );
        }

        @Override
        public Sprite getSprite(Identifier objectId) {
            return super.getSprite(objectId);
        }

        @Override
        public Identifier getFabricId() {
            return ID;
        }
    }
}
