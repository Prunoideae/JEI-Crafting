package moe.wolfgirl.jeicrafting.game;

import moe.wolfgirl.jeicrafting.JEICrafting;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GameRegistries {
    public static class RecipeSerializers {
        private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JEICrafting.MODID);

        public static final Supplier<RecipeSerializer<JEICraftingRecipe>> JEI_CRAFTING = REGISTRY.register("jei_crafting", JEICraftingRecipe.Serializer::new);
    }

    public static class RecipeTypes {
        private static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, JEICrafting.MODID);

        public static final Supplier<RecipeType<JEICraftingRecipe>> JEI_CRAFTING = REGISTRY.register("jei_crafting", () -> new RecipeType<>() {
        });
    }

    public static void register(IEventBus bus) {
        RecipeSerializers.REGISTRY.register(bus);
        RecipeTypes.REGISTRY.register(bus);
    }
}
