package moe.wolfgirl.jeicrafting.data;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.game.ClientState;
import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record UpdateResourcePayload(PlayerResources holding) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateResourcePayload> TYPE = new Type<>(GameUtil.id("update_resources"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateResourcePayload> STREAM_CODEC = PlayerResources.STREAM_CODEC
            .map(UpdateResourcePayload::new, UpdateResourcePayload::holding);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        ClientState.RESOURCES.clear();
        ClientState.RESOURCES.putAll(holding.holding());

        ClientState.RESOURCE_STACKS.clear();
        context.player().registryAccess().lookup(PlayerResourceType.REGISTRY).ifPresent(lookup -> {
            for (Map.Entry<ResourceKey<PlayerResourceType>, Integer> entry : ClientState.RESOURCES.entrySet()) {
                ResourceKey<PlayerResourceType> key = entry.getKey();
                Integer amount = entry.getValue();
                lookup.get(key).ifPresent(value -> ClientState.RESOURCE_STACKS.add(Pair.of(value.value().representativeItem(), amount)));
            }
        });
    }
}
