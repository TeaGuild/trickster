package dev.enjarai.trickster.render.fleck;

import dev.enjarai.trickster.render.CircleRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import dev.enjarai.trickster.fleck.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpellFleckRenderer implements FleckRenderer<SpellFleck> {
    private final CircleRenderer renderer = new CircleRenderer(false, false, 6);

    @Override
    public void render(
        SpellFleck fleck, SpellFleck lastFleck, WorldRenderContext context, ClientWorld world,
        float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color
    ) {

        matrices.push();

        var position = fleck.pos().getPos(world, tickDelta);
        if (position == null) {
            matrices.pop();
            return;
        }

        var facing = fleck.pos().transformFacing(fleck.facing(), world, tickDelta);
        var spell = fleck.spell();
        var size = fleck.size();
        var roll = fleck.roll();

        var oldPosition = fleck.pos().getPos(world, tickDelta);
        if (oldPosition == null) {
            matrices.pop();
            return;
        }

        var oldFacing = fleck.pos().transformFacing(fleck.facing(), world, tickDelta);
        var oldSize = fleck.size();
        var oldRoll = fleck.roll();

        if (lastFleck != null) {
            oldPosition = lastFleck.pos().getPos(world, tickDelta);
            if (oldPosition == null) {
                matrices.pop();
                return;
            }

            oldFacing = lastFleck.pos().transformFacing(lastFleck.facing(), world, tickDelta);
            oldSize = lastFleck.size();
            oldRoll = lastFleck.roll();
        }

        //only lerp if change is small?
        //float lerpAmount = position.distance(oldPosition) > 1 ? 1.0f : tickDelta

        var targetPosition = oldPosition.lerp(position, tickDelta, new Vector3f()).sub(context.camera().getPos().toVector3f());
        var targetFacing = oldFacing.slerp(facing, tickDelta, new Quaternionf());
        var targetSize = MathHelper.lerp(tickDelta, oldSize, size);
        var targetRoll = MathHelper.lerp(tickDelta, oldRoll, roll);

        //        var yaw = (float) Math.atan2(targetFacing.x(), targetFacing.z());
        //        var pitch = (float) (Math.asin(-targetFacing.y()) + Math.PI);

        matrices.translate(targetPosition.x(), targetPosition.y(), targetPosition.z()); //translate over to the position of the spellfleck.
        //        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
        //        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch));
        matrices.multiply(targetFacing);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(targetRoll));

        var facingVec = new Vector3f(0, 0, 1).rotate(targetFacing);
        renderer.renderCircle(
            matrices,
            spell,
            0,
            0,
            targetSize / 2,
            0,
            tickDelta,
            1.0f,
            1.0f,
            new Vec3d(facingVec.x(), facingVec.y(), facingVec.z()),
            null
        );
        CircleRenderer.VERTEX_CONSUMERS.draw();

        matrices.pop();
    }
}
