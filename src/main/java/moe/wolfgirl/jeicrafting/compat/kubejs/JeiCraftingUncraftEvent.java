package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JeiCraftingUncraftEvent implements KubeEvent {
    public final ResourceLocation recipdId;
    public final JEICraftingRecipe recipe;
    public final Player player;
    public List<ItemStack> recipeOutput = new ArrayList<>();

    public JeiCraftingUncraftEvent(ResourceLocation recipdId, JEICraftingRecipe recipe, Player player) {
        this.recipdId = recipdId;
        this.recipe = recipe;
        this.player = player;
    }
}
