package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.game.ClientState;
import moe.wolfgirl.jeicrafting.game.GameConfig;
import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
            int inputWidth = (Screen.hasControlDown() && craftingComponent.isUncraftable() ?
                    craftingComponent.uncraftsTo().size() :
                    craftingComponent.ingredients().size()
            ) * 18;
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
            guiGraphics.blit(x, y, 0, 18, 18, SpriteUploader.getTexture(ClientState.VOID));
            offset = x + 18;
        } else {
            List<ItemStack> inputs;
            if (uncrafting && craftingComponent.isUncraftable()) {
                inputs = craftingComponent.uncraftsTo().stream().map(s -> s.copyWithCount(s.getCount() * multiplier)).toList();
            } else {
                inputs = craftingComponent.consolidateIngredients(offsetTick, multiplier);
            }
            for (int i = 0; i < inputs.size(); i++) {
                var iOffset = i * 18;
                guiGraphics.renderItem(inputs.get(i), iOffset + x, y);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, inputs.get(i), iOffset + x, y);
            }

            offset = x + inputs.size() * 18;
        }

        ResourceLocation foreground = uncrafting && craftingComponent.isUncraftable() ? ClientState.ARROW_UNCRAFTING : ClientState.ARROW;
        ResourceLocation background = uncrafting && craftingComponent.isUncraftable() ? ClientState.ARROW_UNCRAFTING_BG : ClientState.ARROW_BG;
        ResourceLocation status = getCraftingStatus(Minecraft.getInstance().player, craftingComponent, multiplier, uncrafting);
        if (craftingComponent.isInstant()) {
            guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(status == ClientState.MIDDLE_GOOD ? foreground : background));
        } else {
            guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(background));
            if (ClientState.NEXT_CRAFT_TICK != -1 && status == ClientState.MIDDLE_GOOD) {
                int width = 23;
                int remainedTicks = ClientState.NEXT_CRAFT_TICK - player.tickCount;
                int activeWidth = (int) (width * (1 - ((float) remainedTicks / craftingComponent.craftInTicks())));
                if (!uncrafting) {
                    guiGraphics.blitSprite(SpriteUploader.getTexture(foreground), 23, 16, 0, 0, offset, y, 0, activeWidth, 16);
                } else {
                    guiGraphics.blitSprite(SpriteUploader.getTexture(foreground), 23, 16, width - activeWidth, 0, offset + width - activeWidth, y, 0, activeWidth, 16);
                }
            }
        }

        guiGraphics.blit(offset, y, 0, 23, 16, SpriteUploader.getTexture(status));
        offset += 23;

        var output = craftingComponent.output();
        guiGraphics.renderItem(craftingComponent.output(), offset, y);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, output.copyWithCount(output.getCount() * multiplier), offset, y);
    }

    private static ResourceLocation getCraftingStatus(Player player, CraftingComponent component, int multiplier, boolean uncrafting) {
        if (component.isFree()) return ClientState.MIDDLE_GOOD;

        if (uncrafting) {
            if (!component.isUncraftable()) {
                return ClientState.MIDDLE_DISABLED;
            }
            if (GameUtil.countItems(component.output(), player) >= component.output().getCount() * multiplier) {
                return ClientState.MIDDLE_GOOD;
            } else {
                return ClientState.MIDDLE_BAD;
            }
        } else {
            for (SizedIngredient ingredient : component.ingredients()) {
                int expected = ingredient.count() * multiplier;
                if (GameUtil.countItems(ingredient.ingredient(), player) < expected) {
                    return ClientState.MIDDLE_BAD;
                }
            }
            return ClientState.MIDDLE_GOOD;
        }
    }
}
