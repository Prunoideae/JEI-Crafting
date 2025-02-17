package moe.wolfgirl.jeicrafting.game;

import moe.wolfgirl.jeicrafting.JEICrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class GameUtil {
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(JEICrafting.MODID, path);
    }

    public static int countItems(ItemStack output, Player player) {
        return countItems(invStack -> ItemStack.isSameItemSameComponents(invStack, output), player);
    }

    public static int countItems(Predicate<ItemStack> ingredient, Player player) {
        return countOrTakeItems(ingredient, player, 0);
    }

    public static int countOrTakeItems(ItemStack output, Player player, int count) {
        return countOrTakeItems(invStack -> ItemStack.isSameItemSameComponents(invStack, output), player, count);
    }

    public static int countOrTakeItems(Predicate<ItemStack> ingredient, Player player, int count) {
        int taken = player.getInventory()
                .clearOrCountMatchingItems(
                        ingredient,
                        count,
                        player.inventoryMenu.getCraftSlots()
                );
        if (count > 0) {
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.slotsChanged(player.getInventory());
        }
        return taken;
    }
}
