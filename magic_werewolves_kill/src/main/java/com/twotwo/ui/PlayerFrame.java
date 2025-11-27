package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.event.*;

import com.twotwo.logic.*;
import com.twotwo.model.*;
import com.twotwo.util.*;

public class PlayerFrame extends JFrame {
    private Player player;
    private Game game;
    private JTextArea infoArea; // 显示信息
    private JScrollPane scrollPane; // 滚动面板
    private JPanel southContainer; // 下方容器面板
    private JPanel skillPanel; // 技能按钮面板
    private JButton skillBtn; // 技能按钮
    private String skillName = "技能"; // 技能名称
    private JPanel inputPanel; // 输入区域面板
    private JTextField inputField; // 输入数字的文本框
    private JButton confirmBtn; // 确认按钮
    private JPanel cancelPanel; // 取消按钮面板
    private JButton cancelBtn; // 取消按钮
    private boolean isInputReady = false; // 标记输入是否完成
    private CountdownUtil countdownUtil = new CountdownUtil(); // 倒计时工具,每个界面一个实例
    // 其他UI组件...

    public PlayerFrame(Player player, Game game) {
        this.player = player;
        this.game = game;
        switch (this.player.getRole()) {
            case HAMSTER:
                this.skillName = "自爆";
                break;
            case WITCH:
                this.skillName = "光波";
                break;
            case LADY:
                this.skillName = "激光";
                break;
            case DOLLMAKER:
                this.skillName = "复活";
                break;
            default:
                break;
        }
        initUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 允许关闭窗口
    }

    private void initUI() {
        // 获取阵营中文
        String campStr = player.getCamp() == Camp.CampType.GOOD ? "好人" : "狼人";
        // 设置标题为 "中文角色名（阵营）"
        setTitle(player.getName() + "（" + campStr + "）");

        setSize(360, 240);
        setLayout(new BorderLayout());

        // 信息显示区域（不可编辑）
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        scrollPane = new JScrollPane(infoArea);
        add(scrollPane, BorderLayout.CENTER);

        // 技能按钮与输入区域的组合面板
        southContainer = new JPanel(new BorderLayout()); // 垂直布局容器

        // 技能按钮面板（放在输入区域上方）
        skillPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        skillBtn = new JButton(skillName);
        skillBtn.addActionListener(e -> {
            // 按钮点击时，调用 Game 类的方法处理技能使用
            game.useSkill(player);
        });
        skillPanel.add(skillBtn);
        southContainer.add(skillPanel, BorderLayout.NORTH); // 添加到组合面板的上方

        // 输入区域面板
        inputPanel = new JPanel(new FlowLayout());

        // 文本框
        inputField = new JTextField(10);

        // 确认按钮
        confirmBtn = new JButton("确认");
        EnterKeyUtil.pressEnterKey(this, confirmBtn);
        // 确认按钮点击事件：提交输入
        confirmBtn.addActionListener(e -> onConfirmInput(e));
        inputPanel.add(new JLabel("请输入："));

        // 取消面板与按钮
        cancelPanel = new JPanel(new FlowLayout());
        cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> onCancelInput(e));
        cancelPanel.add(cancelBtn);

        // 添加组件到输入面板
        inputPanel.add(inputField);
        inputPanel.add(confirmBtn);
        inputPanel.add(cancelPanel);

        // 为 inputPanel 添加组件监听器
        inputPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // 延迟确保UI组件已完成渲染后再请求焦点
                SwingUtilities.invokeLater(() -> {
                    // 强制请求焦点
                    inputField.requestFocusInWindow();
                    // 确保输入框可聚焦
                    inputField.setFocusable(true);
                    // 显示光标
                    inputField.requestFocus();
                });
            }
        });

        southContainer.add(inputPanel, BorderLayout.SOUTH); // 添加到组合面板的下方

        add(southContainer, BorderLayout.SOUTH); // 将组合面板添加到窗口的南部区域

        // 初始隐藏
        southContainer.setVisible(false);
        skillPanel.setVisible(false);
        inputPanel.setVisible(false);
        cancelPanel.setVisible(false);

        // 设置窗口图标
        URL iconUrl = DataUtil.iconUrl;
        IconUtil.setWindowIcon(this, iconUrl);

        setResizable(false); // 暂时禁止调整窗口大小
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 允许关闭窗口，不影响其他窗口
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 确认输入的处理逻辑
    private void onConfirmInput(ActionEvent e) {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            // 通知游戏逻辑：当前玩家已完成输入
            game.onPlayerInputCompleted(this, input);
            // 清空输入框，隐藏输入区域
            inputField.setText("");
            // inputPanel.setVisible(false); // 不是所有输入都需要隐藏输入框
            isInputReady = true;
        } else {
            updateInfo("请输入有效数字！");
        }
    }

    // 取消输入的处理逻辑
    private void onCancelInput(ActionEvent e) {
        // 通知游戏逻辑：当前玩家取消输入
        game.onPlayerInputCancelled(this);
        // 清空输入框，隐藏输入区域
        inputField.setText("");
        inputPanel.setVisible(false); // 隐藏输入区域
        isInputReady = true;
    }

    // 显示输入区域（不包括取消按钮）
    public void showInputArea() {
        SwingUtilities.invokeLater(() -> {
            southContainer.setVisible(true);
            inputPanel.setVisible(true);
            cancelPanel.setVisible(false);
            inputField.setText(""); // 清空输入框
            isInputReady = false;
            inputField.requestFocusInWindow(); // 请求焦点
        });
    }

    public void showCompleteInputArea() {
        SwingUtilities.invokeLater(() -> {
            southContainer.setVisible(true);
            inputPanel.setVisible(true);
            cancelPanel.setVisible(true);
            inputField.setText("");
            isInputReady = false;
            inputField.requestFocusInWindow();
        });
    }

    // 关闭输入区域
    public void hideInputArea() {
        if (inputPanel.isVisible()) {
            SwingUtilities.invokeLater(() -> {
                inputField.setText("");
                inputPanel.setVisible(false);
                isInputReady = true;
            });
        }
    }

    // 更新窗口信息
    public void updateInfo(String info) {
        infoArea.append(info + "\n");
    }

    // getters
    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public boolean isInputReady() {
        return isInputReady;
    }

    public JTextArea getInfoArea() {
        return infoArea;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JPanel getSouthContainer() {
        return southContainer;
    }

    public JTextField getInputField() {
        return inputField;
    }

    public JPanel getInputPanel() {
        return inputPanel;
    }

    public CountdownUtil getCountdownUtil() {
        return countdownUtil;
    }

    public JPanel getSkillPanel() {
        return skillPanel;
    }

    public JButton getSkillBtn() {
        return skillBtn;
    }

    public JPanel getCancelPanel() {
        return cancelPanel;
    }

    public JButton getCancelBtn() {
        return cancelBtn;
    }
}
