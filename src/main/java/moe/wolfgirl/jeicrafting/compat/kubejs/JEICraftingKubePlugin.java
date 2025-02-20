package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import moe.wolfgirl.jeicrafting.compat.JeiCraftCallback;
import moe.wolfgirl.jeicrafting.compat.JeiUncraftCallback;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class JEICraftingKubePlugin implements KubeJSPlugin {
    @Override
    public void afterInit() {
        JeiCraftCallback.CALLBACKS.add(((player, recipeId, craftingRecipe) -> {
            var event = new JEICraftingCraftEvent(recipeId, craftingRecipe, player);
            var result = JeiCraftingEvents.ITEM_CRAFT.post(event, recipeId);
            if (result.interruptFalse()) return Optional.of(ItemStack.EMPTY);
            return event.recipeOutput.isEmpty() ? Optional.empty() : Optional.of(event.recipeOutput);
        }));

        JeiUncraftCallback.CALLBACKS.add(((player, recipeId, craftingRecipe) -> {
            var event = new JeiCraftingUncraftEvent(recipeId, craftingRecipe, player);
            var result = JeiCraftingEvents.ITEM_UNCRAFT.post(event, recipeId);
            if (result.interruptFalse()) return Optional.of(List.of());
            return event.recipeOutput.isEmpty() ? Optional.empty() : Optional.of(event.recipeOutput);
        }));
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(JeiCraftingEvents.GROUP);
    }
}
