package moe.wolfgirl.jeicrafting.game;

import net.minecraft.resources.ResourceLocation;

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
}
