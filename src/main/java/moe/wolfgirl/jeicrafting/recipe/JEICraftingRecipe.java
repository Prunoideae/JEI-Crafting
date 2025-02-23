package moe.wolfgirl.jeicrafting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.game.GameRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record JEICraftingRecipe(ItemStack output,
                                List<SizedIngredient> ingredients,
                                List<PlayerResources.PlayerResource> resources,
                                List<ItemStack> uncraftingItems,
                                int craftInTicks,
                                Optional<List<String>> stages) implements Recipe<JEICraftingRecipe.JeiCraftingInput> {
    public boolean isFree() {
        return ingredients.isEmpty();
    }

    public boolean isUncraftable() {
        return isFree() || !uncraftingItems.isEmpty();
    }

    public boolean isInstant() {
        return craftInTicks <= 0;
    }

    @Override
    public boolean matches(@NotNull JeiCraftingInput jeiCraftingInput, @NotNull Level level) {
        for (SizedIngredient ingredient : ingredients) {
            for (ItemStack input : jeiCraftingInput.inputs) {
                if (!ingredient.test(input)) return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull JeiCraftingInput jeiCraftingInput, HolderLookup.@NotNull Provider provider) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return GameRegistries.RecipeSerializers.JEI_CRAFTING.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return GameRegistries.RecipeTypes.JEI_CRAFTING.get();
    }

    public static class Serializer implements RecipeSerializer<JEICraftingRecipe> {

        public static final MapCodec<JEICraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                data -> data.group(
                        ItemStack.CODEC.fieldOf("output").forGetter(JEICraftingRecipe::output),
                        SizedIngredient.NESTED_CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(JEICraftingRecipe::ingredients),
                        PlayerResources.PlayerResource.CODEC.listOf().optionalFieldOf("resources", List.of()).forGetter(JEICraftingRecipe::resources),
                        ItemStack.CODEC.listOf().optionalFieldOf("uncraftsTo", List.of()).forGetter(JEICraftingRecipe::uncraftingItems),
                        Codec.INT.optionalFieldOf("craftInTicks", 10).forGetter(JEICraftingRecipe::craftInTicks),
                        Codec.STRING.listOf().optionalFieldOf("stages").forGetter(JEICraftingRecipe::stages)
                ).apply(data, JEICraftingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, JEICraftingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, JEICraftingRecipe::output,
                ByteBufCodecs.collection(ArrayList::new, SizedIngredient.STREAM_CODEC), JEICraftingRecipe::ingredients,
                ByteBufCodecs.collection(ArrayList::new, PlayerResources.PlayerResource.STREAM_CODEC), JEICraftingRecipe::resources,
                ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), JEICraftingRecipe::uncraftingItems,
                ByteBufCodecs.INT, JEICraftingRecipe::craftInTicks,
                ByteBufCodecs.optional(ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8)), JEICraftingRecipe::stages,
                JEICraftingRecipe::new
        );

        @Override
        public @NotNull MapCodec<JEICraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, JEICraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public record JeiCraftingInput(NonNullList<ItemStack> inputs) implements RecipeInput {
        @Override
        public @NotNull ItemStack getItem(int i) {
            return inputs.get(i);
        }

        @Override
        public int size() {
            return inputs.size();
        }
    }
}
