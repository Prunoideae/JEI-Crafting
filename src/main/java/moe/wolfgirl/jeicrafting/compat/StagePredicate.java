package moe.wolfgirl.jeicrafting.compat;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface StagePredicate {
    List<StagePredicate> PREDICATES = new ArrayList<>();

    boolean test(Player player, List<String> stages);

    static boolean stageNotMatch(Player player, List<String> tags) {
        var stages = tags.stream()
                .filter(s -> s.startsWith("stage:"))
                .map(s -> s.substring(6))
                .toList();

        for (StagePredicate predicate : PREDICATES) {
            if (!predicate.test(player, stages)) return true;
        }
        return false;
    }
}
