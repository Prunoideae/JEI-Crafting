package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.data.FakeItemPickupPayload;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.game.GameRegistries;
import moe.wolfgirl.jeicrafting.game.GameState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;

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
            data.notifyClient(serverPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerPickedUpItems(ItemEntityPickupEvent.Pre event) {
        // Transform the item to resources and items
        if (event.getItemEntity().hasPickUpDelay() || event.canPickup().isFalse()) return;
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            var entity = event.getItemEntity();
            var stack = entity.getItem();

            GameState.CONVERSION_CHECK.get().getRecipeFor(new SingleRecipeInput(stack), entity.level()).ifPresent((holder) -> {
                var recipe = holder.value();
                PacketDistributor.sendToPlayer(serverPlayer, new FakeItemPickupPayload(
                        stack,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        entity.getYRot()
                ));

                int count = stack.getCount();
                for (ItemStack remnant : recipe.remnants()) {
                    ItemHandlerHelper.giveItemToPlayer(serverPlayer, remnant.copyWithCount(remnant.getCount() * count), -1);
                }
                var data = serverPlayer.getData(GameRegistries.Attachments.PLAYER_RESOURCES.get());
                for (PlayerResources.PlayerResource resource : recipe.resources()) {
                    int current = data.holding().getOrDefault(resource.id(), 0);
                    data.holding().put(resource.id(), current + resource.amount() * count);
                }
                if (!recipe.resources().isEmpty()) {
                    data.notifyClient(serverPlayer);
                }

                entity.setItem(ItemStack.EMPTY);
                event.setCanPickup(TriState.FALSE);
            });
        }
    }
}
