package com.twotwo.logic;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.twotwo.model.*;
import com.twotwo.model.Role.RoleType;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class Game {
    private List<Player> players = new ArrayList<>();
    private List<PlayerFrame> playerFrames = new ArrayList<>();
    private int day = 1;

    // 当前流程步骤（0-护行动，1-妖精行动，2-侦探行动...）
    private int currentStep = -1; // 之后改成-1
    private PlayerFrame currentWaitingFrame; // 当前等待操作的玩家窗口

    // 其他状态管理对象...
    // private int readyCount = 0; // 暂时未使用
    private Map<Location, List<Player>> locationPlayers = new HashMap<>();

    private int locationInputCount = 0; // 记录已完成地点选择的玩家数量
    private boolean isProcessingLocationSelection = false; // 标记是否处于地点选择阶段

    private WerewolfAction werewolfAction = new WerewolfAction(this); // 狼人行动处理对象

    public Game() {
        // 初始化地点
        for (Location.LocationType loc : Location.LocationType.values()) {
            locationPlayers.put(new Location(loc), new ArrayList<>(3));
        }
    }

    // 初始化玩家
    public void initPlayers() {
        List<Role.RoleType> roles = Arrays.asList(
                Role.RoleType.LADY, Role.RoleType.GUARD, Role.RoleType.WITCH, Role.RoleType.DETECTIVE,
                Role.RoleType.LITTLEYUAN, Role.RoleType.SHINEBLUE, Role.RoleType.SNOWWHITE, Role.RoleType.HAMSTER,
                Role.RoleType.DOLLMAKER, Role.RoleType.COOKGIRL, Role.RoleType.FAIRY);
        // 随机选两个角色作为狼人(大小姐不能是狼)
        List<Role.RoleType> wolfCandidates = new ArrayList<>(roles);
        wolfCandidates.remove(Role.RoleType.LADY);
        Collections.shuffle(wolfCandidates); // 随机打乱List
        Role.RoleType wolf1 = wolfCandidates.get(0);
        Role.RoleType wolf2 = wolfCandidates.get(1);

        // 创建11个玩家对象及其窗口
        for (Role.RoleType role : roles) {
            Player player = new Player(role);
            // 设置狼人
            if (role == wolf1 || role == wolf2) {
                player.setCamp(Camp.CampType.WEREWOLF);
            } else {
                player.setCamp(Camp.CampType.GOOD);
            }
            players.add(player);

            // 创建玩家窗口
            PlayerFrame pf = new PlayerFrame(player, this);
            playerFrames.add(pf);
            pf.setVisible(false); // 初始隐藏
        }
    }

    // 游戏启动方法（开始第一天流程）
    public void startGame() {
        // 显示所有玩家窗口
        playerFrames.forEach(pf -> pf.setVisible(true));

        // 从中间开始测试
        // processNextStep();

        // 正式测试，开始厨娘绑定
        startRoleAction(Role.RoleType.COOKGIRL, "请输入绑定对象（1-10）");
    }

    // 执行一天的流程
    public void processDay() {
        for (PlayerFrame pf : playerFrames) {
            pf.updateInfo("===== 第 " + day + " 天  =====");
        }
        // 启动第一步流程（护行动）
        currentStep = 0; // 目前测试阶段，不用从0开始
        processNextStep();
    }

    // 按步骤推进流程
    public void processNextStep() {
        switch (currentStep) {
            case 0: // 步骤1：护行动
                startRoleAction(Role.RoleType.GUARD, "请输入要守护的玩家编号：");
                break;
            case 1: // 步骤2：妖精行动
                startRoleAction(Role.RoleType.FAIRY, "请输入要遮蔽的玩家编号：");
                break;
            case 2: // 步骤3：选择地点
                AllAction.startLocationSelection(this);
                break;
            case 3: // 步骤4：侦探行动
                startRoleAction(Role.RoleType.DETECTIVE, "请输入是否躲入墙内（1-是，0-否）：");
                break;
            case 4: // 步骤5：狼人动刀
                werewolfAction.processWerewolfKill();
                break;
            case 5: // 步骤6：所有人行动（关键步骤）
                // 蓝金和侦探被动技能
                useSkill(getPlayer(Role.RoleType.SHINEBLUE));
                useSkill(getPlayer(Role.RoleType.DETECTIVE));

                // 令仓鼠、魔女、人偶师的Skill按钮可见
                setSkillButtonVisible(Role.RoleType.HAMSTER, true);
                setSkillButtonVisible(Role.RoleType.WITCH, true);
                setSkillButtonVisible(Role.RoleType.DOLLMAKER, true);

                Delay("行动时间...", 20000);
                break;
            case 6: // 步骤7：打印死亡情况
                setSkillButtonVisible(Role.RoleType.HAMSTER, false);
                setSkillButtonVisible(Role.RoleType.WITCH, false);
                setSkillButtonVisible(Role.RoleType.DOLLMAKER, false);
                for (PlayerFrame pf : playerFrames) {
                    pf.updateInfo("===== 第 " + day + " 天 死亡情况 =====");
                    pf.updateInfo(PlayerListUtil.getTodayDeadPlayerList(this));
                }
                currentStep++;
                processNextStep();
                break;
            case 7: // 步骤8：白雪查验
                startRoleAction(Role.RoleType.SNOWWHITE, "请输入要查验的对象：");
                break;
            case 8: // 步骤9：狼人私聊
                werewolfAction.werewolfPrivateChat();
                break;
            case 9: // 步骤10：进聊天室
                Delay("进入聊天室...", 2000);
                break;
            case 10: // 步骤11：大小姐选择顺序
                Delay("大小姐选择发言顺序...", 2000);
                break;
            case 11: // 步骤12：按顺序公开发言（语音先不做）
                setSkillButtonVisible(Role.RoleType.HAMSTER, true);
                setSkillButtonVisible(Role.RoleType.LADY, true);

                List<PlayerFrame> aliveFrames = getAlivePlayerFrames();
                // 最后由大小姐总结归票
                aliveFrames.add(getPlayerFrame(Role.RoleType.LADY));

                // 启动发言流程
                startPublicSpeaking(aliveFrames, 0);
                break;
            case 12: // 步骤13：小原原行动
                startRoleAction(Role.RoleType.LITTLEYUAN, "请输入要干扰的玩家编号：");
                break;
            case 13: // 步骤14：投票
                setSkillButtonVisible(Role.RoleType.HAMSTER, false);
                setSkillButtonVisible(Role.RoleType.LADY, false);
                Delay("投票环节...", 2000);
                break;
            case 14: // 步骤15：公布投票结果
                Delay("投票结果公布...", 2000);
                break;
            case 15: // 步骤16：发表遗言
                Delay("发表遗言阶段...", 2000);
                break;
            default:
                // 当天流程结束
                for (PlayerFrame pf : playerFrames) {
                    pf.updateInfo("===== 第 " + day + " 天 结束 =====");
                }
                resetDay();
                day++;
                processDay();
                return;
        }
    }

    // 启动某个角色的行动（需要操作的玩家显示输入框，其他人显示等待）
    private void startRoleAction(Role.RoleType roleType, String inputHint) {
        // 找到当前需要行动的玩家窗口
        currentWaitingFrame = playerFrames.stream()
                .filter(pf -> pf.getPlayer().getRole() == roleType && pf.getPlayer().isAlive())
                .findFirst()
                .orElse(null);

        if (currentWaitingFrame == null) {
            // 角色已死亡，直接进入下一步
            currentStep++;
            updateCurrentProcess();
            processNextStep();
            return;
        }

        // 给所有玩家发送信息
        for (PlayerFrame pf : playerFrames) {
            if (pf == currentWaitingFrame) {
                // 操作玩家特殊处理：检查是否被妖精蒙蔽
                if (pf.getPlayer().isDeceived() && currentStep <= 6) {
                    pf.updateInfo("你已被妖精蒙蔽，无法行动。");
                    currentStep++;
                    processNextStep();
                    return;
                }

                // 白雪若为狼人，不发动
                if (pf.getPlayer().getRole() == Role.RoleType.SNOWWHITE
                        && pf.getPlayer().getCamp() == Camp.CampType.WEREWOLF) {
                    pf.updateInfo("正在跳过查验...");
                    currentStep++;
                    processNextStep();
                    return;
                }

                // 操作玩家：显示输入提示和输入框
                pf.updateInfo(inputHint);
                // 显示玩家列表（供选择）
                switch (roleType) {
                    case COOKGIRL:
                        pf.showInputArea();
                        pf.updateInfo(PlayerListUtil.getOtherPlayerList(players, pf.getPlayer()));
                        break;
                    case GUARD:
                        pf.showCompleteInputArea();
                        pf.updateInfo(PlayerListUtil.getAlivePlayerList(players));
                        break;
                    case FAIRY:
                        pf.showCompleteInputArea();
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    case DETECTIVE:
                        pf.showInputArea();
                        break;
                    case SNOWWHITE:
                        pf.showInputArea();
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    case LITTLEYUAN:
                        pf.showCompleteInputArea();
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    default:
                        break;
                }
            } else {
                // 其他玩家：显示等待提示
                pf.updateInfo("等待 " + ChineseNameUtil.getChineseName(roleType) + " 行动...");
            }
        }
    }

    // 当玩家输入完成后调用（由PlayerFrame的确认按钮触发）
    public void onPlayerInputCompleted(PlayerFrame pf, String input) {
        // 公开发言阶段，防止取消输入框 之后优化代码
        if (currentStep == 11) {
            ChatUtil.publicChat(playerFrames, currentWaitingFrame, input);
            return;
        }

        // 仓鼠自爆响应
        if ((currentStep == 5 || currentStep == 11 || currentStep == 12)
                && pf.getPlayer().getRole() == Role.RoleType.HAMSTER) {
            try {
                int targetIndex = Integer.parseInt(input);
                Player Target = PlayerListUtil.getOtherAlivePlayer(players, pf.getPlayer(), targetIndex);
                if (Target != null) {
                    SkillExecutor.handleHAMSTERInput(this, Target);
                }
            } catch (Exception e) {
                pf.updateInfo("自爆失败！");
            } finally {
                setSkillButtonVisible(Role.RoleType.HAMSTER, false);
            }

            return;
        }

        // 魔女发射激光响应
        if (currentStep == 5 && pf.getPlayer().getRole() == Role.RoleType.WITCH) {
            try {
                int targetIndex = Integer.parseInt(input);
                Player Target = PlayerListUtil.getSameLocationPlayer(PlayerListUtil.getSameLocationPlayerList(
                        players, pf.getPlayer(), 0), targetIndex);
                if (Target != null) {
                    SkillExecutor.handleWITCHInput(this, Target);
                }
            } catch (Exception e) {
                pf.updateInfo("发射激光失败！");
            } finally {
                setSkillButtonVisible(Role.RoleType.WITCH, false);
            }
            return;
        }

        // 人偶师复活响应
        if (currentStep == 5 && pf.getPlayer().getRole() == Role.RoleType.DOLLMAKER) {
            try {
                int targetIndex = Integer.parseInt(input);
                Player Target = PlayerListUtil.getPastDeadPlayer(this, targetIndex);
                if (Target != null) {
                    SkillExecutor.handleDOLLMAKERInput(this, Target);
                }
            } catch (Exception e) {
                pf.updateInfo("复活失败！");
            } finally {
                setSkillButtonVisible(Role.RoleType.DOLLMAKER, false);
            }
            return;
        }

        // 地点选择阶段处理
        if (isProcessingLocationSelection) {
            locationInputCount++;

            // 处理输入
            pf.updateInfo("已选择地点：" + input);
            pf.getInputPanel().setVisible(false); // 需要在PlayerFrame中添加getInputPanel()方法

            // 记录玩家选择的地点（需要解析input为LocationType）
            try {
                int locIndex = Integer.parseInt(input);
                Location.LocationType locationType = switch (locIndex) {
                    case 1 -> Location.LocationType.DRAGON_CAVE;
                    case 2 -> Location.LocationType.KITCHEN;
                    case 3 -> Location.LocationType.STORE;
                    case 4 -> Location.LocationType.LIBRARY;
                    default -> throw new IllegalArgumentException("无效地点");
                };
                pf.getPlayer().setCurrentLocation(locationType);
            } catch (Exception e) {
                pf.updateInfo("输入无效，已自动分配随机地点");
                // 随机分配地点
                List<Location.LocationType> locTypes = Arrays.asList(
                        Location.LocationType.DRAGON_CAVE,
                        Location.LocationType.KITCHEN,
                        Location.LocationType.STORE,
                        Location.LocationType.LIBRARY);
                Location.LocationType randomLocation = locTypes.get(new java.util.Random().nextInt(locTypes.size()));
                pf.getPlayer().setCurrentLocation(randomLocation);
            }

            // 检查是否所有存活玩家都已完成选择
            long aliveCount = players.stream().filter(Player::isAlive).count();
            if (locationInputCount >= aliveCount) {
                // 所有玩家完成选择，结束地点选择阶段
                isProcessingLocationSelection = false;
                currentStep++;
                processNextStep();
            } else {
                // 提示等待其他玩家
                pf.updateInfo("等待其他玩家选择地点...（已完成" + locationInputCount + "/" + aliveCount + "）");
            }
            return;
        }

        // 非地点选择阶段的普通输入处理
        // 处理输入（这里简化为直接记录，实际可解析数字执行技能）
        pf.updateInfo("已确认输入：" + input);
        pf.hideInputArea(); // 隐藏输入区域

        // 响应输入：处理厨娘绑定逻辑
        if (currentStep == -1) {
            if (pf.getPlayer().getRole() == Role.RoleType.COOKGIRL) {
                // 厨娘的boundPlayer设置
                try {
                    int targetIndex = Integer.parseInt(input);
                    Player boundPlayer = PlayerListUtil.getOtherPlayer(players, pf.getPlayer(), targetIndex);
                    if (boundPlayer != null) {
                        pf.getPlayer().setBoundPlayer(boundPlayer);
                        pf.updateInfo("已绑定玩家：" + boundPlayer.getName());
                        boundPlayer.setIsBound(true);
                    } else {
                        pf.updateInfo("输入无效，未绑定任何玩家");
                    }
                } catch (Exception e) {
                    pf.updateInfo("输入格式错误，未绑定任何玩家");
                }

            }
            processDay(); // 绑定完成后进入第一天
            return;
        }

        // 响应输入：处理护守护逻辑
        if (currentStep == 0) {
            if (pf.getPlayer().getRole() == Role.RoleType.GUARD) {
                try {
                    int targetIndex = Integer.parseInt(input);
                    Player guardTarget = PlayerListUtil.getAlivePlayer(players, targetIndex);
                    if (guardTarget != null) {
                        pf.updateInfo("已守护玩家：" + guardTarget.getName());
                        guardTarget.setIsGuarded(true);
                    } else {
                        pf.updateInfo("输入无效，未守护任何玩家");
                    }
                } catch (Exception e) {
                    pf.updateInfo("输入格式错误，未守护任何玩家");
                }
            }
        }

        // 响应输入：处理妖精蒙蔽逻辑
        if (currentStep == 1) {
            if (pf.getPlayer().getRole() == Role.RoleType.FAIRY) {
                try {
                    int targetIndex = Integer.parseInt(input);
                    Player deceiveTarget = PlayerListUtil.getOtherAlivePlayer(players, pf.getPlayer(), targetIndex);
                    if (deceiveTarget != null && !deceiveTarget.isGuarded()
                            && pf.getPlayer().getSkillTimes() <= 2) {
                        pf.getPlayer().incrementSkillTimes();
                        pf.updateInfo("已蒙蔽玩家：" + deceiveTarget.getName());
                        deceiveTarget.setIsDeceived(true);
                    }
                } catch (Exception e) {
                    pf.updateInfo("输入格式错误，未蒙蔽任何玩家");
                }
            }
        }

        // 响应输入：设置侦探inWall
        if (currentStep == 3) {
            if (pf.getPlayer().getRole() == Role.RoleType.DETECTIVE) {
                int ifInWall = Integer.parseInt(input);
                pf.getPlayer().setInWall(ifInWall == 1);
            }
        }

        // 响应输入：狼人动刀逻辑
        if (currentStep == 4) {
            werewolfAction.handleKillSelection(pf, input);
            return;
        }

        // 响应输入：白雪查验
        if (currentStep == 7) {
            if (pf.getPlayer().getRole() == Role.RoleType.SNOWWHITE) {
                // 处理白雪的查验逻辑
                int targetIndex = Integer.parseInt(input);
                Player target = PlayerListUtil.getOtherAlivePlayer(players, pf.getPlayer(), targetIndex);
                if (target != null) {
                    String camp = target.getCamp() == Camp.CampType.WEREWOLF ? "狼人" : "好人";
                    pf.updateInfo(target.getName() + "的身份是：" + camp);
                }
            }
        }

        // 响应输入：狼人私聊
        if (currentStep == 8) {
            werewolfAction.handleChatInput(pf, input);
            return;
        }

        // 响应输入：小原原行动
        if (currentStep == 12) {
            if (pf.getPlayer().getRole() == Role.RoleType.LITTLEYUAN) {
                try {
                    int targetIndex = Integer.parseInt(input);
                    Player disturbTarget = PlayerListUtil.getOtherAlivePlayer(players, pf.getPlayer(), targetIndex);
                    if (disturbTarget != null && pf.getPlayer().getSkillTimes() <= 2) {
                        pf.getPlayer().incrementSkillTimes();
                        pf.updateInfo("已干扰玩家：" + disturbTarget.getName());
                        disturbTarget.setIsDisturbed(true);
                    } else {
                        pf.updateInfo("输入无效，未干扰任何玩家");
                    }
                } catch (Exception e) {
                    pf.updateInfo("输入格式错误，未干扰任何玩家");
                }
            }
        }

        // 推进到下一步
        /**
         * 注意：某些步骤可能需要等待多个玩家输入才能推进，
         * 这里简化为单个玩家输入后直接推进，实际可根据需求进行调整
         */
        currentStep++;
        updateCurrentProcess();
        processNextStep();
    }

    // 取消输入的处理逻辑
    public void onPlayerInputCancelled(PlayerFrame pf) {
        // 护和妖精取消响应：跳过
        if (currentStep == 0 || currentStep == 1 || currentStep == 12) {
            currentStep++;
            updateCurrentProcess();
            processNextStep();
        }

        // 公开发言阶段取消响应：跳过倒计时，轮到下一位发言
        if (currentStep == 11) {
            if (pf.getCountdownUtil().isCountingDown()) {
                pf.getCountdownUtil().finishCountdown();
            }
            List<PlayerFrame> aliveFrames = getAlivePlayerFrames();
            aliveFrames.add(getPlayerFrame(Role.RoleType.LADY));
            int index = aliveFrames.indexOf(pf);
            SwingUtilities.invokeLater(() -> {
                Timer delayTimer = new Timer(500, e -> startPublicSpeaking(aliveFrames, index + 1));
                delayTimer.setRepeats(false);
                delayTimer.start();
            });
            return;
        }
    }

    /**
     * 启动公开发言流程
     * 
     * @param frames 按发言顺序排列的玩家窗口列表
     * @param index  当前发言玩家索引
     */
    private void startPublicSpeaking(List<PlayerFrame> frames, int index) {
        if (index >= frames.size()) {
            // 所有玩家发言结束，进入下一步
            currentStep++;
            processNextStep();
            return;
        }

        // 隐藏所有玩家的输入区域
        playerFrames.forEach(pf -> pf.hideInputArea());

        currentWaitingFrame = frames.get(index);
        String speakerName = currentWaitingFrame.getPlayer().getName();

        // 通知所有玩家当前发言状态
        notifyAllPlayers(speakerName + "正在发言...");

        // 配置当前发言玩家
        currentWaitingFrame.updateInfo("轮到你发言（10秒倒计时）：");
        currentWaitingFrame.showCompleteInputArea();
        // 启动10秒倒计时
        currentWaitingFrame.getCountdownUtil().startCountdown(
                currentWaitingFrame.getScrollPane(),
                10,
                () -> {
                    // 倒计时结束回调：记录发言内容并进入下一位
                    String speech = currentWaitingFrame.getInputField().getText().trim();
                    if (!speech.isEmpty()) {
                        ChatUtil.publicChat(playerFrames, currentWaitingFrame, speech);
                    }
                    currentWaitingFrame.hideInputArea(); // 倒计时结束时才隐藏输入区域
                    // 延迟0.5秒进入下一位发言，避免界面闪烁
                    SwingUtilities.invokeLater(() -> {
                        Timer delayTimer = new Timer(500, e -> startPublicSpeaking(frames, index + 1));
                        delayTimer.setRepeats(false);
                        delayTimer.start();
                    });
                });
    }

    /*
     * 一天结束，重置状态
     */
    private void resetDay() {
        // 重置守护状态
        Player IsGuardedOne = players.stream()
                .filter(player -> player.isGuarded())
                .findFirst()
                .orElse(null);
        if (IsGuardedOne != null)
            IsGuardedOne.setIsGuarded(false);

        // 重置蒙蔽状态
        Player IsDeceivedOne = players.stream()
                .filter(player -> player.isDeceived())
                .findFirst()
                .orElse(null);
        if (IsDeceivedOne != null)
            IsDeceivedOne.setIsDeceived(false);

        // 重置干扰状态
        Player IsDisturbedOne = players.stream()
                .filter(player -> player.isDisturbed())
                .findFirst()
                .orElse(null);
        if (IsDisturbedOne != null)
            IsDisturbedOne.setIsDisturbed(false);

        // 重置侦探是否在墙内状态
        Player detective = getPlayer(Role.RoleType.DETECTIVE);
        if (detective != null)
            detective.setInWall(false);

        // 重置当前地点
        players.forEach(player -> player.setCurrentLocation(null));

        currentStep = -1;
        currentWaitingFrame = null;

    }

    /**
     * utils
     * 
     */
    // 广播消息给所有玩家
    public void notifyAllPlayers(String message) {
        for (PlayerFrame pf : playerFrames) {
            pf.updateInfo(message);
        }
    }

    // 控制指定角色的Skill按钮可见性
    public void setSkillButtonVisible(Role.RoleType roleType, boolean visible) {
        // 查找指定角色的玩家窗口
        PlayerFrame targetFrame = getPlayerFrame(roleType);

        int SkillTimes = targetFrame != null ? targetFrame.getPlayer().getSkillTimes() : 0;
        final boolean visibleFlag = ((roleType == Role.RoleType.LADY
                || roleType == Role.RoleType.DOLLMAKER) && SkillTimes > 0) ? false : visible;

        if (targetFrame != null && (targetFrame.getPlayer().GameAlive(this) || !visibleFlag)) {
            // 在Swing线程中更新UI
            SwingUtilities.invokeLater(() -> {
                targetFrame.getSouthContainer().setVisible(visibleFlag);
                targetFrame.getSkillPanel().setVisible(visibleFlag);
                targetFrame.getSkillBtn().setVisible(visibleFlag);
            });
        }
    }

    // Skill按钮触发
    public void useSkill(Player player) {
        if (player == null || !player.GameAlive(this)) {
            return;
        }
        // 调用 SkillExecutor 执行对应角色的技能
        SkillExecutor.executeSkill(player, this);
    }

    // 获取指定角色玩家
    public Player getPlayer(Role.RoleType roleType) {
        return players.stream()
                .filter(player -> player.getRole() == roleType)
                .findFirst()
                .orElse(null);
    }

    // 获取指定角色界面
    public PlayerFrame getPlayerFrame(Role.RoleType roleType) {
        return playerFrames.stream()
                .filter(pf -> pf.getPlayer().getRole() == roleType)
                .findFirst()
                .orElse(null);
    }

    // 延迟执行下一步操作
    public void Delay(String message, int milliseconds) {
        notifyAllPlayers(message);
        SwingUtilities.invokeLater(() -> {
            Timer timer = new Timer(milliseconds, e -> {
                currentStep++;
                processNextStep();
            });
            timer.setRepeats(false); // 只执行一次
            timer.start();
        });
    }

    /**
     * 游戏结束
     * 
     * @return
     */
    public void setGameOver(int result) {
        if (result == 0) {
            notifyAllPlayers("游戏结束，好人阵营获胜！");
        } else if (result == 1) {
            notifyAllPlayers("游戏结束，狼人阵营获胜！");
        }
    }

    // getters
    // 获取存活玩家
    public List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .collect(Collectors.toList());
    }

    // 获取存活玩家界面
    public List<PlayerFrame> getAlivePlayerFrames() {
        return playerFrames.stream()
                .filter(pf -> pf.getPlayer().isAlive())
                .collect(Collectors.toList());
    }

    // 获取玩家列表
    public List<Player> getPlayers() {
        return players;
    }

    // 获取玩家界面列表
    public List<PlayerFrame> getPlayerFrames() {
        return playerFrames;
    }

    // 获取是否正在处理位置选择
    public boolean getIsProcessingLocationSelection() {
        return isProcessingLocationSelection;
    }

    // 获取当前等待的玩家界面
    public PlayerFrame getCurrentWaitingFrame() {
        return currentWaitingFrame;
    }

    // 获取当前游戏步骤
    public int getCurrentStep() {
        return currentStep;
    }

    // 获取当前天数
    public int getCurrentDay() {
        return day;
    }

    // 更新当前流程状态给所有玩家(测试用)
    public void updateCurrentProcess() {
        notifyAllPlayers("current process" + Integer.toString(currentStep + 1));
    }

    // 获取存活人数
    public int AliveNumber() {
        return (int) players.stream().filter(Player::isAlive).count();
    }

    // setters
    public void setIsProcessingLocationSelection(boolean isProcessing) {
        this.isProcessingLocationSelection = isProcessing;
    }

    public void setLocationInputCount(int count) {
        this.locationInputCount = count;
    }

    public void setCurrentWaitingFrame(PlayerFrame currentWaitingFrame) {
        this.currentWaitingFrame = currentWaitingFrame;
    }

    public void setCurrentStep(int step) {
        this.currentStep = step;
    }

}