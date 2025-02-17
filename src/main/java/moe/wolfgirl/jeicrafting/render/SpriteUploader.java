package moe.wolfgirl.jeicrafting.render;

import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SpriteUploader extends TextureAtlasHolder {
    public static final ResourceLocation GUI_LOCATION = GameUtil.id("textures/atlas/gui.png");
    public static final ResourceLocation INFO_LOCATION = GameUtil.id("gui");

    private static SpriteUploader instance = null;

    public SpriteUploader(TextureManager textureManager) {
        super(textureManager, GUI_LOCATION, INFO_LOCATION);
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation location) {
        return super.getSprite(location);
    }

    public static SpriteUploader getInstance() {
        if (instance == null) {
            instance = new SpriteUploader(Minecraft.getInstance().getTextureManager());
        }
        return instance;
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {
        return getInstance().getSprite(location);
    }
}
