package dev.enjarai.trickster.spell.mana;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.World;

public class SavingsManaPool extends SimpleManaPool {
    public static final StructEndec<SavingsManaPool> ENDEC = StructEndecBuilder.of(
        Endec.FLOAT.fieldOf("mana", pool -> pool.mana),
        Endec.FLOAT.fieldOf("max_mana", pool -> pool.maxMana),
        Endec.FLOAT.fieldOf("interest", pool -> pool.interest),
        Endec.LONG.fieldOf("last_update_time", pool -> pool.lastUpdateTime),
        SavingsManaPool::new
    );

    protected final float interest;
    protected long lastUpdateTime;
    protected long ticksPassedWhileFull;
    protected long waste;

    protected SavingsManaPool(float mana, float maxMana, float interest, long lastUpdateTime) {
        super(mana, maxMana);
        this.interest = interest;
        this.lastUpdateTime = lastUpdateTime;
    }

    // It's kinda important for this to be the only public constructor.
    // If lastUpdateTime is 0, mana should probably also be 0, or a lot of free interest will be given.
    public SavingsManaPool(float maxMana, float interest) {
        super(maxMana);
        this.interest = interest;
    }

    @Override
    public ManaPoolType<?> type() {
        return ManaPoolType.SAVINGS;
    }

    @Override
    public void set(float value, World world) {
        waste = computeWaste(world);
        lastUpdateTime = world.getTime();
        super.set(value, world);
    }

    @Override
    public float get(World world) {
        return (float) Math.clamp(getTheoretical(world), 0, getMax(world));
    }

    private float getTheoretical(World world) {
        long ticksPassed = world.getTime() - lastUpdateTime; // t
        float lastMana = super.get(world); // P

        //TODO: guh deal with waste

        // P(1 + i)^t
        return (float) (lastMana * Math.pow(1 + interest, ticksPassed));
    }

    private long computeChargingTicks(World world) {
        float lastMana = super.get(world);
        float maxMana = getMax(world);
        float theoreticalMana = getTheoretical(world);

        if (theoreticalMana < maxMana) return 0;

        return (long) (Math.log(maxMana / lastMana) / Math.log(1 + interest));
    }

    private long computeWaste(World world) {
        long ticksPassed = world.getTime() - lastUpdateTime;
        long ticksCharging = computeChargingTicks(world);
        long waste = Math.max(this.waste - ticksCharging, 0);
        waste += ticksPassed - ticksCharging;
        return waste;
    }

    @Override
    public MutableManaPool makeClone(World world) {
        return new SavingsManaPool(mana, maxMana, interest, lastUpdateTime);
    }
}
