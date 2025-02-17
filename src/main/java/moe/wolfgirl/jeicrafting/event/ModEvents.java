package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.network.CraftItemPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registry = event.registrar("1").optional();

        registry.playToServer(CraftItemPayload.TYPE, CraftItemPayload.STREAM_CODEC, CraftItemPayload::handle);
    }
}
