package com.twotwo.logic;

import java.util.*;
import java.util.stream.Collectors;
import com.twotwo.logic.*;
import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class WerewolfAction {
    private Game game;
    private int Finished = 0;
    private int AliveWerewolfNumber = 0;

    public WerewolfAction(Game game) {
        this.game = game;
    }

    public List<Player> getAliveWolves() {
        // 获取所有存活的狼人
        return game.getPlayers().stream()
                .filter(player -> player.isAlive() && player.getCamp() == Camp.CampType.WEREWOLF)
                .collect(Collectors.toList());
    }

    /**
     * 处理狼人动刀流程
     */
    public void processWerewolfKill() {
        notifyOtherPlayers("等待狼人行动...");
        // 获取所有存活的狼人
        List<Player> aliveWerewolves = getAliveWolves();

        if (aliveWerewolves.isEmpty()) {
            return; // 没有存活狼人，无需动刀
        }

        AliveWerewolfNumber = aliveWerewolves.size();

        // 显示动刀目标列表并让狼人选择
        for (Player werewolf : aliveWerewolves) {
            PlayerFrame wolfFrame = getPlayerFrame(werewolf);
            // 获取所有可被刀的目标（存活且不在墙内的玩家）
            List<Player> targets = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(), wolfFrame.getPlayer(),
                    0);
            // 操作玩家特殊处理
            // 检查是否为侦探且在墙内
            if (werewolf.getRole() == Role.RoleType.DETECTIVE && werewolf.isInWall() && !werewolf.isBound()) {
                wolfFrame.updateInfo("你在墙内，无法动刀。");
                Finished++;
                advanceToNextStep();
                continue;
            }

            // 检查是否被妖精蒙蔽
            if (werewolf.isDeceived() && game.getCurrentDay() <= 6) {
                wolfFrame.updateInfo("你已被妖精蒙蔽，无法行动。");
                Finished++;
                advanceToNextStep();
                continue;
            }
            if (wolfFrame != null) {
                if (targets.isEmpty()) {
                    wolfFrame.updateInfo("没有可刀的目标，跳过本轮动刀");
                    continue;
                }
                showKillSelection(wolfFrame, targets);
            }
        }
    }

    /**
     * 向狼人显示可刀目标并等待选择
     */
    private void showKillSelection(PlayerFrame wolfFrame, List<Player> targets) {
        // 生成目标选择列表
        StringBuilder targetList = new StringBuilder("请选择要刀的玩家（输入编号）：\n");
        for (int i = 0; i < targets.size(); i++) {
            Player target = targets.get(i);
            targetList.append(String.format("%d. %s\n",
                    i + 1,
                    target.getName()));
        }
        // targetList加个墙
        targetList.append(String.format("%d. 墙",
                targets.size() + 1));

        // 向狼人显示信息并等待输入
        wolfFrame.updateInfo("===== 狼人动刀阶段 =====");
        wolfFrame.updateInfo(targetList.toString());
        wolfFrame.showInputArea();
    }

    /**
     * 处理狼人选择的刀人目标
     */
    public void handleKillSelection(PlayerFrame wolfFrame, String input) {
        try {
            int targetIndex = Integer.parseInt(input) - 1;
            List<Player> targets = PlayerListUtil.getSameLocationPlayerList(game.getPlayers(), wolfFrame.getPlayer(),
                    0);

            if (targetIndex >= 0 && targetIndex < targets.size()) {
                Player target = targets.get(targetIndex);
                if (target.isGuarded() || target.getRole() == Role.RoleType.COOKGIRL
                        && target.getBoundPlayer().getCurrentLocation() == wolfFrame.getPlayer().getCurrentLocation()) {
                    // 目标被守护或被厨娘绑定且厨娘在同一地点，刀人失败
                } else {
                    // 执行刀人操作
                    target.setAlive(false);
                    target.setDeathDay(game.getCurrentDay());
                    // 通知所有玩家结果
                    // notifyAllPlayers(String.format("狼人刀了%s，%s已死亡",
                    // target.getName(),
                    // target.getName()));
                }
            } else if (targetIndex == targets.size()) {
                // 选择了墙
                // 查询侦探是否在墙内
                Player detective = game.getPlayers().stream()
                        .filter(player -> player.getRole() == Role.RoleType.DETECTIVE)
                        .findFirst()
                        .orElse(null);
                if (detective != null && detective.isInWall() && !detective.isBound()
                        && detective.getCurrentLocation() == wolfFrame.getPlayer().getCurrentLocation()) {
                    detective.setAlive(false);
                    detective.setDeathDay(game.getCurrentDay());
                    // notifyAllPlayers("狼人刀了侦探，侦探已死亡");
                }
            } else {
                wolfFrame.updateInfo("选择的编号无效，已跳过本次动刀");
            }
            Finished++;
        } catch (NumberFormatException e) {
            wolfFrame.updateInfo("输入格式错误，已跳过本次动刀");
        }

        advanceToNextStep();
    }

    // 推进到下一步流程
    private void advanceToNextStep() {
        if (Finished >= AliveWerewolfNumber) {
            game.setCurrentStep(game.getCurrentStep() + 1);
            game.processNextStep();
        }
    }

    /*
     * 狼人私聊功能
     */
    public void werewolfPrivateChat() {
        List<PlayerFrame> playerFrames = game.getPlayerFrames();
        List<Player> Alivewerewolves = getAliveWolves();
        for (PlayerFrame playerFrame : playerFrames) {
            if (Alivewerewolves.contains(playerFrame.getPlayer())) {
                // 狼人玩家：显示倒计时
                try {
                    // 判断是否还存在队友
                    if (AliveWerewolfNumber <= 1) {
                        playerFrame.updateInfo("狼人队友已死亡，正在跳过此环节...");
                    } else {
                        // 寻找狼人队友界面
                        for (PlayerFrame SecondFrame : playerFrames) {
                            if (Alivewerewolves.contains(SecondFrame.getPlayer())
                                    && SecondFrame != playerFrame) {
                                playerFrame.showInputArea();
                            }
                            break;
                        }
                    }
                    CountdownUtil.startCountdown(
                            playerFrame,
                            playerFrame.getScrollPane(),
                            60,
                            () -> {
                                game.setCurrentStep(game.getCurrentStep() + 1);
                                game.updateCurrentProcess();
                                game.processNextStep();
                            });
                    playerFrame.updateInfo("狼人私聊时间，倒计时60秒...");

                } catch (IllegalArgumentException e) {
                    // 捕获布局不符合要求的异常，避免程序崩溃
                    playerFrame.updateInfo("倒计时初始化失败：" + e.getMessage());
                }
            } else {
                // 非狼人玩家：显示等待提示
                playerFrame.updateInfo("等待狼人私聊结束...");
            }
        }
    }
    
    public void handleChatInput(List<PlayerFrame> playerFrames, String input) {
        for (PlayerFrame playerFrame : playerFrames) {
            playerFrame.updateInfo("[狼人私聊内容] ：" + input);
        }
    }

    /**
     * 通知除狼人外的所有玩家信息
     */
    private void notifyOtherPlayers(String message) {
        for (PlayerFrame frame : game.getPlayerFrames()) {
            if (frame.getPlayer().getCamp() != Camp.CampType.WEREWOLF) {
                frame.updateInfo(message);
            }
        }
    }

    /**
     * 根据玩家获取对应的窗口
     */
    private PlayerFrame getPlayerFrame(Player player) {
        for (PlayerFrame frame : game.getPlayerFrames()) {
            if (frame.getPlayer().equals(player)) {
                return frame;
            }
        }
        return null;
    }
}
