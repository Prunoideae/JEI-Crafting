package moe.wolfgirl.jeicrafting.compat;

import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface JeiCraftCallback {
    ItemStack craft(Player player, JEICraftingRecipe craftingRecipe);
}
