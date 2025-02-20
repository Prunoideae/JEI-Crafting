package moe.wolfgirl.jeicrafting.compat;

import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface JeiCraftCallback {
    List<JeiCraftCallback> CALLBACKS = new ArrayList<>();

    static ItemStack stopAtFirstModification(Player player, ResourceLocation recipeId, JEICraftingRecipe craftingRecipe) {
        for (JeiCraftCallback callback : CALLBACKS) {
            Optional<ItemStack> output = callback.craft(player, recipeId, craftingRecipe);
            if (output.isPresent()) return output.get();
        }
        return craftingRecipe.output();
    }

    Optional<ItemStack> craft(Player player, ResourceLocation recipeId, JEICraftingRecipe craftingRecipe);
}
