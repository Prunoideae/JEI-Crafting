package moe.wolfgirl.jeicrafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.wolfgirl.jeicrafting.data.PlayerResources;
import moe.wolfgirl.jeicrafting.game.GameRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ConvertToResourceRecipe(List<PlayerResources.PlayerResource> resources,
                                      List<ItemStack> remnants,
                                      Ingredient input) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.input.test(input.item());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return GameRegistries.RecipeSerializers.CONVERT_TO_RESOURCE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return GameRegistries.RecipeTypes.CONVERT_TO_RESOURCE.get();
    }

    public static class Serializer implements RecipeSerializer<ConvertToResourceRecipe> {
        public static final MapCodec<ConvertToResourceRecipe> CODEC = RecordCodecBuilder.mapCodec(
                data -> data.group(
                        PlayerResources.PlayerResource.CODEC.listOf().optionalFieldOf("resources", List.of()).forGetter(ConvertToResourceRecipe::resources),
                        ItemStack.CODEC.listOf().optionalFieldOf("remnants", List.of()).forGetter(ConvertToResourceRecipe::remnants),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(ConvertToResourceRecipe::input)
                ).apply(data, ConvertToResourceRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ConvertToResourceRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, PlayerResources.PlayerResource.STREAM_CODEC), ConvertToResourceRecipe::resources,
                ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), ConvertToResourceRecipe::remnants,
                Ingredient.CONTENTS_STREAM_CODEC, ConvertToResourceRecipe::input,
                ConvertToResourceRecipe::new
        );

        @Override
        public @NotNull MapCodec<ConvertToResourceRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ConvertToResourceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
