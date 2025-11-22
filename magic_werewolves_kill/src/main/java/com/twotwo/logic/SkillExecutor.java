package com.twotwo.logic;

import java.util.List;

import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class SkillExecutor {
    public static void executeSkill(Player player, Game game) {
        switch (player.getRole()) {
            // 蓝金和侦探的技能是被动触发，不处理
            // 仓鼠
            case HAMSTER:
                PlayerFrame hamsterFrame = game.getPlayerFrame(Role.RoleType.HAMSTER);
                hamsterFrame.updateInfo("请输入自爆的对象：");
                hamsterFrame.updateInfo(PlayerListUtil.getOtherAlivePlayerList(game.getPlayers(), player));
                hamsterFrame.showInputArea();
                HAMSTER_Skill(game, player.getRole()); // 这里有问题
                break;
            // 魔女
            case WITCH:
                WITCH_Skill(game, player);
                break;
            // 人偶师
            case DOLLMAKER:
                DOLLMAKER_Skill(game, player);
                break;
            default:
                break;
        }
    }

    // 蓝金警觉
    public static void SHINEBLUE_Skill(Game game) {
        // 查找蓝金的界面
        PlayerFrame shineBlueFrame = game.getPlayerFrame(Role.RoleType.SHINEBLUE);
        if (shineBlueFrame.getPlayer().getCamp() == Camp.CampType.GOOD) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    shineBlueFrame.getPlayer(), 1);
            // 如果存在狼人
            if (!sameLocationPlayers.isEmpty()) {
                // 处理存在狼人时的逻辑
                for (Player p : sameLocationPlayers) {
                    if (p.getCamp() == Camp.CampType.WEREWOLF) {
                        shineBlueFrame.updateInfo("发现狼人存在！！！");
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

        // game.setCurrentStep(game.getCurrentStep() + 1);
        // game.processNextStep();
    }

    // 侦探视野
    public static void DETECTIVE_Skill(Game game) {
        PlayerFrame detectiveFrame = game.getPlayerFrame(Role.RoleType.DETECTIVE);
        if (detectiveFrame.getPlayer().isInWall()) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    detectiveFrame.getPlayer(), 0);
            StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
            for (Player sp : sameLocationPlayers) {
                sb.append("- ").append(sp.getName()).append("\n");
            }
            detectiveFrame.updateInfo(sb.toString());
        }
    }

    // 仓鼠自爆
    public static void HAMSTER_Skill(Game game, Role.RoleType target) {
        PlayerFrame hamsterFrame = game.getPlayerFrame(Role.RoleType.HAMSTER);
        PlayerFrame targetFrame = game.getPlayerFrame(target);
        if (hamsterFrame.getPlayer().getSkillTimes() < 1 &&
                hamsterFrame.getPlayer().isAlive() && targetFrame.getPlayer().isAlive()) {
            hamsterFrame.getPlayer().incrementSkillTimes();
            hamsterFrame.getPlayer().setAlive(false);
            targetFrame.getPlayer().setAlive(false);
            targetFrame.getPlayer().setDeathDay(game.getCurrentDay());
            hamsterFrame.updateInfo("您自爆了，带走了" + targetFrame.getPlayer().getName() + "！");
            targetFrame.updateInfo("您被仓鼠自爆带走，死亡！");
        }
    }

    // 魔女攻击
    public static void WITCH_Skill(Game game, Player target) {
        PlayerFrame witchFrame = game.getPlayerFrame(Role.RoleType.WITCH);
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        witchFrame.getPlayer().incrementSkillTimes();
        if (target.getCamp() == witchFrame.getPlayer().getCamp()) {
            witchFrame.getPlayer().setAlive(false);
            witchFrame.getPlayer().setDeathDay(game.getCurrentDay());
            witchFrame.updateInfo("您攻击了队友，自身死亡！");
        } else {
            target.setAlive(false);
            target.setDeathDay(game.getCurrentDay());
            witchFrame.updateInfo("您攻击了敌人，敌人死亡！");
            targetFrame.updateInfo("您被魔女光波击中，死亡！");
        }
    }

    public static void DOLLMAKER_Skill(Game game, Player target) {
        PlayerFrame dollmakerFrame = game.getPlayerFrame(Role.RoleType.DOLLMAKER);
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        if (dollmakerFrame.getPlayer().getSkillTimes() < 1 && !target.isAlive()) {
            dollmakerFrame.getPlayer().incrementSkillTimes();
            target.setAlive(true);
            dollmakerFrame.updateInfo("您复活了" + target.getName() + "！");
            targetFrame.updateInfo("您被人偶师复活了！");
        }
    }

}
