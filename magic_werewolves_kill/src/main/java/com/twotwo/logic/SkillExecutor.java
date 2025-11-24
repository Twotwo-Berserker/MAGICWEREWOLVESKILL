package com.twotwo.logic;

import java.util.List;

import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class SkillExecutor {
    // 技能唯一接口，其他技能私有，通过此方法调用
    public static void executeSkill(Player player, Game game) {
        if (player.isDeceived() && game.getCurrentStep() <= 6) {
            game.getPlayerFrame(player.getRole()).updateInfo("你已被妖精蒙蔽，无法行动！");
            return;
        }
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
                break;
            // 魔女
            case WITCH:
                PlayerFrame witchFrame = game.getPlayerFrame(Role.RoleType.WITCH);
                witchFrame.updateInfo("请输入发射激光的对象：");
                String sameLocationList = PlayerListUtil.getSameLocationPlayerListToString(
                        PlayerListUtil.getSameLocationPlayerList(game.getPlayers(), player, 0));
                witchFrame.updateInfo(sameLocationList);
                if (sameLocationList == "当前地点无其他玩家。") {
                    witchFrame.updateInfo("无法使用技能！");
                    return;
                }
                witchFrame.showInputArea();
                break;
            // 人偶师
            case DOLLMAKER:
                PlayerFrame dollmakerFrame = game.getPlayerFrame(Role.RoleType.DOLLMAKER);
                dollmakerFrame.updateInfo("请输入复活的对象：");
                String pastDeadList = PlayerListUtil.getPastDeadPlayerList(game);
                dollmakerFrame.updateInfo(pastDeadList);
                if (pastDeadList == "所有玩家均存活。") {
                    dollmakerFrame.updateInfo("没有可复活的对象!");
                    return;
                }
                dollmakerFrame.showInputArea();
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
                            shineBlueFrame
                                    .updateInfo(PlayerListUtil.getSameLocationPlayerListToString(sameLocationPlayers));
                        }
                        break; // 只需提示一次
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
            detectiveFrame.updateInfo(PlayerListUtil.getSameLocationPlayerListToString(sameLocationPlayers));
        }
    }

    // 仓鼠自爆
    private static void HAMSTER_Skill(Game game, Player target) {
        PlayerFrame hamsterFrame = game.getPlayerFrame(Role.RoleType.HAMSTER);
        Player hamster = hamsterFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        if (hamster.getSkillTimes() == 0 &&
                hamster.GameAlive(game) && target.GameAlive(game)) {
            hamster.incrementSkillTimes();
            hamster.Die(game);
            hamsterFrame.updateInfo("您自爆了，带走了" + target.getName() + "！");
            if (!(game.getCurrentStep() == 5 && target.isGuarded())) {
                target.Die(game);
                targetFrame.updateInfo("您被仓鼠自爆带走，死亡！");
            }
        }
    }

    // 对外接口，处理仓鼠输入
    public static void handleHAMSTERInput(Game game, Player target) {
        HAMSTER_Skill(game, target);
    }

    // 魔女光波
    private static void WITCH_Skill(Game game, Player target) {
        PlayerFrame witchFrame = game.getPlayerFrame(Role.RoleType.WITCH);
        Player witch = witchFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        witch.incrementSkillTimes();
        // 魔女光波无视护卫
        if (target.getCamp() == witch.getCamp()) {
            witch.Die(game);
            witchFrame.updateInfo("您攻击了队友，自身死亡！");
        } else {
            target.Die(game);
            witchFrame.updateInfo("您攻击了敌人，敌人死亡！");
            targetFrame.updateInfo("您被魔女光波击中，死亡！");
        }
    }

    // 对外接口，处理魔女输入
    public static void handleWITCHInput(Game game, Player target) {
        WITCH_Skill(game, target);
    }

    // 人偶师复活
    private static void DOLLMAKER_Skill(Game game, Player target) {
        PlayerFrame dollmakerFrame = game.getPlayerFrame(Role.RoleType.DOLLMAKER);
        Player dollmaker = dollmakerFrame.getPlayer();
        PlayerFrame targetFrame = game.getPlayerFrame(target.getRole());
        if (dollmaker.getSkillTimes() < 1 && !target.isAlive()) {
            dollmaker.incrementSkillTimes();
            target.setAlive(true);
            target.setDeathDay(-1);
            dollmakerFrame.updateInfo("您复活了" + target.getName() + "！");
            targetFrame.updateInfo("您被人偶师复活了！");
        }
    }

    // 对外接口，处理人偶师输入
    public static void handleDOLLMAKERInput(Game game, Player target) {
        DOLLMAKER_Skill(game, target);
    }

}
