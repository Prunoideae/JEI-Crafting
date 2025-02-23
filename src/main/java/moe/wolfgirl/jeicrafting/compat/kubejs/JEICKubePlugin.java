package moe.wolfgirl.jeicrafting.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.stages.Stages;
import moe.wolfgirl.jeicrafting.compat.StagePredicate;
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
}
