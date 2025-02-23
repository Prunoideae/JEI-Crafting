package moe.wolfgirl.jeicrafting.render;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.game.ClientState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

public class ResourcesLayer implements LayeredDraw.Layer {
    public static final ResourcesLayer INSTANCE = new ResourcesLayer();

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        int x = 10;
        int y = 10;

        for (Pair<ItemStack, Integer> resourceStack : ClientState.RESOURCE_STACKS) {
            ItemStack itemStack = resourceStack.getFirst();
            String amount = NUMBER_FORMAT.format((long) resourceStack.getSecond());

            guiGraphics.renderItem(itemStack, x, y);
            guiGraphics.drawString(Minecraft.getInstance().font, amount, x + 20, y + 9, 16777215, true);
            y += 20;
        }
    }
}
