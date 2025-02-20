package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class JEICraftingCraftEvent implements KubeEvent {
    public final ResourceLocation recipeId;
    public final JEICraftingRecipe recipe;
    public final Player player;
    public ItemStack recipeOutput = ItemStack.EMPTY;

    public JEICraftingCraftEvent(ResourceLocation recipeId, JEICraftingRecipe recipe, Player player) {
        this.recipeId = recipeId;
        this.recipe = recipe;
        this.player = player;
    }
}
