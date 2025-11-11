package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.event.ActionEvent;

import com.twotwo.logic.*;
import com.twotwo.model.*;
import com.twotwo.util.*;

public class PlayerFrame extends JFrame {
    private Player player;
    private Game game;
    private JTextArea infoArea; // 显示信息
    private JTextField inputField; // 输入数字的文本框
    private JPanel inputPanel; // 输入区域面板
    private JButton confirmBtn; // 确认按钮
    private boolean isInputReady = false; // 标记输入是否完成
    private JScrollPane scrollPane;
    // 其他UI组件...

    public PlayerFrame(Player player, Game game) {
        this.player = player;
        this.game = game;
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

        // 输入区域（底部）：文本框 + 确认按钮
        inputPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(5); // 只能输入数字，长度5
        confirmBtn = new JButton("确认");
        EnterKeyUtil.pressEnterKey(this, confirmBtn);
        // 确认按钮点击事件：提交输入
        confirmBtn.addActionListener(e -> onConfirmInput(e));
        inputPanel.add(new JLabel("请输入："));
        inputPanel.add(inputField);
        inputPanel.add(confirmBtn);
        add(inputPanel, BorderLayout.SOUTH);

        // 初始隐藏输入区域（需要时再显示）
        inputPanel.setVisible(false);

        // 设置窗口图标
        URL iconUrl = DataUtil.iconUrl;
        IconUtil.setWindowIcon(this, iconUrl);

        setResizable(false); // 暂时禁止调整窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            inputPanel.setVisible(false);
            isInputReady = true;
        } else {
            updateInfo("请输入有效数字！");
        }
    }

    // 显示输入区域（需要玩家操作时调用）
    public void showInputArea() {
        SwingUtilities.invokeLater(() -> {
            inputPanel.setVisible(true);
            inputField.requestFocus(); // 聚焦到输入框
            isInputReady = false;
        });
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

    public JPanel getInputPanel() {
        return inputPanel;
    }
    
    public JTextArea getInfoArea() {
        return infoArea;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
