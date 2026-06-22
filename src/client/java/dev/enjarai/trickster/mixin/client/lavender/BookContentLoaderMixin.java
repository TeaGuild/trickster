package dev.enjarai.trickster.mixin.client.lavender;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.pond.TexturedLavenderThing;
import io.wispforest.lavender.Lavender;
import io.wispforest.lavender.book.Book;
import io.wispforest.lavender.book.BookContentLoader;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.book.Entry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

@Mixin(BookContentLoader.class)
public class BookContentLoaderMixin {
    @Shadow
    @Final
    private static Gson GSON;

    @ModifyArg(
        method = "lambda$reloadContents$0", at = @At(
            value = "INVOKE", target = "Lio/wispforest/lavender/book/Book;addCategory(Lio/wispforest/lavender/book/Category;)V"
        )
    )
    private static Category loadCategoryTexture(Category category, @Local(argsOnly = true) Identifier identifier, @Local(argsOnly = true) Resource resource) {
        var meta = parseFrontmatter(identifier, resource);
        var textured = (TexturedLavenderThing) (Object) category;

        if (meta != null && meta.has("trickster:page_texture")) {
            textured.trickster$setTexture(Identifier.of(meta.get("trickster:page_texture").getAsString()));
        }

        return category;
    }

    @Inject(
        method = "lambda$reloadContents$1", at = @At(
            value = "INVOKE", target = "Lio/wispforest/lavender/book/Entry;id()Lnet/minecraft/util/Identifier;"
        )
    )
    private static void loadEntryTexture(Book book, Identifier identifier, Resource resource, CallbackInfo ci, @Local(name = "entry") Entry entry) {
        var meta = parseFrontmatter(identifier, resource);
        var textured = (TexturedLavenderThing) (Object) entry;

        if (meta != null && meta.has("trickster:page_texture")) {
            textured.trickster$setTexture(Identifier.of(meta.get("trickster:page_texture").getAsString()));
        }
    }

    // I hate that we need to do this, but i cant think of a better solution with all the private stuff around.
    @Unique
    private static @Nullable JsonObject parseFrontmatter(Identifier resourceId, Resource resource) {
        try {
            var content = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8).strip();
            JsonObject meta;

            if (content.startsWith("```json")) {
                content = content.substring("```json".length());
                int frontmatterEnd = content.indexOf("```");
                if (frontmatterEnd == -1) {
                    throw new RuntimeException("Unterminated markdown meta");
                }

                meta = GSON.fromJson(content.substring(0, frontmatterEnd), JsonObject.class);

                return meta;
            } else {
                throw new RuntimeException("Missing markdown meta");
            }
        } catch (Exception e) {
            Lavender.LOGGER.warn("Could not load markdown file {}", resourceId, e);
            return null;
        }
    }
}
