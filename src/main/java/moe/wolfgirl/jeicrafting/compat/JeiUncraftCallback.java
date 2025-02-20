package moe.wolfgirl.jeicrafting.compat;

import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface JeiUncraftCallback {
    List<JeiUncraftCallback> CALLBACKS = new ArrayList<>();

    static List<ItemStack> stopAtFirstModification(Player player, ResourceLocation recipeId, JEICraftingRecipe craftingRecipe) {
        for (JeiUncraftCallback callback : CALLBACKS) {
            var output = callback.uncraft(player, recipeId, craftingRecipe);
            if (output.isPresent()) return output.get();
        }
        return craftingRecipe.uncraftingItems().stream().map(ItemStack::copy).toList();
    }

    Optional<List<ItemStack>> uncraft(Player player, ResourceLocation recipeId, JEICraftingRecipe craftingRecipe);
}
