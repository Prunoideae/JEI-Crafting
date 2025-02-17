package moe.wolfgirl.jeicrafting.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import moe.wolfgirl.jeicrafting.JEICraftingPlugin;
import moe.wolfgirl.jeicrafting.game.GameConfig;
import moe.wolfgirl.jeicrafting.game.GameState;
import moe.wolfgirl.jeicrafting.network.CraftItemPayload;
import moe.wolfgirl.jeicrafting.render.CraftingComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE || event.getAction() != InputConstants.RELEASE) {
            return;
        }
        var selected = JEICraftingPlugin.getSelectedItem();
        if (selected == null) return;

        if (GameState.getMatchingRecipes(selected).isEmpty() && !selected.is(GameState.FREE_ITEM)) return;
        PacketDistributor.sendToServer(new CraftItemPayload(selected, 0, GameConfig.getCurrentMultiplier(), Screen.hasControlDown()));
    }

    @SubscribeEvent
    public static void renderComponents(RenderTooltipEvent.GatherComponents event) {
        var selected = JEICraftingPlugin.getSelectedItem();
        if (selected == null || !ItemStack.isSameItemSameComponents(selected, event.getItemStack())) return;

        if (selected.is(GameState.FREE_ITEM)) {
            event.getTooltipElements().add(1, Either.right(new CraftingComponent(
                    selected.copyWithCount(1),
                    List.of()
            )));
        } else {
            var matchingRecipes = GameState.getMatchingRecipes(selected);
            if (matchingRecipes.isEmpty()) return;
            var selectedRecipe = matchingRecipes.getFirst();

            event.getTooltipElements().add(1, Either.right(new CraftingComponent(
                    selectedRecipe.output(),
                    selectedRecipe.ingredients()
            )));
        }
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        GameState.reloadRecipes(event.getRecipeManager());
    }
}
