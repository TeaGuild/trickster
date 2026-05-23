package dev.enjarai.trickster.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenlessLootableBlockEntity extends BlockEntity implements LootableInventory {

    @Nullable
    protected RegistryKey<LootTable> lootTable;
    protected long lootTableSeed = 0L;

    public ScreenlessLootableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @Nullable RegistryKey<LootTable> getLootTable() {
        return lootTable;
    }

    @Override
    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        if (this.lootTable != null) {
            componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readLootTable(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        writeLootTable(nbt);
    }
}
