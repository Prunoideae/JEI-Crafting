package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import net.minecraft.resources.ResourceLocation;

public interface JeiCraftingEvents {
    EventGroup GROUP = EventGroup.of("JeiCraftingEvents");
    TargetedEventHandler<ResourceLocation> ITEM_CRAFT = GROUP.server("itemCrafting", () -> JEICraftingCraftEvent.class).hasResult().requiredTarget(EventTargetType.ID);
    TargetedEventHandler<ResourceLocation> ITEM_UNCRAFT = GROUP.server("itemUncrafting", () -> JeiCraftingUncraftEvent.class).hasResult().requiredTarget(EventTargetType.ID);
}
