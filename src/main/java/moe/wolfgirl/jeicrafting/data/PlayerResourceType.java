package moe.wolfgirl.jeicrafting.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.wolfgirl.jeicrafting.JEICrafting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record PlayerResourceType(ItemStack representativeItem, int max) {
    public static final ResourceKey<Registry<PlayerResourceType>> REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(JEICrafting.MODID, "resource_type"));

    public static final Codec<PlayerResourceType> DIRECT_CODEC = RecordCodecBuilder.create(
            data -> data.group(
                    ItemStack.CODEC.fieldOf("itemIcon").forGetter(PlayerResourceType::representativeItem),
                    Codec.INT.optionalFieldOf("max", 1000000).forGetter(PlayerResourceType::max)
            ).apply(data, PlayerResourceType::new)
    );
}
