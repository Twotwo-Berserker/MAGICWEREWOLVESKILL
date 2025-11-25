package com.twotwo.logic;

import com.twotwo.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class WinChecker {
    // 检查狼人阵营是否获胜[0:好人胜利, 1:狼人胜利, -1:继续游戏]
    public static int checkWerewolfWin(Game game) {
        List<Player> aliveGoods = game.getPlayers().stream()
                .filter(player -> player.isAlive() && player.getCamp() == Camp.CampType.GOOD)
                .collect(Collectors.toList());
        List<Player> aliveWerewolves = game.getPlayers().stream()
                .filter(player -> player.isAlive() && player.getCamp() == Camp.CampType.WEREWOLF)
                .collect(Collectors.toList());

        int aliveGoodsNumber = aliveGoods.size();
        int aliveWerewolvesNumber = aliveWerewolves.size();

        if (aliveWerewolvesNumber == 0) {
            if (aliveGoodsNumber == 0) {
                return 2; // 特殊结局
            }
            return 0; // 狼人全部死亡
        }

        if (aliveGoodsNumber == 0) {
            return 1; // 好人全部死亡，狼人胜利
        }

        if (aliveGoodsNumber == 1) {
            Player lastGood = aliveGoods.get(0);
            if (lastGood.getRole() == Role.RoleType.LADY) {
                int GUARDexist = aliveWerewolves.stream()
                        .filter(player -> player.getRole() == Role.RoleType.GUARD)
                        .collect(Collectors.toList()).size();
                if (GUARDexist > 0 || lastGood.getSkillTimes() > 0) {
                    return 1;
                }
            }
            if (lastGood.getRole() == Role.RoleType.DOLLMAKER) {
                if (lastGood.getSkillTimes() > 0) {
                    return 1;
                }
            }
            if (lastGood.getRole() == Role.RoleType.LITTLEYUAN
                    || lastGood.getRole() == Role.RoleType.HAMSTER) {
                if (lastGood.getSkillTimes() < 2)
                    return -1;
            }
            return 1;
        }

        return -1; // 游戏继续
    }
}
