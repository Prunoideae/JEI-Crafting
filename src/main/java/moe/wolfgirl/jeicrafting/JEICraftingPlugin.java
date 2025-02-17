package moe.wolfgirl.jeicrafting;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import moe.wolfgirl.jeicrafting.game.GameState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JEICraftingPlugin implements IModPlugin {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JEICrafting.MODID, "jei");

    private static IJeiRuntime runtime = null;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (ItemStack outputItem : GameState.getOutputItems()) {
            registration.addItemStackInfo(outputItem,
                    Component.translatable("tooltip.jei_crafting.quick_craft.0"),
                    Component.translatable("tooltip.jei_crafting.quick_craft.1")
            );
        }
    }

    @Nullable
    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    @Nullable
    public static ItemStack getSelectedItem() {
        if (runtime == null || Internal.getClientToggleState().isCheatItemsEnabled()) return null;
        var stack = runtime.getIngredientListOverlay().getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (stack == null) stack = runtime.getBookmarkOverlay().getItemStackUnderMouse();
        return stack;
    }
}
