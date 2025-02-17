package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.game.GameState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new GameState.ReloadListener(event.getServerResources()));
    }
}
