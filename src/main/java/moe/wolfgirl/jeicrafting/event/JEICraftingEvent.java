package moe.wolfgirl.jeicrafting.event;

import moe.wolfgirl.jeicrafting.JEICrafting;
import moe.wolfgirl.jeicrafting.data.PlayerResourceType;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.*;

public abstract class JEICraftingEvent extends Event {
    /**
     * Determines if a recipe can be crafted, and if yes, how many items, how much resource are needed.
     * <br>
     * Thus, this is fired on both client and server side.
     */
    public static class Pre extends JEICraftingEvent implements ICancellableEvent {
        public final ItemStack output;
        private final List<SizedIngredient> ingredients;
        private final List<PlayerResources.PlayerResource> resources;
        private final List<ItemStack> uncraftingItems;
        public int craftInTicks;
        private final Set<String> tags;
        public final Player player;

        public Pre(JEICraftingRecipe recipe, Player player) {
            this.output = recipe.output();
            this.ingredients = new ArrayList<>(recipe.ingredients());
            this.resources = new ArrayList<>(recipe.resources());
            this.uncraftingItems = new ArrayList<>(recipe.uncraftingItems());
            this.craftInTicks = recipe.craftInTicks();
            this.tags = new HashSet<>(recipe.tags());
            this.player = player;
        }

        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }

        public void addTag(String tag) {
            tags.add(tag);
        }

        public void removeTag(String tag) {
            tags.remove(tag);
        }

        public int getResource(ResourceKey<PlayerResourceType> id) {
            for (PlayerResources.PlayerResource resource : resources) {
                if (resource.id().equals(id)) return resource.amount();
            }
            return 0;
        }

        public void setResource(ResourceKey<PlayerResourceType> id, int amount) {
            resources.removeIf(res -> res.id().equals(id));
            if (amount <= 0) return;
            resources.add(new PlayerResources.PlayerResource(id, amount));
        }

        public List<PlayerResources.PlayerResource> getResources() {
            return resources;
        }

        public void setIngredients(SizedIngredient... ingredients) {
            this.ingredients.clear();
            this.ingredients.addAll(List.of(ingredients));
        }

        public void setUncraftingItems(ItemStack... itemStacks) {
            this.uncraftingItems.clear();
            this.uncraftingItems.addAll(List.of(itemStacks));
        }

        public List<SizedIngredient> getIngredients() {
            return ingredients;
        }

        public List<ItemStack> getUncraftingItems() {
            return uncraftingItems;
        }

        public JEICraftingRecipe getRecipe() {
            return isCanceled() ? null : new JEICraftingRecipe(
                    output,
                    Collections.unmodifiableList(ingredients),
                    Collections.unmodifiableList(resources),
                    Collections.unmodifiableList(uncraftingItems),
                    craftInTicks,
                    List.copyOf(tags)
            );
        }
    }

    public class Post extends JEICraftingEvent {

    }
}
