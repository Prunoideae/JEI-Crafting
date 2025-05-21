package moe.wolfgirl.jeicrafting.game;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GameConfig {

    public static final ModConfigSpec.IntValue ALT_MULTIPLIER;
    public static final ModConfigSpec.IntValue SHIFT_MULTIPLIER;
    public static final ModConfigSpec.IntValue DEFAULT_MULTIPLIER;

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("JEI Crafting").push("crafting");
        ALT_MULTIPLIER = builder.comment("How many times the crafting will be performed if alt is pressed.")
                .defineInRange("alt_multiplier", 1, 1, Integer.MAX_VALUE);
        DEFAULT_MULTIPLIER = builder.comment("How many times the crafting will be performed if no key is pressed.")
                .defineInRange("default_multiplier", 8, 1, Integer.MAX_VALUE);
        SHIFT_MULTIPLIER = builder.comment("How many times the crafting will be performed if shift is pressed.")
                .defineInRange("shift_multiplier", 64, 1, Integer.MAX_VALUE);

        SPEC = builder.build();
    }

    public static int[] getMultipliers() {
        return new int[]{DEFAULT_MULTIPLIER.get(), SHIFT_MULTIPLIER.get(), ALT_MULTIPLIER.get()};
    }
}
