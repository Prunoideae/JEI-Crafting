package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.render.ClientCraftingComponent;
import moe.wolfgirl.jeicrafting.render.CraftingComponent;
import moe.wolfgirl.jeicrafting.render.SpriteUploader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CraftingComponent.class, ClientCraftingComponent::new);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SpriteUploader.getInstance());
    }
}
