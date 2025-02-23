package moe.wolfgirl.jeicrafting.game;

import com.mojang.datafixers.util.Pair;
import moe.wolfgirl.jeicrafting.data.PlayerResourceType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientState {
    public static final ResourceLocation ARROW = GameUtil.id("recipe_arrow");
    public static final ResourceLocation ARROW_UNCRAFTING = GameUtil.id("recipe_arrow_reversed");
    public static final ResourceLocation ARROW_BG = GameUtil.id("recipe_arrow_empty");
    public static final ResourceLocation ARROW_UNCRAFTING_BG = GameUtil.id("recipe_arrow_reversed_empty");
    public static final ResourceLocation MIDDLE_GOOD = GameUtil.id("middle_button");
    public static final ResourceLocation MIDDLE_BAD = GameUtil.id("middle_button_insufficient");
    public static final ResourceLocation MIDDLE_DISABLED = GameUtil.id("middle_button_disabled");
    public static final ResourceLocation VOID = GameUtil.id("void");
    public static int NEXT_CRAFT_TICK = -1;

    public static final Map<ResourceKey<PlayerResourceType>, Integer> RESOURCES = new HashMap<>();
    public static final List<Pair<ItemStack, Integer>> RESOURCE_STACKS = new ArrayList<>();
}
