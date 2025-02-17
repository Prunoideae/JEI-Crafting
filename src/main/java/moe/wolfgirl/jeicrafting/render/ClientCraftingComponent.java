package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.game.GameConfig;
import moe.wolfgirl.jeicrafting.game.GameState;
import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientCraftingComponent implements ClientTooltipComponent {
    private final CraftingComponent craftingComponent;

    public ClientCraftingComponent(CraftingComponent craftingComponent) {
        this.craftingComponent = craftingComponent;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (craftingComponent.isFree()) {
            return 18 + 22 + 18;
        } else {
            int inputWidth = craftingComponent.ingredients().size() * 18;
            return inputWidth + 22 + 18;
        }

    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        y++;
        int multiplier = GameConfig.getCurrentMultiplier();
        boolean uncrafting = Screen.hasControlDown();
        int offsetTick = player.tickCount / 20;

        int offset;
        if (craftingComponent.isFree()) {
            guiGraphics.blit(x, y, 0, 18, 18, SpriteUploader.getTexture(GameState.VOID));
            offset = x + 18;
        } else {
            List<ItemStack> inputs = craftingComponent.consolidateIngredients(offsetTick, multiplier);

            for (int i = 0; i < inputs.size(); i++) {
                var iOffset = i * 18;
                guiGraphics.renderItem(inputs.get(i), iOffset + x, y);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, inputs.get(i), iOffset + x, y);
            }

            offset = x + inputs.size() * 18;
        }


        if (uncrafting && (craftingComponent.isUncraftable() || craftingComponent.isFree())) {
            guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(GameState.ARROW_UNCRAFTING));
        } else {
            guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(GameState.ARROW));
        }
        guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(
                getCraftingStatus(Minecraft.getInstance().player, craftingComponent, multiplier, uncrafting)
        ));
        offset += 23;

        var output = craftingComponent.output();
        guiGraphics.renderItem(craftingComponent.output(), offset, y);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, output.copyWithCount(output.getCount() * multiplier), offset, y);
    }

    private static ResourceLocation getCraftingStatus(Player player, CraftingComponent component, int multiplier, boolean uncrafting) {
        if (component.isFree()) return GameState.MIDDLE_GOOD;

        if (uncrafting) {
            if (!component.isUncraftable()) {
                return GameState.MIDDLE_DISABLED;
            }
            if (GameUtil.countItems(component.output(), player) > component.output().getCount() * multiplier) {
                return GameState.MIDDLE_GOOD;
            } else {
                return GameState.MIDDLE_BAD;
            }
        } else {
            for (SizedIngredient ingredient : component.ingredients()) {
                int expected = ingredient.count() * multiplier;
                if (GameUtil.countItems(ingredient.ingredient(), player) < expected) {
                    return GameState.MIDDLE_BAD;
                }
            }
            return GameState.MIDDLE_GOOD;
        }
    }
}
