package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.spell.fragment.ColorFragment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class LightningParticle extends SpriteBillboardParticle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final long seed;

    protected LightningParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, int c, double targetX, double targetY, double targetZ) {
        super(world, x, y, z, dx, dy, dz);
        maxAge = 8;
        //        scale = 1 / 16f * 8.5f;
        var color = ColorFragment.argbAsVector(c);
        alpha = color.w;
        red = color.x;
        green = color.y;
        blue = color.z;

        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;

        this.seed = this.random.nextLong();
    }

    @Override
    public void move(double dx, double dy, double dz) {

    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        var cameraPos = camera.getPos();
        var gStart = new Vector3d(prevPosX, prevPosY, prevPosZ).lerp(new Vector3d(x, y, z), tickDelta);
        var gEnd = gStart.add(targetX, targetY, targetZ, new Vector3d());
        gStart.sub(cameraPos.x, cameraPos.y, cameraPos.z);
        gEnd.sub(cameraPos.x, cameraPos.y, cameraPos.z);

        var start = new Vector3f((float) gStart.x, (float) gStart.y, (float) gStart.z);
        var end = new Vector3f((float) gEnd.x, (float) gEnd.y, (float) gEnd.z);
        var direction = end.sub(start, new Vector3f()).normalize();
        var normal = start.normalize(new Vector3f()).negate();
        var side = normal.cross(direction, new Vector3f()).normalize();

        random.setSeed(seed);

        var breaks = random.nextBetween(3, 6);
        var step = end.distance(start) / breaks;

        var sectionStart = start;
        for (int i = 0; i < breaks; i++) {
            var sectionEnd = direction.mul(step * (i + 1), new Vector3f()).add(start);

            if (i + 1 != breaks) {
                sectionEnd
                    .add(direction.mul(step * random.nextFloat() * 0.3f, new Vector3f()))
                    .add(normal.mul(step * random.nextFloat() * 0.6f, new Vector3f()))
                    .add(side.mul(step * random.nextFloat() * 0.6f, new Vector3f()));
            }

            drawSegment(
                vertexConsumer, sectionStart, sectionEnd,
                sectionStart.normalize(new Vector3f()).negate(),
                tickDelta, camera, 0.01f, 0
            );

            sectionStart = sectionEnd;
        }
    }

    private void drawSegment(VertexConsumer vertexConsumer, Vector3f start, Vector3f end, Vector3f normal, float tickDelta, Camera camera, float width, int i) {
        var direction = end.sub(start, new Vector3f()).normalize();
        var side = normal.cross(direction).normalize().mul(width);

        vertexConsumer.vertex(side.add(start, new Vector3f()))
            .color(red, green, blue, getAlpha(tickDelta, i))
            .texture(getMinU(), getMaxV())
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE);

        vertexConsumer.vertex(side.negate(new Vector3f()).add(start))
            .color(red, green, blue, getAlpha(tickDelta, i))
            .texture(getMaxU(), getMaxV())
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE);

        vertexConsumer.vertex(side.negate(new Vector3f()).add(end))
            .color(red, green, blue, getAlpha(tickDelta, i))
            .texture(getMaxU(), getMinV())
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE);

        vertexConsumer.vertex(side.add(end, new Vector3f()))
            .color(red, green, blue, getAlpha(tickDelta, i))
            .texture(getMinU(), getMinV())
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getAlpha(float tickDelta, int offset) {
        var a = Math.max(0, alpha - ((age - offset + tickDelta) / 6f * alpha));
        if (a > 1) {
            return 1;
        }
        return a;
    }

    @Override
    protected int getBrightness(float tint) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<LightningParticleOptions> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(LightningParticleOptions options, ClientWorld clientWorld, double x, double y, double z, double dx, double dy, double dz) {
            var particle = new LightningParticle(
                clientWorld, x, y, z, dx, dy, dz,
                options.color(), options.targetX(), options.targetY(), options.targetZ()
            );
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
