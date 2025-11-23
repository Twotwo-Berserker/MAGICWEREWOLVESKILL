package com.twotwo.logic;

import java.util.List;

import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class SkillExecutor {
    // 技能唯一接口，其他技能私有，通过此方法调用
    public static void executeSkill(Player player, Game game) {
        switch (player.getRole()) {
            // 蓝金和侦探的技能是被动触发
            case SHINEBLUE:
                SHINEBLUE_Skill(game);
                break;
            case DETECTIVE:
                DETECTIVE_Skill(game);
                break;
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
                PlayerFrame witchFrame = game.getPlayerFrame(Role.RoleType.WITCH);
                witchFrame.updateInfo("请输入发射激光的对象：");
                witchFrame.updateInfo(PlayerListUtil.getOtherAlivePlayerList(game.getPlayers(), player));
                witchFrame.showInputArea();
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
    private static void SHINEBLUE_Skill(Game game) {
        // 查找蓝金的界面
        PlayerFrame shineBlueFrame = game.getPlayerFrame(Role.RoleType.SHINEBLUE);
        Player shineBlue = shineBlueFrame.getPlayer();
        // 蓝金被动技能不被妖精影响
        if (shineBlue.isAlive() && shineBlue.getCamp() == Camp.CampType.GOOD) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    shineBlue, 1);
            // 如果存在狼人
            if (!sameLocationPlayers.isEmpty()) {
                // 处理存在狼人时的逻辑
                for (Player p : sameLocationPlayers) {
                    if (p.getCamp() == Camp.CampType.WEREWOLF) {
                        shineBlueFrame.updateInfo("发现狼人存在！！！");
                        if (shineBlue.getSkillTimes() == 0) {
                            shineBlue.setAlive(true);
                            shineBlue.setDeathDay(-1);
                            shineBlue.incrementSkillTimes();
                            StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
                            for (Player sp : sameLocationPlayers) {
                                sb.append("- ").append(sp.getName()).append("\n");
                            }
                            shineBlueFrame.updateInfo(sb.toString());
                        }
                    }
                }
            }
        }
    }

    // 侦探视野
    private static void DETECTIVE_Skill(Game game) {
        PlayerFrame detectiveFrame = game.getPlayerFrame(Role.RoleType.DETECTIVE);
        Player detective = detectiveFrame.getPlayer();
        if (detective.isAlive() && detective.isInWall()) {
            List<Player> sameLocationPlayers = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(),
                    detective, 0);
            StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
            for (Player sp : sameLocationPlayers) {
                sb.append("- ").append(sp.getName()).append("\n");
            }
            detectiveFrame.updateInfo(sb.toString());
        }
    }

    // 仓鼠自爆
    private static void HAMSTER_Skill(Game game, Role.RoleType target) {
        PlayerFrame hamsterFrame = game.getPlayerFrame(Role.RoleType.HAMSTER);
        Player hamster = hamsterFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target);
        Player targetPlayer = targetFrame.getPlayer();
        if (hamster.getSkillTimes() < 1 &&
                hamster.GameAlive(game) && targetPlayer.GameAlive(game)) {
            hamster.incrementSkillTimes();
            hamster.Die(game);
            targetPlayer.Die(game);
            hamsterFrame.updateInfo("您自爆了，带走了" + targetPlayer.getName() + "！");
            targetFrame.updateInfo("您被仓鼠自爆带走，死亡！");
        }
    }

    // 魔女攻击
    private static void WITCH_Skill(Game game, Player target) {
        PlayerFrame witchFrame = game.getPlayerFrame(Role.RoleType.WITCH);
        Player witch = witchFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        Player targetPlayer = targetFrame.getPlayer();
        witch.incrementSkillTimes();
        if (target.getCamp() == witch.getCamp()) {
            witch.Die(game);
            witchFrame.updateInfo("您攻击了队友，自身死亡！");
        } else {
            targetPlayer.Die(game);
            witchFrame.updateInfo("您攻击了敌人，敌人死亡！");
            targetFrame.updateInfo("您被魔女光波击中，死亡！");
        }
    }

   private static void DOLLMAKER_Skill(Game game, Player target) {
       PlayerFrame dollmakerFrame = game.getPlayerFrame(Role.RoleType.DOLLMAKER);
        Player dollmaker = dollmakerFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        Player targetPlayer = targetFrame.getPlayer();
        if (dollmaker.getSkillTimes() < 1 && !targetPlayer.isAlive()) {
            dollmaker.incrementSkillTimes();
            targetPlayer.setAlive(true);
            targetPlayer.setDeathDay(-1);
            dollmakerFrame.updateInfo("您复活了" + targetPlayer.getName() + "！");
            targetFrame.updateInfo("您被人偶师复活了！");
        }
    }

}
