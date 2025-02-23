package moe.wolfgirl.jeicrafting.data;

import moe.wolfgirl.jeicrafting.game.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FakeItemPickupPayload(ItemStack itemStack,
                                    double x,
                                    double y,
                                    double z,
                                    float yRot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FakeItemPickupPayload> TYPE = new Type<>(GameUtil.id("fake_item_pickup"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FakeItemPickupPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, FakeItemPickupPayload::itemStack,
            ByteBufCodecs.DOUBLE, FakeItemPickupPayload::x,
            ByteBufCodecs.DOUBLE, FakeItemPickupPayload::y,
            ByteBufCodecs.DOUBLE, FakeItemPickupPayload::z,
            ByteBufCodecs.FLOAT, FakeItemPickupPayload::yRot,
            FakeItemPickupPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        var level = context.player().level();
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setYRot(yRot);

        var mc = Minecraft.getInstance();
        level.playLocalSound(x, y, z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
                (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F,
                false);

        mc.particleEngine.add(new ItemPickupParticle(
                mc.getEntityRenderDispatcher(),
                mc.renderBuffers(),
                (ClientLevel) level,
                itemEntity,
                context.player()
        ));
    }
}
