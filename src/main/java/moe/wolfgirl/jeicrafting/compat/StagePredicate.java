package moe.wolfgirl.jeicrafting.compat;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface StagePredicate {
    List<StagePredicate> PREDICATES = new ArrayList<>();

    boolean test(Player player, List<String> stages);

    static boolean testAll(Player player, List<String> stages) {
        for (StagePredicate predicate : PREDICATES) {
            if (!predicate.test(player, stages)) return false;
        }
        return true;
    }
}
