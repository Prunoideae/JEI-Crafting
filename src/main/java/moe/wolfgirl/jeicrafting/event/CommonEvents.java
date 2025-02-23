package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.game.GameRegistries;
import moe.wolfgirl.jeicrafting.game.GameState;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new GameState.ReloadListener(event.getServerResources()));
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            var data = serverPlayer.getData(GameRegistries.Attachments.PLAYER_RESOURCES.get());
            if (data.holding().isEmpty()) return;
            data.notifyClient(serverPlayer);
        }
    }
}
