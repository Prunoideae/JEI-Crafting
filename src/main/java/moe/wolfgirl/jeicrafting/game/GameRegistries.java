package moe.wolfgirl.jeicrafting.game;

import moe.wolfgirl.jeicrafting.JEICrafting;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.recipe.JEICraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
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

    public static class Attachments {
        private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JEICrafting.MODID);

        public static final Supplier<AttachmentType<PlayerResources>> PLAYER_RESOURCES = REGISTRY.register("player_resources",
                () -> AttachmentType.builder(() -> new PlayerResources(new HashMap<>()))
                        .serialize(PlayerResources.CODEC)
                        .copyOnDeath()
                        .build()
        );
    }

    public static void register(IEventBus bus) {
        RecipeSerializers.REGISTRY.register(bus);
        RecipeTypes.REGISTRY.register(bus);
        Attachments.REGISTRY.register(bus);
    }
}
