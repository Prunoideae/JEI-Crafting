package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.data.CraftItemPayload;
import moe.wolfgirl.jeicrafting.data.PlayerResourceType;
import moe.wolfgirl.jeicrafting.data.UpdateResourcePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registry = event.registrar("1").optional();

        registry.playToServer(CraftItemPayload.TYPE, CraftItemPayload.STREAM_CODEC, CraftItemPayload::handle);
        registry.playToClient(UpdateResourcePayload.TYPE, UpdateResourcePayload.STREAM_CODEC, UpdateResourcePayload::handle);
    }

    @SubscribeEvent
    public static void registerResources(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PlayerResourceType.REGISTRY, PlayerResourceType.DIRECT_CODEC, PlayerResourceType.DIRECT_CODEC);
    }
}
