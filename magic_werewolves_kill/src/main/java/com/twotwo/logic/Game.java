package com.twotwo.logic;

import java.util.*;
import com.twotwo.model.*;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class Game {
    private List<Player> players = new ArrayList<>();
    private List<PlayerFrame> playerFrames = new ArrayList<>();
    private int day = 1;

    // 当前流程步骤（0-护行动，1-妖精行动，2-侦探行动...）
    private int currentStep = -1;
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

        // 等多人游玩时，需要等待所有玩家点击进入游戏
        /*
         * for (PlayerFrame pf : playerFrames) {
         * pf.updateInfo("正在等待玩家准备...");
         * }
         * if (allPlayersReady())
         * processDay();
         */

        // 目前测试，直接厨娘绑定然后进入第一天
        startRoleAction(Role.RoleType.COOKGIRL, "请输入绑定对象（1-10）");
    }

    // 执行一天的流程
    public void processDay() {
        for (PlayerFrame pf : playerFrames) {
            pf.updateInfo("===== 第 " + day + " 天  =====");
        }
        // 启动第一步流程（护行动）
        currentStep = 0;
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
            case 5: // 步骤6：所有人行动
                SkillExecutor.SHINEBLUE_Skill(this);
                SkillExecutor.DETECTIVE_Skill(this);
                break;
            case 6: // 步骤7：打印死亡情况
                for (PlayerFrame pf : playerFrames) {
                    pf.updateInfo("===== 第 " + day + " 天 死亡情况 =====");
                    pf.updateInfo(PlayerListUtil.getTodayDeadPlayerList(players, this));
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
                break;
            case 10: // 步骤11：大小姐选择顺序
                break;
            case 11: // 步骤12：按顺序发公开信（语音先不做），再选择一人发私信（暂时写成连在一起）
                break;
            case 12: // 步骤13：小原原行动
                break;
            case 13: // 步骤14：投票
                break;
            case 14: // 步骤15：公布投票结果
                break;
            case 15: // 步骤16：发表遗言，若被投出去的是仓鼠，可选择行动
                break;
            default:
                // 当天流程结束
                for (PlayerFrame pf : playerFrames) {
                    pf.updateInfo("===== 第 " + day + " 天 结束 =====");
                }
                day++;
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

                // 操作玩家：显示输入提示和输入框
                pf.updateInfo(inputHint);
                pf.showInputArea(); // 显示输入区域
                // 显示玩家列表（供选择）
                switch (roleType) {
                    case COOKGIRL:
                        pf.updateInfo(PlayerListUtil.getOtherPlayerList(players, pf.getPlayer()));
                        break;
                    case GUARD:
                        pf.updateInfo(PlayerListUtil.getAlivePlayerList(players));
                        break;
                    case FAIRY:
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    case SNOWWHITE:
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    case HAMSTER:
                        pf.updateInfo(PlayerListUtil.getOtherAlivePlayerList(players, pf.getPlayer()));
                        break;
                    case LITTLEYUAN:
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
                // 可添加随机分配地点的逻辑
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
        pf.getInputPanel().setVisible(false); // 隐藏输入区域

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
            // 目前狼人私聊逻辑在WerewolfAction中处理
            werewolfAction.handleChatInput(playerFrames,input);
        }

        // 推进到下一步
        currentStep++;
        updateCurrentProcess();
        processNextStep();
    }

    // 其他流程方法实现...

    // 检查游戏是否结束
    /*
     * public boolean checkGameOver() {
     * // 统计存活狼人数量
     * long aliveWolves = players.stream()
     * .filter(p -> p.isAlive() && p.getCamp() == Camp.WEREWOLF)
     * .count();
     * 
     * // 统计存活好人数量
     * long aliveGood = players.stream()
     * .filter(p -> p.isAlive() && p.getCamp() == Camp.GOOD)
     * .count();
     * 
     * // 狼人全灭，好人赢
     * if (aliveWolves == 0) {
     * return true;
     * }
     * 
     * // 好人只剩一个(除小原原外)等狼人赢的情况
     * // ...
     * 
     * return false;
     * }
     */

    // getters
    public List<Player> getPlayers() {
        return players;
    }

    public List<PlayerFrame> getPlayerFrames() {
        return playerFrames;
    }

    public boolean getIsProcessingLocationSelection() {
        return isProcessingLocationSelection;
    }

    public PlayerFrame getCurrentWaitingFrame() {
        return currentWaitingFrame;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getCurrentDay() {
        return day;
    }

    public void updateCurrentProcess() {
        for (PlayerFrame playerFrame : playerFrames) {
            playerFrame.updateInfo("currentStep: " + String.valueOf(currentStep));
        }
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
    // 记录准备状态的方法
    /*
     * public void addReadyPlayer() {
     * if (readyCount < players.size()) {
     * readyCount++;
     * }
     * }
     * public boolean allPlayersReady() {
     * return readyCount == players.size();
     * }
     * public int getReadyCount() {
     * return readyCount;
     * }
     */

}