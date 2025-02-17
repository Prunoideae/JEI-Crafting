package moe.wolfgirl.jeicrafting.game;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import moe.wolfgirl.jeicrafting.render.SpriteUploader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public static final TagKey<Item> FREE_ITEM = TagKey.create(Registries.ITEM, GameUtil.id("free_item"));
    public static final TagKey<Item> UNCRAFTABLE_ITEM = TagKey.create(Registries.ITEM, GameUtil.id("uncraftable_item"));

    public static final ResourceLocation ARROW = GameUtil.id("recipe_arrow");
    public static final ResourceLocation ARROW_UNCRAFTING = GameUtil.id("recipe_arrow_reversed");
    public static final ResourceLocation MIDDLE_GOOD = GameUtil.id("middle_button");
    public static final ResourceLocation MIDDLE_BAD = GameUtil.id("middle_button_insufficient");
    public static final ResourceLocation MIDDLE_DISABLED = GameUtil.id("middle_button_disabled");
    public static final ResourceLocation VOID = GameUtil.id("void");

    public static final List<JEICraftingRecipe> RECIPES = new ArrayList<>();
    private static Pair<ItemStack, List<JEICraftingRecipe>> cache = null;

    public static List<JEICraftingRecipe> getMatchingRecipes(ItemStack output) {
        if (cache != null && ItemStack.isSameItemSameComponents(cache.getFirst(), output)) return cache.getSecond();
        List<JEICraftingRecipe> matched = new ArrayList<>();

        for (JEICraftingRecipe recipe : RECIPES) {
            if (ItemStack.isSameItemSameComponents(output, recipe.output())) {
                matched.add(recipe);
            }
        }
        cache = Pair.of(output, List.copyOf(matched));
        return cache.getSecond();
    }

    public static List<ItemStack> getOutputItems() {
        return RECIPES.stream().map(JEICraftingRecipe::output).toList();
    }

    public record ReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
            var recipeManager = resources.getRecipeManager();
            cache = null;
            RECIPES.clear();
            for (RecipeHolder<JEICraftingRecipe> holder : recipeManager.getAllRecipesFor(GameRegistries.RecipeTypes.JEI_CRAFTING.get())) {
                RECIPES.add(holder.value());
            }
        }
    }
}
