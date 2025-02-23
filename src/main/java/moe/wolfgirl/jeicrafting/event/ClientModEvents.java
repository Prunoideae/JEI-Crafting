package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.game.GameUtil;
import moe.wolfgirl.jeicrafting.render.ClientCraftingComponent;
import moe.wolfgirl.jeicrafting.render.CraftingComponent;
import moe.wolfgirl.jeicrafting.render.ResourcesLayer;
import moe.wolfgirl.jeicrafting.render.SpriteUploader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

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

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(GameUtil.id("resources"), ResourcesLayer.INSTANCE);
    }
}
