package moe.wolfgirl.jeicrafting.game;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

    public static final Map<ResourceLocation, JEICraftingRecipe> RECIPES = new HashMap<>();
    private static Pair<ItemStack, List<JEICraftingRecipe>> cache = null;

    public static List<JEICraftingRecipe> getMatchingRecipes(ItemStack output) {
        if (cache != null && ItemStack.isSameItemSameComponents(cache.getFirst(), output)) return cache.getSecond();
        List<JEICraftingRecipe> matched = new ArrayList<>();

        for (JEICraftingRecipe recipe : RECIPES.values()) {
            if (ItemStack.isSameItemSameComponents(output, recipe.output())) {
                matched.add(recipe);
            }
        }
        cache = Pair.of(output, List.copyOf(matched));
        return cache.getSecond();
    }

    public static List<Pair<ResourceLocation, JEICraftingRecipe>> getMatchingRecipeAndId(ItemStack output) {
        var matched = new ArrayList<Pair<ResourceLocation, JEICraftingRecipe>>();

        for (Map.Entry<ResourceLocation, JEICraftingRecipe> entry : RECIPES.entrySet()) {
            ResourceLocation id = entry.getKey();
            JEICraftingRecipe recipe = entry.getValue();

            if (ItemStack.isSameItemSameComponents(output, recipe.output())) {
                matched.add(Pair.of(id, recipe));
            }
        }
        return matched;
    }

    public static List<ItemStack> getOutputItems() {
        return RECIPES.values().stream().map(JEICraftingRecipe::output).toList();
    }

    public static void reloadRecipes(RecipeManager recipeManager) {
        cache = null;
        RECIPES.clear();
        for (RecipeHolder<JEICraftingRecipe> holder : recipeManager.getAllRecipesFor(GameRegistries.RecipeTypes.JEI_CRAFTING.get())) {
            RECIPES.put(holder.id(), holder.value());
        }
    }

    public record ReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
            reloadRecipes(resources.getRecipeManager());
        }
    }
}
