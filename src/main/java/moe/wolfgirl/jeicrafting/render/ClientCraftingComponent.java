package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.data.PlayerResourceType;
import moe.wolfgirl.jeicrafting.game.ClientState;
import moe.wolfgirl.jeicrafting.game.GameConfig;
import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
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
        boolean canPerformUncrafting = uncrafting && craftingComponent.isUncraftable();

        int offset = x;
        if (craftingComponent.isFree()) {
            guiGraphics.blit(x, y, 0, 18, 18, SpriteUploader.getTexture(ClientState.VOID));
            offset += 18;
        } else {
            offset += renderInputItems(guiGraphics, canPerformUncrafting, player, multiplier, x, y);
            offset += renderInputResources(guiGraphics, player, canPerformUncrafting, multiplier, offset, y);
        }

        ResourceLocation foreground = canPerformUncrafting ? ClientState.ARROW_UNCRAFTING : ClientState.ARROW;
        ResourceLocation background = canPerformUncrafting ? ClientState.ARROW_UNCRAFTING_BG : ClientState.ARROW_BG;
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

        offset += 23;

        var output = craftingComponent.output();
        guiGraphics.renderItem(output, offset, y);
        int color = canPerformUncrafting ? (GameUtil.countItems(output, player) >= output.getCount() * multiplier ? 5635925 : 16733525) : 16777215;
        renderItemCount(guiGraphics, Minecraft.getInstance().font, offset, y, output.getCount() * multiplier, color);
    }

    private int renderInputResources(@NotNull GuiGraphics guiGraphics, LocalPlayer player, boolean canPerformUncrafting, int multiplier, int x, int y) {
        var lookup = player.registryAccess().lookup(PlayerResourceType.REGISTRY).orElse(null);
        if (lookup != null) {
            for (int i = 0; i < craftingComponent.resources().size(); i++) {
                var resource = craftingComponent.resources().get(i);
                ItemStack itemStack = lookup.get(resource.id()).map(Holder.Reference::value).map(PlayerResourceType::representativeItem).orElse(ItemStack.EMPTY);
                if (!itemStack.isEmpty()) {
                    var expected = resource.amount() * multiplier;
                    int color = canPerformUncrafting ?
                            16777215 :
                            expected <= ClientState.RESOURCES.getOrDefault(resource.id(), 0) ?
                                    5635925 :
                                    16733525;
                    guiGraphics.renderItem(itemStack, x + 18 * i, y);
                    renderItemCount(guiGraphics, Minecraft.getInstance().font, x + 18 * i, y, expected, color);
                }
            }
        }
        return 18 * craftingComponent.resources().size();
    }

    private int renderInputItems(@NotNull GuiGraphics guiGraphics, boolean canPerformUncrafting, LocalPlayer player, int multiplier, int x, int y) {
        List<ItemStack> inputs;
        if (canPerformUncrafting) {
            inputs = craftingComponent.uncraftsTo().stream().map(s -> s.copyWithCount(s.getCount() * multiplier)).toList();
        } else {
            int offsetTick = player.tickCount / 20;
            inputs = craftingComponent.consolidateIngredients(offsetTick, multiplier);
        }

        for (int i = 0; i < inputs.size(); i++) {
            var iOffset = i * 18;
            guiGraphics.renderItem(inputs.get(i), iOffset + x, y);
            int color = canPerformUncrafting ? 16777215 : GameUtil.countItems(inputs.get(i), player) >= inputs.get(i).getCount() ? 5635925 : 16733525;
            renderItemCount(guiGraphics, Minecraft.getInstance().font, iOffset + x, y, inputs.get(i).getCount(), color);
        }

        return inputs.size() * 18;
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

    private static void renderItemCount(GuiGraphics guiGraphics, Font font, int x, int y, int count, int color) {
        String s = String.valueOf(count);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200f);
        guiGraphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, color);
        guiGraphics.pose().popPose();
    }
}
