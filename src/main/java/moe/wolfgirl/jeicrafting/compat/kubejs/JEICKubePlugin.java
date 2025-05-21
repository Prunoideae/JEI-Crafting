package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.rhino.type.TypeInfo;
import moe.wolfgirl.jeicrafting.JEICrafting;
import moe.wolfgirl.jeicrafting.compat.StagePredicate;
import moe.wolfgirl.jeicrafting.data.PlayerResourceType;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class JEICKubePlugin implements KubeJSPlugin {

    public static boolean testStage(Player player, List<String> stages) {
        Stages playerStages = player.kjs$getStages();
        for (String stage : stages) {
            if (!playerStages.has(stage)) return false;
        }
        return true;
    }

    @Override
    public void afterInit() {
        StagePredicate.PREDICATES.add(JEICKubePlugin::testStage);
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        JEICrafting.LOGGER.info("Added registry types");
        RegistryType.register(PlayerResourceType.REGISTRY, TypeInfo.of(PlayerResourceType.class));
        registry.register(PlayerResourceType.REGISTRY, PlayerResourceType.DIRECT_CODEC, PlayerResourceType.class);
    }
}
