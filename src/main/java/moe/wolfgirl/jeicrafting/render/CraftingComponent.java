package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.compat.StagePredicate;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record CraftingComponent(ItemStack output, List<SizedIngredient> ingredients,
                                List<ItemStack> uncraftsTo,
                                List<PlayerResources.PlayerResource> resources,
                                List<String> tags,
                                int craftInTicks) implements TooltipComponent {

    public CraftingComponent(JEICraftingRecipe recipe) {
        this(recipe.output(), recipe.ingredients(), recipe.uncraftingItems(), recipe.resources(), recipe.tags(), recipe.craftInTicks());
    }

    public boolean isUncraftable() {
        return isFree() || !uncraftsTo.isEmpty() || !resources.isEmpty();
    }

    public boolean isFree() {
        return ingredients.isEmpty() && resources.isEmpty();
    }

    public boolean isInstant() {
        return craftInTicks <= 0;
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

    public List<String> getMissingStages(Player player) {
        return tags.stream()
                .filter(s -> StagePredicate.stageNotMatch(player, List.of(s)))
                .map(s -> s.substring(6))
                .toList();
    }
}
