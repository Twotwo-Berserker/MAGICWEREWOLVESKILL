package com.twotwo.logic;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.twotwo.logic.*;
import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class WerewolfAction {
    private Game game;
    private int Finished = 0;
    private int AliveWerewolfNumber = 2; // 初始有两个狼人

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
            PlayerFrame wolfFrame = game.getPlayerFrame(werewolf.getRole());
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
            if (werewolf.isDeceived() && game.getCurrentStep() <= 6) {
                wolfFrame.updateInfo("你已被妖精蒙蔽，无法行动。");
                Finished++;
                advanceToNextStep();
                continue;
            }
            if (wolfFrame != null) {
                // 就算没人在也可选墙杀
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
                    target.Die(game);
                }
            } else if (targetIndex == targets.size()) {
                // 选择了墙
                // 查询侦探是否在墙内
                Player detective = game.getPlayer(Role.RoleType.DETECTIVE);
                if (detective != null && detective.getCurrentLocation() == wolfFrame.getPlayer().getCurrentLocation() 
                        && detective.isInWall() && !detective.isGuarded()) {
                    detective.Die(game);
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
            game.updateCurrentProcess();
            game.processNextStep();
            Finished = 0; // 为下一次行动做准备
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
                Player anotherWolf = Alivewerewolves.stream()
                        .filter(wolf -> wolf.isAlive() && wolf != playerFrame.getPlayer())
                        .findFirst()
                        .orElse(null);
                // 判断是否还存在队友
                if (anotherWolf == null) {
                    playerFrame.updateInfo("狼人队友已死亡，正在跳过此环节...");
                    SwingUtilities.invokeLater(() -> {
                        Timer timer = new Timer(2000, e -> {
                            game.setCurrentStep(9);
                            game.updateCurrentProcess();
                            game.processNextStep();
                            playerFrame.hideInputArea();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    });
                    return;
                } else {
                    // 确保在Swing线程中显示输入区域
                    SwingUtilities.invokeLater(() -> {
                        playerFrame.showInputArea(); // 显示输入框
                    });
                    playerFrame.updateInfo("狼人私聊时间，倒计时60秒...请输入私聊内容：");
                }
                // 倒计时应在输入区域显示后启动
                playerFrame.getCountdownUtil().startCountdown(
                        playerFrame.getScrollPane(),
                        60,
                        () -> {
                            if (game.getCurrentStep() == 8) {
                                game.setCurrentStep(9);
                                game.updateCurrentProcess();
                                game.processNextStep();
                                playerFrame.hideInputArea();
                                game.getPlayerFrame(anotherWolf.getRole()).hideInputArea();
                            }

                        });
            } else {
                // 非狼人玩家：显示等待提示
                playerFrame.updateInfo("等待狼人私聊结束...");
            }
        }
    }

    public void handleChatInput(PlayerFrame senderFrame, String input) {
        // 验证发送者是否为存活狼人
        List<Player> aliveWerewolves = getAliveWolves();
        if (!aliveWerewolves.contains(senderFrame.getPlayer())) {
            senderFrame.updateInfo("你不是狼人，无法发送私聊！");
            return;
        }

        boolean sented = false;
        // 遍历所有玩家窗口，仅向存活狼人队友发送消息
        for (PlayerFrame receiverFrame : game.getPlayerFrames()) {
            // 排除发送者自身，且接收者必须是存活狼人
            if (receiverFrame != senderFrame && aliveWerewolves.contains(receiverFrame.getPlayer())) {
                ChatUtil.privateChat(senderFrame, receiverFrame, input);
                sented = true;
            }
            if (sented) {
                if (senderFrame.getCountdownUtil().isCountingDown()) {
                    senderFrame.getCountdownUtil().finishCountdown();
                }
                Finished++;
                advanceToNextStep();
                break;
            }
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
}
