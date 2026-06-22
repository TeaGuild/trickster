package dev.enjarai.trickster.data.conversion;

import net.minecraft.registry.RegistryWrapper;

public class ErodeConversionLoader extends SimpleBlockConversionLoader {
    public ErodeConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super("erode", registryLookup);
    }
}
