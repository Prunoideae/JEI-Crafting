package moe.wolfgirl.jeicrafting.data;

import moe.wolfgirl.jeicrafting.compat.StagePredicate;
import moe.wolfgirl.jeicrafting.game.GameRegistries;
import moe.wolfgirl.jeicrafting.game.GameState;
import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CraftItemPayload(ItemStack itemStack, int offset, int multiplier,
                               boolean uncrafting) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CraftItemPayload> TYPE = new Type<>(GameUtil.id("craft_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftItemPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, CraftItemPayload::itemStack,
            ByteBufCodecs.VAR_INT, CraftItemPayload::offset,
            ByteBufCodecs.VAR_INT, CraftItemPayload::multiplier,
            ByteBufCodecs.BOOL, CraftItemPayload::uncrafting,
            CraftItemPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        var player = context.player();
        var recipes = GameState.getMatchingRecipes(itemStack);

        if (offset >= recipes.size()) {
            return; // Prevent people from maliciously sending non-craftable items and crash the server
        }

        var recipe = recipes.get(offset);
        var data = player.getData(GameRegistries.Attachments.PLAYER_RESOURCES.get());
        if (uncrafting) {
            if (!recipe.isUncraftable()) return;
            int expected = recipe.output().getCount() * multiplier;
            var results = recipe.uncraftingItems();

            if (!recipe.stages().map(s -> StagePredicate.testAll(player, s)).orElse(true)) {
                player.playNotifySound(SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1f, 1f);
                return;
            }

            if (GameUtil.countItems(recipe.output(), player) < expected) {
                return;
            }
            GameUtil.countOrTakeItems(recipe.output(), player, expected);
            for (ItemStack uncraftingItems : results) {
                int shouldGive = uncraftingItems.getCount() * multiplier;
                ItemHandlerHelper.giveItemToPlayer(player, uncraftingItems.copyWithCount(shouldGive), -1);
            }

            for (PlayerResources.PlayerResource resource : recipe.resources()) {
                int shouldGive = resource.amount() * multiplier;
                int current = data.holding().getOrDefault(resource.id(), 0);
                data.holding().put(resource.id(), current + shouldGive);
            }
        } else {
            var result = recipe.output();
            if (!recipe.stages().map(s -> StagePredicate.testAll(player, s)).orElse(true)) {
                player.playNotifySound(SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1f, 1f);
                return;
            }
            for (SizedIngredient ingredient : recipe.ingredients()) {
                int expected = ingredient.count() * multiplier;
                if (GameUtil.countItems(ingredient.ingredient(), player) < expected) {
                    return;
                }
            }

            for (PlayerResources.PlayerResource resource : recipe.resources()) {
                int expected = resource.amount() * multiplier;
                if (data.holding().getOrDefault(resource.id(), 0) < expected) {
                    return;
                }
            }

            for (SizedIngredient ingredient : recipe.ingredients()) {
                int expected = ingredient.count() * multiplier;
                GameUtil.countOrTakeItems(ingredient.ingredient(), player, expected);
            }

            for (PlayerResources.PlayerResource resource : recipe.resources()) {
                int expected = resource.amount() * multiplier;
                int current = data.holding().getOrDefault(resource.id(), 0);
                data.holding().put(resource.id(), current - expected);
            }
            ItemHandlerHelper.giveItemToPlayer(player, result.copyWithCount(result.getCount() * multiplier), -1);
        }
        data.notifyClient((ServerPlayer) player);
    }
}
