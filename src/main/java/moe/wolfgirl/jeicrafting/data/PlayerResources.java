package moe.wolfgirl.jeicrafting.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Represents abstract resources that a player hold, and can consume in JEI Crafting
// E.g. "Wood", "Steel", "Stone", or "Cash"
// Is not constrained by inventory and can have any amount in double
public record PlayerResources(Map<ResourceKey<PlayerResourceType>, Integer> holding) {
    public static final Codec<PlayerResources> CODEC = PlayerResource.CODEC
            .listOf()
            .xmap(PlayerResources::fromList, PlayerResources::toList);

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResources> STREAM_CODEC = PlayerResource.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(PlayerResources::fromList, PlayerResources::toList);


    public record PlayerResource(ResourceKey<PlayerResourceType> id, int amount) {
        public static final Codec<PlayerResource> CODEC = RecordCodecBuilder.create(
                data -> data.group(
                        ResourceKey.codec(PlayerResourceType.REGISTRY).fieldOf("id").forGetter(PlayerResource::id),
                        Codec.INT.optionalFieldOf("amount", 1000000).forGetter(PlayerResource::amount)
                ).apply(data, PlayerResource::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerResource> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(PlayerResourceType.REGISTRY), PlayerResource::id,
                ByteBufCodecs.VAR_INT, PlayerResource::amount,
                PlayerResource::new
        );
    }

    public static PlayerResources fromList(List<PlayerResource> resources) {
        Map<ResourceKey<PlayerResourceType>, Integer> current = new HashMap<>();
        for (PlayerResource resource : resources) {
            current.put(resource.id, resource.amount);
        }
        return new PlayerResources(current);
    }

    private List<PlayerResource> toList() {
        List<PlayerResource> resources = new ArrayList<>();
        for (Map.Entry<ResourceKey<PlayerResourceType>, Integer> entry : holding.entrySet()) {
            resources.add(new PlayerResource(entry.getKey(), entry.getValue()));
        }
        return resources;
    }

    public void notifyClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new UpdateResourcePayload(this));
    }
}
