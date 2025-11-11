package com.twotwo.logic;

import java.util.List;

import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class SkillExecutor {
    // 蓝金警觉
    public static void SHINEBLUE_Skill(Game game) {
        // 查找蓝金的界面
        PlayerFrame shineBlueFrame = game.getPlayerFrames().stream()
                .filter(pf -> pf.getPlayer().getRole() == Role.RoleType.SHINEBLUE)
                .findFirst()
                .orElse(null);
        if (shineBlueFrame.getPlayer().getCamp() == Camp.CampType.GOOD) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    shineBlueFrame.getPlayer(), 1);
            // 如果存在狼人
            if (!sameLocationPlayers.isEmpty()) {
                // 处理存在狼人时的逻辑
                for (Player p : sameLocationPlayers) {
                    if (p.getCamp() == Camp.CampType.WEREWOLF) {
                        shineBlueFrame.updateInfo("发现狼人存在！！！");
                        // shineBlueFrame.getPlayer().setIsGuarded(true); // 免疫狼刀（如果之后改到狼刀前可用）
                        if (shineBlueFrame.getPlayer().getSkillTimes() == 0) {
                            shineBlueFrame.getPlayer().setAlive(true);
                            shineBlueFrame.getPlayer().incrementSkillTimes();
                        }
                        StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
                        for (Player sp : sameLocationPlayers) {
                            sb.append("- ").append(sp.getName()).append("\n");
                        }
                        shineBlueFrame.updateInfo(sb.toString());
                    }
                }
            }
        }

        game.setCurrentStep(game.getCurrentStep() + 1);
        game.processNextStep();
    }

    public static void DETECTIVE_Skill(Game game) {
        PlayerFrame detectivFrame = game.getPlayerFrames().stream()
                .filter(pf -> pf.getPlayer().getRole() == Role.RoleType.DETECTIVE)
                .findFirst()
                .orElse(null);
        if (detectivFrame.getPlayer().isInWall()) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    detectivFrame.getPlayer(), 0);
            StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
            for (Player sp : sameLocationPlayers) {
                sb.append("- ").append(sp.getName()).append("\n");
            }
            detectivFrame.updateInfo(sb.toString());
        }
    }

    public static void HAMSTER_Skill(Game game, Role.RoleType target) {
        // 仓鼠自爆
        List<Player> players = game.getPlayers();
        for (Player p : players) {
            if ((p.getRole() == target || p.getRole() == Role.RoleType.HAMSTER) && p.isAlive()) {
                p.setAlive(false); // 目标角色和仓鼠死亡
                // 更新所有玩家界面
                for (PlayerFrame pf : game.getPlayerFrames()) {
                    pf.updateInfo(" 仓鼠自爆，杀死了：" + p.getName() + "！");
                }
                break;
            }
        }
    }
}
