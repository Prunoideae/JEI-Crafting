package moe.wolfgirl.jeicrafting.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import moe.wolfgirl.jeicrafting.JEICraftingPlugin;
import moe.wolfgirl.jeicrafting.game.ClientState;
import moe.wolfgirl.jeicrafting.game.GameConfig;
import moe.wolfgirl.jeicrafting.game.GameState;
import moe.wolfgirl.jeicrafting.network.CraftItemPayload;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import moe.wolfgirl.jeicrafting.render.CraftingComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onMouseMiddleClicked(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE) return;
        var selected = JEICraftingPlugin.getSelectedItem();
        if (selected == null) return;
        List<JEICraftingRecipe> recipes = GameState.getMatchingRecipes(selected);
        if (recipes.isEmpty()) return;
        JEICraftingRecipe recipe = recipes.getFirst();

        if (event.getAction() == InputConstants.PRESS) {
            if (recipe.isInstant() || (Screen.hasControlDown() && !recipe.isUncraftable())) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            ClientState.NEXT_CRAFT_TICK = mc.player.tickCount + recipe.craftInTicks();
        } else {
            ClientState.NEXT_CRAFT_TICK = -1;
            if (!recipe.isInstant()) return; // Handles all the crafting logic in mouse down
            PacketDistributor.sendToServer(new CraftItemPayload(selected, 0, GameConfig.getCurrentMultiplier(), Screen.hasControlDown()));
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (ClientState.NEXT_CRAFT_TICK == -1) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        var selected = JEICraftingPlugin.getSelectedItem();
        if (selected == null) {
            ClientState.NEXT_CRAFT_TICK = -1;
            return;
        }

        if (mc.player.tickCount < ClientState.NEXT_CRAFT_TICK) return;
        List<JEICraftingRecipe> recipes = GameState.getMatchingRecipes(selected);
        if (recipes.isEmpty()) return;
        JEICraftingRecipe recipe = recipes.getFirst();
        ClientState.NEXT_CRAFT_TICK = mc.player.tickCount + recipe.craftInTicks();
        PacketDistributor.sendToServer(new CraftItemPayload(selected, 0, GameConfig.getCurrentMultiplier(), Screen.hasControlDown()));
    }

    @SubscribeEvent
    public static void renderComponents(RenderTooltipEvent.GatherComponents event) {
        var selected = JEICraftingPlugin.getSelectedItem();
        if (selected == null || !ItemStack.isSameItemSameComponents(selected, event.getItemStack())) return;

        var matchingRecipes = GameState.getMatchingRecipes(selected);
        if (matchingRecipes.isEmpty()) return;
        var selectedRecipe = matchingRecipes.getFirst();

        event.getTooltipElements().add(1, Either.right(new CraftingComponent(selectedRecipe)));
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        GameState.reloadRecipes(event.getRecipeManager());
    }
}
