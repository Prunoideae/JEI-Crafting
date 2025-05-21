package moe.wolfgirl.jeicrafting.game;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.event.JEICraftingEvent;
import moe.wolfgirl.jeicrafting.recipe.ConvertToResourceRecipe;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    public static final Lazy<RecipeManager.CachedCheck<SingleRecipeInput, ConvertToResourceRecipe>> CONVERSION_CHECK = Lazy.of(() -> RecipeManager.createCheck(GameRegistries.RecipeTypes.CONVERT_TO_RESOURCE.get()));
    public static final Map<ResourceLocation, JEICraftingRecipe> RECIPES = new HashMap<>();
    private static Pair<ItemStack, JEICraftingRecipe> itemCache = Pair.of(ItemStack.EMPTY, null);

    public static JEICraftingRecipe getMatchingRecipe(ItemStack output, Player player) {
        return getMatchingRecipe(output, player, true);
    }

    public static JEICraftingRecipe getMatchingRecipe(ItemStack output, Player player, boolean useCache) {
        if (useCache && ItemStack.isSameItemSameComponents(itemCache.getFirst(), output)) {
            return itemCache.getSecond();
        }

        for (JEICraftingRecipe recipe : RECIPES.values()) {
            if (ItemStack.isSameItemSameComponents(output, recipe.output())) {
                var event = NeoForge.EVENT_BUS.post(new JEICraftingEvent.Pre(recipe, player));
                itemCache = Pair.of(output, event.getRecipe());
                return itemCache.getSecond();
            }
        }

        itemCache = Pair.of(output, null);
        return null;
    }

    public static List<ItemStack> getOutputItems() {
        return RECIPES.values().stream().map(JEICraftingRecipe::output).toList();
    }

    public static void reloadRecipes(RecipeManager recipeManager) {
        clearCache();
        RECIPES.clear();
        for (RecipeHolder<JEICraftingRecipe> holder : recipeManager.getAllRecipesFor(GameRegistries.RecipeTypes.JEI_CRAFTING.get())) {
            RECIPES.put(holder.id(), holder.value());
        }
    }

    public static void clearCache() {
        itemCache = Pair.of(ItemStack.EMPTY, null);
    }

    public record ReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
            reloadRecipes(resources.getRecipeManager());
        }
    }
}
