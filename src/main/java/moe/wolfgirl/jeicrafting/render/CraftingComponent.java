package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record CraftingComponent(ItemStack output, List<SizedIngredient> ingredients,
                                List<ItemStack> uncraftsTo) implements TooltipComponent {

    public CraftingComponent(JEICraftingRecipe recipe) {
        this(recipe.output(), recipe.ingredients(), recipe.uncraftingItems());
    }

    public boolean isUncraftable() {
        return isFree() || !uncraftsTo.isEmpty();
    }

    public boolean isFree() {
        return ingredients.isEmpty();
    }

    public List<ItemStack> consolidateIngredients(int offsetTicks, int multiplier) {
        List<ItemStack> itemStacks = new ArrayList<>();

        for (SizedIngredient ingredient : ingredients) {
            ItemStack[] ingredientItems = ingredient.getItems();
            if (ingredientItems.length == 0) continue;
            var item = ingredientItems[offsetTicks % ingredientItems.length];
            itemStacks.add(item.copyWithCount(item.getCount() * multiplier));
        }

        return itemStacks;
    }
}
